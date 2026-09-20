/**
 * @author Grant Yang
 * @version 2025.10.15
 * CSE 123 BC
 * P0: Ciphers
 * TA: Benoit Le
 */

import java.util.List;

/**
 * A MultiCipher is a cipher constructed from composing multiple sub-ciphers together.
 * This class abstracts away having multiple ciphers provide multiple layers of encryption.
 */
public class MultiCipher extends Cipher {

    private final List<Cipher> ciphers;

    /**
     * Constructs a MultiCipher from a list of sub-ciphers, allowing us
     * to compose arbitrarily many ciphers together.
     *  
     * @param ciphers A list of ciphers to apply in sequence for encryption.
     *                The plaintext is fed into the first, and the output of the first cipher
     *                is fed into the next, and so on. The list must not contain any null
     *                elements, and the ciphers in it must not throw exceptions when called.
     *                If this list is empty, MultiCipher reduces to a no-op.
     * @throws IllegalArgumentException If `ciphers` is null.
     */
    public MultiCipher(List<Cipher> ciphers) {
        super();
        if (ciphers == null)
            throw new IllegalArgumentException("null list passed to MultiCipher");
        this.ciphers = ciphers;
    }

    /**
     * Encrypts a string by applying all the sub-ciphers in sequence.
     * @param input The plaintext to encrypt. This string must not be null, and 
     *              it must not contain characters outside the encodable range.
     * @return The resulting encrypted ciphertext. This string
     *         will only contain characters within the encodable range.
     * @throws IllegalArgumentException If the input was null.
     */
    @Override
    public String encrypt(String input) {
        if (input == null)
            throw new IllegalArgumentException("null input to encrypt()");

        for (Cipher cipher : ciphers)
            input = cipher.encrypt(input);
        return input;
    }

    /**
     * Decrypts a string by applying the inverse of all the sub-ciphers.
     * @param input The ciphertext to decrypt. This string must not be null, and 
     *              it must not contain characters outside the encodable range.
     * @return The resulting plaintext. This string will only contain
     *         characters within the encodable range.
     * @throws IllegalArgumentException If the input was null.
     */
    @Override
    public String decrypt(String input) {
        if (input == null)
            throw new IllegalArgumentException("null input to decrypt()");

        for (Cipher cipher : ciphers.reversed())
            input = cipher.decrypt(input);
        return input;
    }
    
}
