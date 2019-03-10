package io.github.jgame.crypto;

import io.github.jgame.Constants;
import org.testng.annotations.Test;

import java.math.BigInteger;
import java.util.LinkedList;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

public class RSATest {
    private RSA testRSA;

    @Test
    public void testRSA() {
        testRSA = new RSA(512);
        assertEquals(testRSA.getModulus().bitLength(), 512);
    }

    @Test(dependsOnMethods = {"testRSA"})
    public void testRawEncrypt() {
        BigInteger maxInt = testRSA.getModulus().subtract(new BigInteger("1"));
        BigInteger cipherText = testRSA.rawEncrypt(maxInt);
        assertEquals(testRSA.rawDecrypt(cipherText), maxInt);

        assertThrows(IllegalArgumentException.class, () -> testRSA.rawEncrypt(testRSA.getModulus()));
    }

    @Test(dependsOnMethods = {"testRSA"})
    public void testStringEncrypt() {
        String dummy = Constants.JGameStr.getString("dummyText");
        LinkedList<Integer> alph = RSA.getAlphabet(dummy);

        BigInteger[] enc = testRSA.encryptString(dummy, 8, alph);
        assertEquals(testRSA.decryptString(enc, alph), dummy);

    }
}