/**
 * @author Grant Yang
 * @version 2025.10.03
 * CSE 123 BC
 * P0: Ciphers
 * TA: Benoit Le
 */

/**
 * A substituion cipher that simply applies a substitution per-character
 * on a string, using a mapping specified by an automorphism on the encodable range.
 */
public class Substitution extends Cipher {

    private String encoding;

    /**
     * Constructs a substitution cipher without an encoding.
     * The cipher is in an invalid state until setEncoding() is called successfully,
     * and calling encrypt() or decrypt() will result in an error.
     */
    public Substitution() {
        super();
        encoding = null;
    }

    /**
     * Constructs a substitution cipher with the specified mapping, which must
     * be an automorphism on [Cipher.MIN_CHAR, Cipher.MAX_CHAR].
     * 
     * @param encoding A specification for the mapping to use for the cipher, 
     *                 where the nth character specifies the value of the map for
     *                 Cipher.MIN_CHAR + n.
     * @throws IllegalArgumentException If the encoding map is null, or if it is not 
     *                                  an automorphism (i.e. its length is not equal to
     *                                  Cipher.TOTAL_CHARS or it contains duplicate characters or
     *                                  characters outside of [Cipher.MIN_CHAR, Cipher.MAX_CHAR]).
     */
    public Substitution(String encoding) {
        this();
        setEncoding(encoding);
    }

    /**
     * Validates that the mapping specified by the encoding/key string 
     * both has a valid codomain and is injective.
     * The codomain must be a subset of (or equal to) [Cipher.MIN_CHAR, Cipher.MAX_CHAR].
     * 
     * @param encoding A string specifying the mapping, where the character at the nth
     *                 index specifies the value of the map for the nth input value.
     * @throws IllegalArgumentException If the encoding specified is not injective 
     *                                  (i.e. contains duplicate characters) 
     *                                  or maps to a value outside the valid codomain.
     *                                  Also throws if `encoding` is null.
     */
    public static void checkCodomainInjective(String encoding) {
        if (encoding == null)
            throw new IllegalArgumentException("null mapping");
        
        for (int i = 0; i < encoding.length(); i++) {
            char c = encoding.charAt(i);
            int dupIdx = encoding.substring(0, i).indexOf(c);
            if (dupIdx != -1)
                throw new IllegalArgumentException(
                    "mapping not injective: " + c +
                    " appears at both " + dupIdx + " and " + i);
            if (!Cipher.isCharInRange(c))
                throw new IllegalArgumentException("mapping contains out of range char: " + c);
        }
    }

    /**
     * Sets the substitution cipher's encoding map, which must
     * be an automorphism on [Cipher.MIN_CHAR, Cipher.MAX_CHAR].
     * 
     * @param encoding A specification for the mapping to use for the cipher, 
     *                 where the nth character specifies the value of the map for
     *                 Cipher.MIN_CHAR + n.
     * @throws IllegalArgumentException If the encoding map is null, or if it is not 
     *                                  an automorphism (i.e. its length is not equal to
     *                                  Cipher.TOTAL_CHARS or it contains duplicate characters or
     *                                  characters outside of [Cipher.MIN_CHAR, Cipher.MAX_CHAR]).
     */
    public void setEncoding(String encoding) {
        checkCodomainInjective(encoding);
        if (encoding.length() != TOTAL_CHARS)
            throw new IllegalArgumentException(
                "encoding must have the same length as Cipher's encodable range");
        this.encoding = encoding;
    }

    /**
     * Encrypts a string by performing the substitution specified in setEncoding().
     * @param input The plaintext to encrypt. This string must not be null, and 
     *              it must not contain characters outside of the encodable range.
     * @returns The encrypted ciphertext resulting from the substitution. This string
     *          will be of the same length as the input and only contain characters
     *          within the encodable range.
     * @throws IllegalStateException If no encoding was set on this cipher prior to this call.
     * @throws IllegalArgumentException If the input was null or contained out of range characters.
     */
    @Override
    public String encrypt(String input) {
        if (encoding == null)
            throw new IllegalStateException("encoding not set before encrypt()");
        if (input == null)
            throw new IllegalArgumentException("null input to encrypt()");

        String out = "";
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (!Cipher.isCharInRange(c))
                throw new IllegalArgumentException("input contains out of range char: " + c);
            out += encoding.charAt(((int) c) - Cipher.MIN_CHAR);
        }

        return out;
    }

    /**
     * Decrypts a string by performing the inverse of the substitution specified in setEncoding().
     * @param input The ciphertext to decrypt. This string must not be null, and 
     *              it must not contain characters outside of the encodable range.
     * @returns The plaintext resulting from the inverse substitution. This string
     *          will be of the same length as the input and only contain characters
     *          within the encodable range.
     * @throws IllegalStateException If no encoding was set on this cipher prior to this call.
     * @throws IllegalArgumentException If the input was null or contained out of range characters.
     */
    @Override
    public String decrypt(String input) {
        if (encoding == null)
            throw new IllegalStateException("encoding not set before decrypt()");
        if (input == null)
            throw new IllegalArgumentException("null input to decrypt()");

        String out = "";
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (!Cipher.isCharInRange(c))
                throw new IllegalArgumentException("input contains out of range char: " + c);
            out += (char) (Cipher.MIN_CHAR + encoding.indexOf(c));
        }

        return out;
    }

}