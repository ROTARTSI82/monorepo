package io.github.jgame.crypto;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class UserDatabaseTest {
    UserDatabase users;

    @BeforeSuite
    public void setUp() throws Exception {
        users = new UserDatabase();
    }

    @Test
    public void testCreateAccount() throws Exception {
        users.createAccount("TestAccount", "Lorem Ipsum".toCharArray());
        assertTrue(users.accountExists("TestAccount"));
    }

    @Test(dependsOnMethods = {"testCreateAccount"})
    public void testVerifyPassword() throws Exception {
        assertTrue(users.verifyPassword("TestAccount", "Lorem Ipsum".toCharArray()));
        assertTrue(!users.verifyPassword("TestAccount", "incorrectPasscode".toCharArray()));
    }

    @Test(dependsOnMethods = {"testCreateAccount"})
    public void testUserData() throws Exception {
        users.setUserData("TestAccount", "Lorem Ipsum".toCharArray(), "Test data");
        String get = (String) users.getUserData("TestAccount", "Lorem Ipsum".toCharArray());
        assertEquals(get, "Test data");
    }

    @Test(dependsOnMethods = {"testUserData", "testVerifyPassword"})
    public void testDeleteAccount() throws Exception {
        users.deleteAccount("TestAccount", "incorrectPasscode".toCharArray());
        assertTrue(users.accountExists("TestAccount"));

        users.deleteAccount("TestAccount", "Lorem Ipsum".toCharArray());
        assertTrue(!users.accountExists("TestAccount"));
    }
}