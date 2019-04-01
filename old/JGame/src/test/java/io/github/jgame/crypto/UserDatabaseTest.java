package io.github.jgame.crypto;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class UserDatabaseTest {
    private UserDatabase users;
    private final String account = "TestAccount";
    private final String otheraccount = "NonExistantAccount";
    private final char[] passcode = "Lorem Ipsum".toCharArray();
    private final char[] otherpass = "incorrectPasscode".toCharArray();

    @BeforeSuite
    public void setUp() throws Exception {
        users = new UserDatabase();
    }

    @Test
    public void testCreateAccount() throws Exception {
        assertTrue(users.createAccount(account, passcode));
        assertTrue(users.accountExists(account));
        assertFalse(users.createAccount(account, otherpass));
    }

    @Test(dependsOnMethods = {"testCreateAccount"})
    public void testVerifyPassword() throws Exception {
        assertTrue(users.verifyPassword(account, passcode));
        assertFalse(users.verifyPassword(account, otherpass));

        assertFalse(users.verifyPassword(otheraccount, otherpass));
        assertFalse(users.verifyPassword(otheraccount, passcode));
    }

    @Test(dependsOnMethods = {"testCreateAccount"})
    public void testUserData() throws Exception {
        assertFalse(users.setUserData(account, otherpass, "someData"));

        assertTrue(users.setUserData(account, passcode, "Test data"));
        assertNull(users.getUserData(account, otherpass));

        String get = (String) users.getUserData(account, passcode);
        assertEquals(get, "Test data");

        assertNull(users.getUserData(otheraccount, otherpass));
        assertNull(users.getUserData(otheraccount, passcode));
    }

    @Test(dependsOnMethods = {"testUserData", "testVerifyPassword"})
    public void testDeleteAccount() throws Exception {
        assertFalse(users.deleteAccount(account, otherpass));
        assertTrue(users.accountExists(account));

        assertTrue(users.deleteAccount(account, passcode));
        assertFalse(users.accountExists(account));

        assertFalse(users.accountExists(otheraccount));
        assertFalse(users.deleteAccount(otheraccount, otherpass));
    }
}