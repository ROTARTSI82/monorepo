package io.github.jgame.crypto;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.math.BigInteger;

import static org.testng.Assert.*;

public class RSATest {
    private RSA testRSA;

    @BeforeSuite
    public void setUp() {
        testRSA = new RSA(512);
    }

    @Test
    public void testRawEncrypt() {
        BigInteger maxInt = testRSA.getModulus().subtract(new BigInteger("1"));
        BigInteger cipherText = testRSA.rawEncrypt(maxInt);
        assertEquals(testRSA.rawDecrypt(cipherText), maxInt);

        assertThrows(IllegalArgumentException.class, new ThrowingRunnable() {
            @Override
            public void run() throws IllegalArgumentException {
                testRSA.rawEncrypt(testRSA.getModulus());
            }
        });
    }
}