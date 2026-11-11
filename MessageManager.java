/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.quickchat;

import java.io.IOException;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Part 3 - keeps every message in arrays and works with the stored
 * messages that are saved in the JSON file.
 *
 * @author Student
 */
public class MessageManager {

    // File the stored messages are saved to and read from
    private final String jsonFile;

    // ===== Arrays (filled while the app runs - nothing is hard-coded) =====
    private Message[] sentMessages = new Message[0];
    private Message[] disregardedMessages = new Message[0];
    private Message[] storedMessages = new Message[0];
    private String[] messageHashes = new String[0];
    private String[] messageIDs = new String[0];

    // Constructor
    public MessageManager(String jsonFile) {
        this.jsonFile = jsonFile;
    }

    // =========================================
    // POPULATING THE ARRAYS
    // =========================================

    // Put a message into the correct array using its flag.
    // Stored messages are also written to the JSON file.
    public boolean addMessage(Message message) {

        if (message == null) {
            return false;
        }

        switch (message.getFlag()) {

            case Message.FLAG_SENT:
                sentMessages = addToArray(sentMessages, message);
                break;

            case Message.FLAG_DISREGARDED:
                disregardedMessages = addToArray(disregardedMessages, message);
                break;

            case Message.FLAG_STORED:
                if (!message.storeAsJson(jsonFile)) {
                    return false;
                }
                storedMessages = addToArray(storedMessages, message);
                break;

            default:
                return false;
        }

        messageHashes = addToArray(messageHashes, message.getMessageHash());
        messageIDs = addToArray(messageIDs, message.getMessageId());
        return true;
    }

    // Read the JSON file into the stored messages array.
    // Returns how many stored messages were loaded.
    public int loadStoredMessages() {

        try {
            JSONArray array = Message.readJsonArray(jsonFile);

            storedMessages = new Message[0];

            for (int i = 0; i < array.length(); i++) {

                Message message = Message.fromJson(array.getJSONObject(i));

                storedMessages = addToArray(storedMessages, message);

                if (indexOf(messageIDs, message.getMessageId()) == -1) {
                    messageHashes = addToArray(messageHashes, message.getMessageHash());
                    messageIDs = addToArray(messageIDs, message.getMessageId());
                }
            }

            return storedMessages.length;

        } catch (IOException | JSONException e) {
            System.out.println("Could not read stored messages: " + e.getMessage());
            return 0;
        }
    }

    // =========================================
    // GETTERS FOR THE ARRAYS
    // =========================================

    public Message[] getSentMessages() {
        return Arrays.copyOf(sentMessages, sentMessages.length);
    }

    public Message[] getDisregardedMessages() {
        return Arrays.copyOf(disregardedMessages, disregardedMessages.length);
    }

    public Message[] getStoredMessages() {
        return Arrays.copyOf(storedMessages, storedMessages.length);
    }

    public String[] getMessageHashes() {
        return Arrays.copyOf(messageHashes, messageHashes.length);
    }

    public String[] getMessageIDs() {
        return Arrays.copyOf(messageIDs, messageIDs.length);
    }

    // Only the text of every sent message
    public String[] getSentMessageTexts() {
        return toTexts(sentMessages);
    }

    // Only the text of every disregarded message
    public String[] getDisregardedMessageTexts() {
        return toTexts(disregardedMessages);
    }

    // Only the text of every stored message
    public String[] getStoredMessageTexts() {
        return toTexts(storedMessages);
    }

    // =========================================
    // STORED MESSAGES MENU (a - f)
    // =========================================

    // a) Display the sender and recipient of all stored messages
    public String displaySenderAndRecipients() {

        if (storedMessages.length == 0) {
            return "There are no stored messages.";
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < storedMessages.length; i++) {

            String sender = storedMessages[i].getSender().isEmpty()
                    ? "Unknown"
                    : storedMessages[i].getSender();

            sb.append(i + 1).append(") Sender: ").append(sender)
              .append(" | Recipient: ").append(storedMessages[i].getRecipient());

            if (i < storedMessages.length - 1) {
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    // b) Return the longest stored message
    public String getLongestStoredMessage() {

        if (storedMessages.length == 0) {
            return "There are no stored messages.";
        }

        Message longest = storedMessages[0];

        for (int i = 1; i < storedMessages.length; i++) {
            if (storedMessages[i].getMessageText().length()
                    > longest.getMessageText().length()) {
                longest = storedMessages[i];
            }
        }

        return longest.getMessageText();
    }

    // c) Search for a message ID (sent, stored and disregarded messages).
    // Returns null when the ID is not found.
    public Message searchByMessageId(String messageId) {

        Message[][] allArrays = {sentMessages, storedMessages, disregardedMessages};

        for (Message[] array : allArrays) {
            for (Message message : array) {
                if (message.getMessageId().equals(messageId.trim())) {
                    return message;
                }
            }
        }

        return null;
    }

    // c) The recipient and message for a message ID, as text
    public String displayMessageById(String messageId) {

        Message message = searchByMessageId(messageId);

        if (message == null) {
            return "Message ID not found.";
        }

        return "Recipient: " + message.getRecipient()
                + "\nMessage: " + message.getMessageText();
    }

    // d) Every sent or stored message for one recipient
    public String[] searchByRecipient(String recipient) {

        String[] results = new String[0];

        Message[][] arraysToSearch = {sentMessages, storedMessages};

        for (Message[] array : arraysToSearch) {
            for (Message message : array) {
                if (message.getRecipient().equals(recipient.trim())) {
                    results = addToArray(results, message.getMessageText());
                }
            }
        }

        return results;
    }

    // e) Delete a stored message using its message hash.
    // The JSON file is updated as well.
    public String deleteByHash(String messageHash) {

        for (int i = 0; i < storedMessages.length; i++) {

            Message message = storedMessages[i];

            if (message.getMessageHash().equalsIgnoreCase(messageHash.trim())) {

                storedMessages = removeFromArray(storedMessages, i);

                int index = indexOf(messageIDs, message.getMessageId());
                if (index != -1) {
                    messageIDs = removeFromArray(messageIDs, index);
                    messageHashes = removeFromArray(messageHashes, index);
                }

                saveStoredMessages();

                return "Message: \"" + message.getMessageText()
                        + "\" successfully deleted.";
            }
        }

        return "Message hash not found.";
    }

    // f) Report with the full details of every stored message
    public String displayStoredReport() {
        return buildReport("STORED MESSAGES REPORT", storedMessages);
    }

    // Report with the full details of every sent message
    public String displaySentReport() {
        return buildReport("SENT MESSAGES REPORT", sentMessages);
    }

    // =========================================
    // HELPERS
    // =========================================

    // Build a report for a list of messages
    private String buildReport(String title, Message[] messages) {

        if (messages.length == 0) {
            return "There are no messages to show.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("========== ").append(title).append(" ==========\n");

        for (int i = 0; i < messages.length; i++) {

            Message m = messages[i];

            sb.append("Message ").append(i + 1).append("\n")
              .append("Message ID: ").append(m.getMessageId()).append("\n")
              .append("Message Hash: ").append(m.getMessageHash()).append("\n")
              .append("Recipient: ").append(m.getRecipient()).append("\n")
              .append("Message: ").append(m.getMessageText()).append("\n")
              .append("----------------------------------------");

            if (i < messages.length - 1) {
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    // Write the stored messages array back to the JSON file
    private void saveStoredMessages() {

        JSONArray array = new JSONArray();

        for (Message message : storedMessages) {
            array.put(message.toJson());
        }

        try {
            Message.writeJsonArray(jsonFile, array);
        } catch (IOException e) {
            System.out.println("Could not update stored messages: " + e.getMessage());
        }
    }

    // Copy the text out of each message
    private static String[] toTexts(Message[] messages) {

        String[] texts = new String[messages.length];

        for (int i = 0; i < messages.length; i++) {
            texts[i] = messages[i].getMessageText();
        }

        return texts;
    }

    // Return a new array that is one bigger, with the item at the end
    private static Message[] addToArray(Message[] array, Message item) {

        Message[] bigger = Arrays.copyOf(array, array.length + 1);
        bigger[array.length] = item;
        return bigger;
    }

    private static String[] addToArray(String[] array, String item) {

        String[] bigger = Arrays.copyOf(array, array.length + 1);
        bigger[array.length] = item;
        return bigger;
    }

    // Return a new array without the item at the given position
    private static Message[] removeFromArray(Message[] array, int index) {

        Message[] smaller = new Message[array.length - 1];

        for (int i = 0, j = 0; i < array.length; i++) {
            if (i != index) {
                smaller[j++] = array[i];
            }
        }

        return smaller;
    }

    private static String[] removeFromArray(String[] array, int index) {

        String[] smaller = new String[array.length - 1];

        for (int i = 0, j = 0; i < array.length; i++) {
            if (i != index) {
                smaller[j++] = array[i];
            }
        }

        return smaller;
    }

    // Position of a value in an array, or -1 if it is not there
    private static int indexOf(String[] array, String value) {

        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) {
                return i;
            }
        }

        return -1;
    }
}
