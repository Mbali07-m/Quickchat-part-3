/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

package com.mycompany.quickchat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Student
 */
public class LoginTest {

    // Test data from the assignment
    private static final String GOOD_USERNAME = "kyl_1";
    private static final String BAD_USERNAME = "kyle!!!!!!!";
    private static final String GOOD_PASSWORD = "Ch&&sec@ke99!";
    private static final String BAD_PASSWORD = "password";
    private static final String GOOD_CELL = "+27838968976";
    private static final String BAD_CELL = "08966553";

    // =========================================
    // USERNAME TESTS
    // =========================================

    @Test
    public void testUsernameCorrectlyFormatted() {

        Login login = new Login("Kyle", "Smith", GOOD_USERNAME, GOOD_PASSWORD, GOOD_CELL);

        assertEquals(
                "Welcome Kyle Smith, it is great to see you again.",
                login.returnLoginStatus(GOOD_USERNAME, GOOD_PASSWORD));
    }

    @Test
    public void testUsernameIncorrectlyFormatted() {

        Login login = new Login("Kyle", "Smith", BAD_USERNAME, GOOD_PASSWORD, GOOD_CELL);

        assertEquals(
                "Username is not correctly formatted; please ensure "
                + "that your username contains an underscore and is "
                + "no more than five characters in length.",
                login.registerUser());
    }

    @Test
    public void testUsernameCorrectlyFormattedBoolean() {

        Login login = new Login("Kyle", "Smith", GOOD_USERNAME, GOOD_PASSWORD, GOOD_CELL);

        assertTrue(login.checkUserName());
    }

    @Test
    public void testUsernameIncorrectlyFormattedBoolean() {

        Login login = new Login("Kyle", "Smith", BAD_USERNAME, GOOD_PASSWORD, GOOD_CELL);

        assertFalse(login.checkUserName());
    }

    // =========================================
    // PASSWORD TESTS
    // =========================================

    @Test
    public void testPasswordMeetsComplexityRequirements() {

        Login login = new Login("Kyle", "Smith", GOOD_USERNAME, GOOD_PASSWORD, GOOD_CELL);

        assertEquals("Registration successful.", login.registerUser());
    }

    @Test
    public void testPasswordDoesNotMeetComplexityRequirements() {

        Login login = new Login("Kyle", "Smith", GOOD_USERNAME, BAD_PASSWORD, GOOD_CELL);

        assertEquals(
                "Password is not correctly formatted; please ensure "
                + "that the password contains at least eight characters, "
                + "a capital letter, a number, and a special character.",
                login.registerUser());
    }

    @Test
    public void testPasswordComplexityTrue() {

        Login login = new Login("Kyle", "Smith", GOOD_USERNAME, GOOD_PASSWORD, GOOD_CELL);

        assertTrue(login.checkPasswordComplexity());
    }

    @Test
    public void testPasswordComplexityFalse() {

        Login login = new Login("Kyle", "Smith", GOOD_USERNAME, BAD_PASSWORD, GOOD_CELL);

        assertFalse(login.checkPasswordComplexity());
    }

    // Checks the regex fix: capital, number and special character
    // can be anywhere in the password, not only at the start.
    @Test
    public void testPasswordRegexAcceptsCharactersAnywhere() {

        Login login = new Login("Kyle", "Smith", GOOD_USERNAME, "abcdefG1!", GOOD_CELL);

        assertTrue(login.checkPasswordComplexity());
    }

    // =========================================
    // CELLPHONE TESTS
    // =========================================

    @Test
    public void testCellPhoneCorrectlyFormatted() {

        Login login = new Login("Kyle", "Smith", GOOD_USERNAME, GOOD_PASSWORD, GOOD_CELL);

        assertEquals("Registration successful.", login.registerUser());
    }

    @Test
    public void testCellPhoneIncorrectlyFormatted() {

        Login login = new Login("Kyle", "Smith", GOOD_USERNAME, GOOD_PASSWORD, BAD_CELL);

        assertEquals(
                "Cell number is incorrectly formatted or does not "
                + "contain an international code; please correct the "
                + "number and try again.",
                login.registerUser());
    }

    @Test
    public void testCellPhoneCorrectlyFormattedBoolean() {

        Login login = new Login("Kyle", "Smith", GOOD_USERNAME, GOOD_PASSWORD, GOOD_CELL);

        assertTrue(login.checkCellPhoneNumber());
    }

    @Test
    public void testCellPhoneIncorrectlyFormattedBoolean() {

        Login login = new Login("Kyle", "Smith", GOOD_USERNAME, GOOD_PASSWORD, BAD_CELL);

        assertFalse(login.checkCellPhoneNumber());
    }

    // =========================================
    // LOGIN TESTS
    // =========================================

    @Test
    public void testLoginSuccessful() {

        Login login = new Login("Kyle", "Smith", GOOD_USERNAME, GOOD_PASSWORD, GOOD_CELL);

        assertTrue(login.loginUser(GOOD_USERNAME, GOOD_PASSWORD));
    }

    @Test
    public void testLoginFailed() {

        Login login = new Login("Kyle", "Smith", GOOD_USERNAME, GOOD_PASSWORD, GOOD_CELL);

        assertFalse(login.loginUser("wrong", "wrong"));
    }
}
