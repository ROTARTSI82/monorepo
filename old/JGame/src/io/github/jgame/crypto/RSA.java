package io.github.jgame.crypto;

import groovy.transform.NotYetImplemented;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Modified version of code <a href="https://introcs.cs.princeton.edu/java/99crypto/RSA.java.html">this code</a>
 * that addresses the known bugs.
 * <p>
 * Copyright © 2000–2017, Robert Sedgewick and Kevin Wayne.
 * Last updated: Fri Oct 20 14:12:12 EDT 2017.
 * <p>
 * Generate an N-bit public and private RSA key and use to encrypt
 * and decrypt a random message.
 */

public class RSA implements Serializable {
    // TODO: Make these values transient?
    private final static BigInteger one = new BigInteger("1");
    private final static SecureRandom random = new SecureRandom();

    private int nBits;

    private BigInteger privateKey;
    private BigInteger publicKey;
    private BigInteger modulus;

    private BigInteger phi;
    private BigInteger p;
    private BigInteger q;

    /**
     * Keep generating public and private keys until the modulus is exactly N bits
     * and the {@code gcd(phi, publicKey) != 1}
     *
     * @param N number of bits for the keys.
     */
    public RSA(int N) {
        nBits = N;
        recalculateKey();
        // Calculates keys until the gcd is 1 and the bitLength is exactly N
        while (phi.gcd(publicKey).compareTo(one) != 0 || modulus.bitLength() != nBits) {
            recalculateKey();
        }
    }

    private void recalculateKey() {
        p = BigInteger.probablePrime(nBits / 2, random);
        q = BigInteger.probablePrime(nBits / 2, random);
        phi = (p.subtract(one)).multiply(q.subtract(one));

        modulus = p.multiply(q);
        publicKey = new BigInteger("65537");     // common value in practice = 2^16 + 1
        privateKey = publicKey.modInverse(phi);
    }

    /**
     * Encrypt a BigInteger that is smaller than modulus using the public key.
     *
     * @param message BigInteger to encrypt
     * @return encrypted BigInteger
     */
    BigInteger rawEncrypt(BigInteger message) {
        if (message.compareTo(modulus) >= 0) {
            throw new IllegalArgumentException("Message is larger than (or equals) modulus");
        }
        return message.modPow(publicKey, modulus);
    }

    /**
     * All integers to be encrypted must be smaller than this value.
     *
     * @return Modulus
     */
    BigInteger getModulus() {
        return modulus;
    }

    /**
     * Decrypt an encrypted BigInteger using the private key.
     *
     * @param encrypted BigInteger to decrypt
     * @return decrypted Biginteger
     */
    BigInteger rawDecrypt(BigInteger encrypted) {
        return encrypted.modPow(privateKey, modulus);
    }

    // TODO: implement these!

    /**
     * Not implemented YET!
     * <p>
     * Split the msg into {@code chunkSize} chucks and evaluate the hex. Then, use {@link #rawEncrypt(BigInteger)}
     * to encrypt each BigInteger.
     *
     * @param msg       String message to encrypt
     * @param chunkSize How big each chunk should be.
     * @return encrypted BigInteger array
     */
    @NotYetImplemented
    BigInteger[] encryptString(String msg, int chunkSize) {
        return new BigInteger[]{one};
    }

    /**
     * Not implemented YET!
     * <p>
     * Decrypt all the BigIntegers using {@link #rawDecrypt(BigInteger)} and turn them into hex and interpret
     * the hex as ascii text.
     *
     * @param encrypted Encrypted message
     * @return Decrepted string
     */
    @NotYetImplemented
    String decryptString(BigInteger[] encrypted) {
        return "";
    }

    public String toString() {
        return String.format("RSA<private=%s public=%s mod=%s bits=%s>", privateKey, publicKey, modulus, nBits);
    }
}