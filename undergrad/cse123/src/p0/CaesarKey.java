/**
 * @author Grant Yang
 * @version 2025.10.03
 * CSE 123 BC
 * P0: Ciphers
 * TA: Benoit Le
 */

/**
 * Implements a keyed Caesar cipher, where a key of length n
 * specifies the mapping for the first n characters of the encodable range,
 * and the rest of the range is filled by the unused characters in order.
 */
public class CaesarKey extends Substitution {


    /**
     * Constructs a keyed Caesar cipher from the specified key.
     * 
     * @param key The key string to use.
     * @throws IllegalArgumentException If the key is null, contains duplicate characters,
     *                                  or contains characters outside of the encodable range.
     */
    CaesarKey(String key) {
        super();
        Substitution.checkCodomainInjective(key);
        String encoding = key;
        for (int c = Cipher.MIN_CHAR; c <= Cipher.MAX_CHAR; c++)
            if (key.indexOf((char) c) == -1)
                encoding += (char) c;

        setEncoding(encoding);
    }
}