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

    private HashMap<String, HashMap<String, Byte[]>> userData = new HashMap<>();

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
            ret[j++] = b;
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
        HashMap<String, Byte[]> data = new HashMap<>();
        data.put("password", toWrapper(hmac(password, salt)));
        data.put("salt", toWrapper(salt));
        data.put("data", new Byte[]{});
        data.put("iv", toWrapper(getRandomSalt(16)));
        userData.put(username, data);
        return true;
    }

    public boolean verifyPassword(String username, char[] password) throws Exception {
        byte[] expected = toPrimitive(userData.get(username).get("password"));
        byte[] actual = hmac(password, toPrimitive(userData.get(username).get("salt")));
        return Arrays.equals(expected, actual);
    }

    private SecretKeySpec getKey(char[] password, byte[] salt) throws Exception {
        KeySpec spec = new PBEKeySpec(password, salt, 65536, 256); // AES-256
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
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

        byte[] salt = toPrimitive(userData.get(username).get("salt"));
        cipher.init(Cipher.ENCRYPT_MODE, getKey(password, salt),
                new IvParameterSpec(toPrimitive(userData.get(username).get("iv"))));
        userData.get(username).put("data", toWrapper(cipher.doFinal(rawData)));
        return true;
    }

    public boolean accountExists(String username) {
        return userData.containsKey(username);
    }

    public boolean deleteAccount(String username, char[] password) throws Exception {
        if (!verifyPassword(username, password) || !accountExists(username)) {
            return false;
        }
        userData.remove(username);
        return true;
    }

    public Object getUserData(String username, char[] password) throws Exception {
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
