package io.github.jgame.crypto;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class UserDatabaseTest {
    private UserDatabase users;

    @BeforeSuite
    public void setUp() throws Exception {
        users = new UserDatabase();
    }

    @Test
    public void testCreateAccount() throws Exception {
        assertTrue(users.createAccount("TestAccount", "Lorem Ipsum".toCharArray()));
        assertTrue(users.accountExists("TestAccount"));

        assertFalse(users.createAccount("TestAccount", "otherPass".toCharArray()));
    }

    @Test(dependsOnMethods = {"testCreateAccount"})
    public void testVerifyPassword() throws Exception {
        assertTrue(users.verifyPassword("TestAccount", "Lorem Ipsum".toCharArray()));
        assertFalse(users.verifyPassword("TestAccount", "incorrectPasscode".toCharArray()));
    }

    @Test(dependsOnMethods = {"testCreateAccount"})
    public void testUserData() throws Exception {
        assertFalse(users.setUserData("TestAccount", "incorrectPasscode".toCharArray(), "someData"));

        assertTrue(users.setUserData("TestAccount", "Lorem Ipsum".toCharArray(), "Test data"));
        assertNull(users.getUserData("TestAccount", "incorrectPasscode".toCharArray()));
        String get = (String) users.getUserData("TestAccount", "Lorem Ipsum".toCharArray());
        assertEquals(get, "Test data");
    }

    @Test(dependsOnMethods = {"testUserData", "testVerifyPassword"})
    public void testDeleteAccount() throws Exception {
        assertFalse(users.deleteAccount("TestAccount", "incorrectPasscode".toCharArray()));
        assertTrue(users.accountExists("TestAccount"));

        assertTrue(users.deleteAccount("TestAccount", "Lorem Ipsum".toCharArray()));
        assertFalse(users.accountExists("TestAccount"));
    }
}