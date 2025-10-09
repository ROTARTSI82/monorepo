/**
 * @author Grant Yang
 * @version 2025.10.03
 * CSE 123 BC
 * P0: Ciphers
 * TA: Benoit Le
 */

 /**
  * Implements a simple Caesar shift cipher, shifting every character
  * of the encodable range by a fixed offset using modular arithmetic.
  */
public class CaesarShift extends Substitution {

    /**
     * Constructs a Caesar shift cipher from a shift amount. All input characters
     * are shifted by `shift` ASCII code points, with modular arithmetic used to
     * wrap values that are shifted outside of [Cipher.MIN_CHAR, Cipher.MAX_CHAR]
     * back into the encodable range.
     * 
     * @param shift A non-negative number of letters to shift by.
     * @throws IllegalArgumentException If the shift offset is negative.
     */
    public CaesarShift(int shift) {
        super();
        if (shift < 0)
            throw new IllegalArgumentException("CaesarShift called with negative shift");
        shift = shift % Cipher.TOTAL_CHARS;

        String encoding = "";
        for (int i = 0; i < Cipher.TOTAL_CHARS; i++)
            encoding += (char) (Cipher.MIN_CHAR + (shift + i) % Cipher.TOTAL_CHARS);
        setEncoding(encoding);
    }
}