package io.github.jgame.crypto;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.HashMap;

public class UserDatabase implements Serializable {
    private final static SecureRandom random = new SecureRandom();

    private HashMap<String, Byte[]> passwords = new HashMap<>();
    private HashMap<String, Byte[]> salts = new HashMap<>();
    private HashMap<String, Byte[]> userData = new HashMap<>();

    private HashMap<String, IvParameterSpec> ivs = new HashMap<>();

    private Cipher cipher;
    private int saltLen;

    public UserDatabase() throws Exception {
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        saltLen = 16;
    }

    private static byte[] hmac(char[] message, byte[] salt) throws Exception {
        Mac sha512_HMAC = Mac.getInstance("HmacSHA512");
        SecretKeySpec keySpec = new SecretKeySpec(salt, "HmacSHA512");
        sha512_HMAC.init(keySpec);
        return sha512_HMAC.doFinal(String.valueOf(message).getBytes(StandardCharsets.UTF_8));
    }

    private static byte[] getRandomSalt(int len) {
        byte[] ret = new byte[len];
        random.nextBytes(ret);
        return ret;
    }

    private static byte[] toPrimitive(Byte[] bytes) {
        byte[] ret = new byte[bytes.length];

        int j = 0;
        for (Byte b : bytes) {
            ret[j++] = b.byteValue();
        }
        return ret;
    }

    private static Byte[] toWrapper(byte[] bytes) {
        Byte[] ret = new Byte[bytes.length];

        int i = 0;
        for (byte b : bytes) {
            ret[i++] = b;
        }
        return ret;
    }

    public boolean createAccount(String username, char[] password) throws Exception {
        if (accountExists(username)) {
            return false;
        }
        byte[] salt = getRandomSalt(saltLen);
        passwords.put(username, toWrapper(hmac(password, salt)));
        salts.put(username, toWrapper(salt));
        userData.put(username, new Byte[]{});

        byte[] ivBytes = new byte[16];
        random.nextBytes(ivBytes);
        ivs.put(username, new IvParameterSpec(ivBytes));
        return true;
    }

    public boolean verifyPassword(String username, char[] password) throws Exception {
        byte[] expected = toPrimitive(passwords.get(username));
        byte[] actual = hmac(password, toPrimitive(salts.get(username)));
        return Arrays.equals(expected, actual);
    }

    private SecretKeySpec getKey(char[] password, byte[] salt) throws Exception {
        KeySpec spec = new PBEKeySpec(password, salt, 65536, 256); // AES-256
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] key = f.generateSecret(spec).getEncoded();

        return new SecretKeySpec(key, "AES");
    }

    public boolean setUserData(String username, char[] password, Object data) throws Exception {
        if (!verifyPassword(username, password) || !accountExists(username)) {
            return false;
        }
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        ObjectOutputStream serializer = new ObjectOutputStream(bytesOut);
        serializer.writeObject(data);
        serializer.close();
        byte[] rawData = bytesOut.toByteArray();

        cipher.init(Cipher.ENCRYPT_MODE, getKey(password, toPrimitive(salts.get(username))), ivs.get(username));
        userData.put(username, toWrapper(cipher.doFinal(rawData)));
        return true;
    }

    public boolean accountExists(String username) {
        return passwords.containsKey(username);
    }

    public boolean deleteAccount(String username, char[] password) throws Exception {
        if (!verifyPassword(username, password) || !accountExists(username)) {
            return false;
        }
        salts.remove(username);
        passwords.remove(username);
        userData.remove(username);
        ivs.remove(username);
        return true;
    }

    public Object getUserData(String username, char[] password) throws Exception {
        if (!verifyPassword(username, password) || !accountExists(username)) {
            return null;
        }
        byte[] enc = toPrimitive(userData.get(username));
        byte[] salt = toPrimitive(salts.get(username));

        cipher.init(Cipher.DECRYPT_MODE, getKey(password, salt), ivs.get(username));

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(cipher.doFinal(enc)));
        Object o = ois.readObject();
        ois.close();
        return o;
    }
}
