package io.github.jgame.crypto;

import org.testng.annotations.Test;

import java.math.BigInteger;
import java.util.LinkedList;

import static org.testng.Assert.*;

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

        assertThrows(IllegalArgumentException.class, new ThrowingRunnable() {
            @Override
            public void run() throws IllegalArgumentException {
                testRSA.rawEncrypt(testRSA.getModulus());
            }
        });
    }

    @Test(dependsOnMethods = {"testRSA"})
    public void testStringEncrypt() {
        String dummy = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque lobortis elit enim," +
                " id dictum erat porttitor vel. Etiam eget velit pellentesque, faucibus libero at, ultrices eros." +
                " Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla posuere id ligula cursus sagittis." +
                " Nulla ut arcu metus. In nunc magna, semper at est sit amet, hendrerit bibendum velit. Maecenas " +
                "iaculis nunc nec purus consequat, vel cursus eros suscipit. Etiam id quam sed nunc vestibulum dapibus." +
                "Maecenas laoreet efficitur ligula ut aliquam. Praesent faucibus enim posuere, sagittis magna vel, " +
                "mollis tortor. Duis massa nulla, mattis vel efficitur non, fermentum et nulla. Mauris vestibulum dolor" +
                " in tellus fringilla volutpat.\n";
        LinkedList<Integer> alph = RSA.getAlphabet(dummy);

        BigInteger[] enc = testRSA.encryptString(dummy, 8, alph);
        assertEquals(dummy, testRSA.decryptString(enc, alph));

    }
}