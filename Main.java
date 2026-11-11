/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.quickchat;

import java.util.Scanner;

/**
 *
 * @author Student
 */
public class Main {

    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.println("================================");
            System.out.println("     REGISTRATION AND LOGIN");
            System.out.println("================================");

            System.out.print("Enter your first name: ");
            String firstName = scanner.nextLine();

            System.out.print("Enter your last name: ");
            String lastName = scanner.nextLine();

            System.out.print("Enter your username: ");
            String username = scanner.nextLine();

            System.out.print("Enter your password: ");
            String password = scanner.nextLine();

            System.out.print("Enter your cellphone number (+27XXXXXXXXX): ");
            String cellphone = scanner.nextLine();

            Login user = new Login(
                    firstName, lastName, username,
                    password, cellphone);

            System.out.println();
            System.out.println(user.registerUser());

            // Only allow login when registration is valid.
            if (!user.checkUserName()
                    || !user.checkPasswordComplexity()
                    || !user.checkCellPhoneNumber()) {
                return;
            }

            System.out.println("\n========== LOGIN ==========");

            System.out.print("Enter your username: ");
            String enteredUsername = scanner.nextLine();

            System.out.print("Enter your password: ");
            String enteredPassword = scanner.nextLine();

            System.out.println(user.returnLoginStatus(enteredUsername, enteredPassword));

            if (!user.loginUser(enteredUsername, enteredPassword)) {
                return;
            }

            System.out.println("\nWelcome to QuickChat.");

            System.out.print("How many messages would you like to send? ");
            int requestedMessages = readMessageCount(scanner);

            // Part 3: every message goes into the arrays in MessageManager.
            // Messages stored in earlier runs are read back from the JSON file.
            MessageManager manager = new MessageManager(Message.STORED_MESSAGES_FILE);
            int loaded = manager.loadStoredMessages();
            System.out.println(loaded + " stored message(s) loaded from "
                    + Message.STORED_MESSAGES_FILE + ".");

            String senderName = firstName + " " + lastName;
            boolean messagesEntered = false;
            boolean quit = false;

            // Keep displaying the menu until the user chooses Quit.
            while (!quit) {

                System.out.println("\n========== QUICKCHAT ==========");
                System.out.println("1) Send Messages");
                System.out.println("2) Show recently sent messages");
                System.out.println("3) Quit");
                System.out.println("4) Stored Messages");
                System.out.print("Choose an option: ");

                String option = scanner.nextLine().trim();

                switch (option) {

                    case "1":

                        if (messagesEntered) {
                            System.out.println(
                                    "You have already entered the "
                                    + requestedMessages + " message(s) you asked for.");
                            break;
                        }

                        // A for loop runs once for every message
                        // the user said they want to send.
                        for (int i = 0; i < requestedMessages; i++) {

                            System.out.println("\n--- Message " + (i + 1)
                                    + " of " + requestedMessages + " ---");

                            String recipient = readRecipient(scanner);
                            String text = readMessageText(scanner);

                            Message message = new Message(i, recipient, text);
                            message.setSender(senderName);

                            System.out.println("Message ID generated: "
                                    + message.getMessageId());

                            String action = readAction(scanner);

                            System.out.println(message.sendMessage(action));

                            // Put the message in the correct array
                            if (!manager.addMessage(message)) {
                                System.out.println("The message could not be saved.");
                            }

                            // Show the message details AFTER it is sent.
                            if ("1".equals(action)) {
                                System.out.println("\n----- MESSAGE DETAILS -----");
                                System.out.println(message.getMessageDetails());
                                System.out.println("---------------------------");
                            }
                        }

                        messagesEntered = true;
                        System.out.println("\nTotal messages sent: "
                                + Message.getTotalMessagesSent());
                        break;

                    case "2":
                        System.out.println(manager.displaySentReport());
                        break;

                    case "3":
                        quit = true;
                        break;

                    case "4":
                        storedMessagesMenu(scanner, manager);
                        break;

                    default:
                        System.out.println(
                                "Invalid option. Please select 1, 2, 3 or 4.");
                }
            }

            System.out.println("\nTotal messages sent: "
                    + manager.getSentMessages().length);
            System.out.println("Goodbye.");
        }
    }

    // Part 3: the "Stored Messages" menu
    private static void storedMessagesMenu(Scanner scanner, MessageManager manager) {

        boolean back = false;

        while (!back) {

            System.out.println("\n========== STORED MESSAGES ==========");
            System.out.println("a) Display the sender and recipient of all stored messages");
            System.out.println("b) Display the longest stored message");
            System.out.println("c) Search for a message ID");
            System.out.println("d) Search for all messages to a recipient");
            System.out.println("e) Delete a message using the message hash");
            System.out.println("f) Display a report of all stored messages");
            System.out.println("x) Back to the main menu");
            System.out.print("Choose an option: ");

            String option = scanner.nextLine().trim().toLowerCase();

            switch (option) {

                case "a":
                    System.out.println(manager.displaySenderAndRecipients());
                    break;

                case "b":
                    System.out.println("Longest stored message: "
                            + manager.getLongestStoredMessage());
                    break;

                case "c":
                    System.out.print("Enter the message ID: ");
                    System.out.println(manager.displayMessageById(scanner.nextLine()));
                    break;

                case "d":
                    System.out.print("Enter the recipient's cell number: ");
                    String[] found = manager.searchByRecipient(scanner.nextLine());

                    if (found.length == 0) {
                        System.out.println("No messages found for that recipient.");
                    }

                    for (int i = 0; i < found.length; i++) {
                        System.out.println((i + 1) + ") " + found[i]);
                    }
                    break;

                case "e":
                    System.out.print("Enter the message hash: ");
                    System.out.println(manager.deleteByHash(scanner.nextLine()));
                    break;

                case "f":
                    System.out.println(manager.displayStoredReport());
                    break;

                case "x":
                    back = true;
                    break;

                default:
                    System.out.println("Invalid option. Please select a, b, c, d, e, f or x.");
            }
        }
    }

    // Keep asking until a valid recipient number is entered.
    private static String readRecipient(Scanner scanner) {

        while (true) {

            System.out.print("Enter the recipient's cell number (+27XXXXXXXXX): ");
            String recipient = scanner.nextLine().trim();

            System.out.println(Message.validateRecipientCell(recipient));

            if (Message.checkRecipientCell(recipient)) {
                return recipient;
            }
        }
    }

    // Keep asking until a message of 250 characters or less is entered.
    private static String readMessageText(Scanner scanner) {

        while (true) {

            System.out.print("Enter your message (maximum 250 characters): ");
            String text = scanner.nextLine();

            System.out.println(Message.validateMessageLength(text));

            if (Message.checkMessageLength(text) && !text.trim().isEmpty()) {
                return text;
            }
        }
    }

    // Keep asking until the user picks 1, 0 or 2.
    private static String readAction(Scanner scanner) {

        while (true) {

            System.out.println("\n1) Send Message");
            System.out.println("0) Disregard Message");
            System.out.println("2) Store Message to send later");
            System.out.print("Choose an option: ");

            String action = scanner.nextLine().trim();

            if ("1".equals(action) || "0".equals(action) || "2".equals(action)) {
                return action;
            }

            System.out.println("Invalid option. Please select 1, 0 or 2.");
        }
    }

    // Read a valid number of messages from the user.
    private static int readMessageCount(Scanner scanner) {

        while (true) {

            String input = scanner.nextLine().trim();

            try {
                int number = Integer.parseInt(input);

                if (number >= 0) {
                    return number;
                }
            } catch (NumberFormatException e) {
                // Display an error below if the input is not a whole number.
            }

            System.out.print("Please enter a whole number (0 or greater): ");
        }
    }
}
