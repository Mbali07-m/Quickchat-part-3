/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.quickchat;

import java.util.regex.Pattern;

/**
 *
 * @author Student
 */
public class Login {

    private final String firstName;
    private final String lastName;
    private final String username;
    private final String password;
    private final String cellphoneNumber;

    // Constructor
    public Login(String firstName, String lastName, String username,
                 String password, String cellphoneNumber) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.cellphoneNumber = cellphoneNumber;
    }

    // Check username
    // Username must contain an underscore
    // Username must be no more than 5 characters long
    public boolean checkUserName() {

        return username != null
                && username.contains("_")
                && username.length() <= 5;
    }

    // Check password complexity
    // Password must:
    // - contain at least 8 characters
    // - contain a capital letter
    // - contain a number
    // - contain a special character
    //
    // FIX: each look-ahead now starts with ".*" so the capital letter,
    // number and special character can appear ANYWHERE in the password.
    // Before, the look-aheads all had to match the very first character,
    // which meant no password could ever pass.
    public boolean checkPasswordComplexity() {

        String passwordPattern =
                "^(?=.*[A-Z])(?=.*[0-9])(?=.*[^a-zA-Z0-9]).{8,}$";

        return password != null
                && Pattern.matches(passwordPattern, password);
    }

    // Check cellphone number
    // South African international format:
    // +27 followed by 9 digits
    public boolean checkCellPhoneNumber() {

        String cellphonePattern = "^\\+27[0-9]{9}$";

        return cellphoneNumber != null
                && Pattern.matches(cellphonePattern, cellphoneNumber);
    }

    // Register user
    public String registerUser() {

        if (!checkUserName()) {

            return "Username is not correctly formatted; please ensure "
                    + "that your username contains an underscore and is "
                    + "no more than five characters in length.";
        }

        if (!checkPasswordComplexity()) {

            return "Password is not correctly formatted; please ensure "
                    + "that the password contains at least eight characters, "
                    + "a capital letter, a number, and a special character.";
        }

        if (!checkCellPhoneNumber()) {

            return "Cell number is incorrectly formatted or does not "
                    + "contain an international code; please correct the "
                    + "number and try again.";
        }

        return "Registration successful.";
    }

    // Check login details
    public boolean loginUser(String enteredUsername,
                             String enteredPassword) {

        return username != null && password != null
                && username.equals(enteredUsername)
                && password.equals(enteredPassword);
    }

    // Return login status
    public String returnLoginStatus(String enteredUsername,
                                    String enteredPassword) {

        if (loginUser(enteredUsername, enteredPassword)) {

            return "Welcome " + firstName + " " + lastName
                    + ", it is great to see you again.";

        } else {

            return "Username or password incorrect, please try again.";
        }
    }
}
