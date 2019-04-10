package com.rotartsi.jgame.crypto;

import com.rotartsi.jgame.Constants;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.HashMap;

import static com.rotartsi.jgame.Constants.settings;

/**
 * Handles logins and encrypts user data using their passcode.
 */
public class UserDatabase implements Serializable {

    /**
     * Contains the following:
     * <ol>
     * <li>Initialization Vectors (used for key generation to encrypt user data)</li>
     * <li>Salts (Used to hash passwords)</li>
     * <li>Password Hash Digests</li>
     * <li>User Data (Encrypted using the user's passcode, salt, and init vector)</li>
     * </ol>
     */
    private HashMap<String, HashMap<String, Byte[]>> userData = new HashMap<>();

    /**
     * Cipher used to encrypt user data. By default it is AES/CBC/PKCS5Padding
     * (Advanced Encryption Standard with Cipher Block Chaining with PKC5 Padding.)
     */
    private Cipher cipher;

    /**
     * The length of salts used for HMAC512 to hash the passcodes. 16 characters should be enough.
     */
    private int saltLen;

    /**
     * New UserDatabase!
     *
     * @throws NoSuchAlgorithmException Bad encryption algorithm
     * @throws NoSuchPaddingException Bad padding (incompatible or unrecognized)
     */
    public UserDatabase() throws NoSuchAlgorithmException, NoSuchPaddingException {
        cipher = Cipher.getInstance(settings.get("crypto.UserDatabase.cipher"));
        saltLen = 16;
    }

    /**
     * Hash message with the salt. The salt prevents a dictionary attack.
     *
     * @param message Message to hash
     * @param salt    salt
     * @return Hashed message
     *
     * @throws IllegalStateException HMAC may fail
     * @throws NoSuchAlgorithmException Bad HMAC algorithm
     * @throws InvalidKeyException Bad key supplied.
     */
    private static byte[] hmac(char[] message, byte[] salt) throws NoSuchAlgorithmException, InvalidKeyException {
        String hmacAlgorithm = settings.get("crypto.UserDatabase.hmac");
        Mac sha512_HMAC = Mac.getInstance(hmacAlgorithm);
        SecretKeySpec keySpec = new SecretKeySpec(salt, hmacAlgorithm);
        sha512_HMAC.init(keySpec);
        return sha512_HMAC.doFinal(String.valueOf(message).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generate {@code len} random bytes using a {@link SecureRandom} for use as salt or initialization vector
     *
     * @param len Salt len
     * @return {@code} len random bytes
     */
    private static byte[] getRandomSalt(int len) {
        byte[] ret = new byte[len];
        Constants.secureRand.nextBytes(ret);
        return ret;
    }

    /**
     * "Unbox" an array. Convert {@code Byte[]} to {@code byte[]}
     *
     * @param bytes Wrapper array
     * @return Primitive array
     */
    private static byte[] toPrimitive(Byte[] bytes) {
        byte[] ret = new byte[bytes.length];

        int j = 0;
        for (Byte b : bytes) {
            ret[j++] = b;
        }
        return ret;
    }

    /**
     * "Box" an array. Convert {@code byte[]} to {@code Byte[]}
     *
     * @param bytes Primitive array
     * @return Wrapper array
     */
    private static Byte[] toWrapper(byte[] bytes) {
        Byte[] ret = new Byte[bytes.length];

        int i = 0;
        for (byte b : bytes) {
            ret[i++] = b;
        }
        return ret;
    }

    /**
     *
     * Initialize an account with random Initialization Vector and salt.
     * <p>
     * (Will abort if account already exists.)
     *
     * @param username Username to create account for
     * @param password Corresponding passcode
     * @return Returns true if account creation succeeds.
     *
     * @throws NoSuchAlgorithmException No such encryption algorithm
     * @throws InvalidKeyException Invalid encryption key
     */
    public boolean createAccount(String username, char[] password) throws NoSuchAlgorithmException,
            InvalidKeyException {
        if (accountExists(username)) {
            return false;
        }
        byte[] salt = getRandomSalt(saltLen);
        HashMap<String, Byte[]> data = new HashMap<>();
        data.put("password", toWrapper(hmac(password, salt)));
        data.put("salt", toWrapper(salt));
        data.put("data", new Byte[]{});
        data.put("iv", toWrapper(getRandomSalt(16)));  // IV = Initialization Vector
        userData.put(username, data);
        return true;
    }

    /**
     * Check if the password matches the account
     *
     * @param username Account to check
     * @param password passcode
     * @return true if the passcode matches the account.
     *
     * @throws NoSuchAlgorithmException No such encryption algorithm
     * @throws InvalidKeyException Invalid key supplied
     */
    public boolean verifyPassword(String username, char[] password) throws NoSuchAlgorithmException, InvalidKeyException {
        if (!accountExists(username)) {
            return false;
        }
        byte[] expected = toPrimitive(userData.get(username).get("password"));
        byte[] actual = hmac(password, toPrimitive(userData.get(username).get("salt")));
        return Arrays.equals(expected, actual);
    }

    /**
     * Generate a key for encryption from the user's passcode.
     *
     * @param password Passcode
     * @param salt     salt
     * @return SecretKeySpec for encryption
     * @throws NoSuchAlgorithmException bad encryption algorithm
     * @throws InvalidKeySpecException  Bad key!
     */
    private SecretKeySpec getKey(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(password, salt, 65536, 256); // AES-256
        SecretKeyFactory f = SecretKeyFactory.getInstance(
                settings.get("crypto.UserDatabase.keyFactory"));
        byte[] key = f.generateSecret(spec).getEncoded();

        return new SecretKeySpec(key, settings.get("crypto.UserDatabase.cipherAlgorithm"));
    }

    /**
     * Serialize and encrypt the data and store it.
     * - Data is encrypted using key generated with {@link #getKey}
     * Will abort if password doesn't match the given username.
     *
     * @param username Account
     * @param password passcode
     * @param data     data to store
     * @return true if successful
     *
     * @throws IOException Cannot deserialize
     * @throws InvalidKeyException Cannot decrypt (bad key)
     * @throws InvalidAlgorithmParameterException Invalid params for decryption
     * @throws NoSuchAlgorithmException Bad decrypt algorithm
     * @throws InvalidKeySpecException Invalid key!
     * @throws IllegalBlockSizeException Bad block size for block-chaining
     * @throws BadPaddingException Bad padding for decryption!
     */
    public boolean setUserData(String username, char[] password, Object data) throws IOException, InvalidKeyException,
            InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeySpecException,
            IllegalBlockSizeException, BadPaddingException {

        if (!verifyPassword(username, password) || !accountExists(username)) {
            return false;
        }
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        ObjectOutputStream serializer = new ObjectOutputStream(bytesOut);
        serializer.writeObject(data);
        serializer.close();
        byte[] rawData = bytesOut.toByteArray();

        byte[] salt = toPrimitive(userData.get(username).get("salt"));
        cipher.init(Cipher.ENCRYPT_MODE, getKey(password, salt),
                new IvParameterSpec(toPrimitive(userData.get(username).get("iv"))));
        userData.get(username).put("data", toWrapper(cipher.doFinal(rawData)));
        return true;
    }

    /**
     * Check if account exists. No passcode required.
     *
     * @param username account
     * @return true if account exists.
     */
    public boolean accountExists(String username) {
        return userData.containsKey(username);
    }

    /**
     * Delete account if the passcode is correct.
     *
     * @param username Account
     * @param password passcode
     * @return true if account is successfully deleted.
     *
     * @throws NoSuchAlgorithmException Bad algorithm
     * @throws InvalidKeyException Bad key supplied
     */
    public boolean deleteAccount(String username, char[] password) throws NoSuchAlgorithmException,
            InvalidKeyException {
        if (!verifyPassword(username, password) || !accountExists(username)) {
            return false;
        }
        userData.remove(username);
        return true;
    }

    /**
     * Retrieve user data stored using {@link #setUserData(String, char[], Object)} only if the passcode is valid.
     *
     * @param username Account
     * @param password passcode
     * @return Retrieved object. null if passcode verification failed or account doesn't exist.
     *
     * @throws ClassNotFoundException The serialized data is corrupt. (unrecognized class in serial)
     * @throws BadPaddingException Unrecognized padding supplied
     * @throws InvalidKeyException Invalid key!
     * @throws InvalidAlgorithmParameterException Bad params for encrypt!
     * @throws NoSuchAlgorithmException Bad algorithm!
     * @throws InvalidKeySpecException Invalid encrypt key!
     * @throws IOException Cannot serialize!
     * @throws IllegalBlockSizeException Bad block size for block-chaining
     */
    public Object getUserData(String username, char[] password) throws InvalidKeyException,
            InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeySpecException, IOException,
            IllegalBlockSizeException, BadPaddingException, ClassNotFoundException {

        if (!verifyPassword(username, password) || !accountExists(username)) {
            return null;
        }
        byte[] enc = toPrimitive(userData.get(username).get("data"));

        byte[] salt = toPrimitive(userData.get(username).get("salt"));
        cipher.init(Cipher.DECRYPT_MODE, getKey(password, salt),
                new IvParameterSpec(toPrimitive(userData.get(username).get("iv"))));

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(cipher.doFinal(enc)));
        Object o = ois.readObject();
        ois.close();
        return o;
    }
}
