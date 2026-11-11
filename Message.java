/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.quickchat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Student
 */
public class Message {

    private static final Random RANDOM = new Random();

    // File that stored messages are saved to (in the project folder)
    public static final String STORED_MESSAGES_FILE = "stored_messages.json";

    // Flags that show what happened to a message
    public static final String FLAG_SENT = "Sent";
    public static final String FLAG_STORED = "Stored";
    public static final String FLAG_DISREGARDED = "Disregard";

    private static int totalMessagesSent = 0;

    private final String messageId;
    private final int messageNumber;
    private final String recipient;
    private final String messageText;
    private final String messageHash;

    private String sender = "";
    private String flag = "";

    // Constructor - a random message ID is generated
    public Message(int messageNumber, String recipient,
                   String messageText) {

        this(generateMessageId(), messageNumber, recipient, messageText);
    }

    // Constructor - uses a message ID that already exists
    // (used when stored messages are read back from the JSON file)
    public Message(String messageId, int messageNumber, String recipient,
                   String messageText) {

        this.messageId = messageId;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;

        this.messageHash = createMessageHash(
                messageId, messageNumber, messageText);
    }

    // Generate a random ten-digit message ID.
    // The ID is built with string manipulation - one random
    // digit is added to the end of the string on every pass of the loop.
    private static String generateMessageId() {

        StringBuilder id = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            id.append(RANDOM.nextInt(10));
        }

        return id.toString();
    }

    // Check that the message ID is not more than ten characters
    public boolean checkMessageID() {

        return messageId != null
                && !messageId.isEmpty()
                && messageId.length() <= 10;
    }

    // Check that the message does not exceed 250 characters
    public static boolean checkMessageLength(String message) {

        return message != null && message.length() <= 250;
    }

    // Return the length result as text
    public static String validateMessageLength(String message) {

        if (message == null || message.trim().isEmpty()) {
            return "Please enter a message.";
        }

        if (checkMessageLength(message)) {
            return "Message ready to send.";
        }

        int extra = message.length() - 250;

        return "Message exceeds 250 characters by " + extra
                + ", please reduce size.";
    }

    // Check recipient cellphone number
    // South African international format: +27 followed by 9 digits
    public static boolean checkRecipientCell(String recipient) {

        return recipient != null
                && recipient.matches("^\\+27[0-9]{9}$");
    }

    // Return the cellphone result as text
    public static String validateRecipientCell(String recipient) {

        if (checkRecipientCell(recipient)) {
            return "Cell phone number successfully captured.";
        }

        return "Cell phone number is incorrectly formatted or does not "
                + "contain an international code. Please correct the "
                + "number and try again.";
    }

    // Generate the message hash
    // Format: first two ID digits:message number:first word + last word
    // Example: 00:0:HITONIGHT
    // Punctuation is removed before the words are taken.
    public static String createMessageHash(String messageId,
                                           int messageNumber,
                                           String messageText) {

        if (messageId == null || messageText == null) {
            return "";
        }

        // Remove everything that is not a letter, number or space
        String cleanText = messageText.replaceAll("[^a-zA-Z0-9\\s]", "").trim();

        if (cleanText.isEmpty()) {
            return "";
        }

        String[] words = cleanText.split("\\s+");

        String firstWord = words[0];
        String lastWord = words[words.length - 1];

        String firstTwoDigits = messageId.length() >= 2
                ? messageId.substring(0, 2)
                : messageId;

        return (firstTwoDigits + ":" + messageNumber + ":"
                + firstWord + lastWord).toUpperCase();
    }

    // Return message ID
    public String getMessageId() {
        return messageId;
    }

    // Return message number
    public int getMessageNumber() {
        return messageNumber;
    }

    // Return recipient
    public String getRecipient() {
        return recipient;
    }

    // Return message text
    public String getMessageText() {
        return messageText;
    }

    // Return message hash
    public String getMessageHash() {
        return messageHash;
    }

    // Return the name of the person who wrote the message
    public String getSender() {
        return sender;
    }

    // Set the name of the person who wrote the message
    public void setSender(String sender) {
        this.sender = sender == null ? "" : sender;
    }

    // Return Sent, Stored or Disregard
    public String getFlag() {
        return flag;
    }

    // Return total messages successfully sent
    public static int getTotalMessagesSent() {
        return totalMessagesSent;
    }

    // Details shown to the user after a message is sent
    public String getMessageDetails() {

        return "Message ID: " + messageId
                + "\nMessage Hash: " + messageHash
                + "\nRecipient: " + recipient
                + "\nMessage: " + messageText;
    }

    // Send, discard or store a message.
    // The message is flagged so the MessageManager knows which
    // array it belongs in.
    public String sendMessage(String choice) {

        if (choice == null) {
            return "Invalid option.";
        }

        switch (choice.trim()) {

            case "1":
                flag = FLAG_SENT;
                totalMessagesSent++;
                return "Message successfully sent.";

            case "0":
                flag = FLAG_DISREGARDED;
                return "Press 0 to delete message.";

            case "2":
                flag = FLAG_STORED;
                return "Message successfully stored.";

            default:
                return "Invalid option.";
        }
    }

    // =========================================
    // JSON
    // =========================================

    // Turn this message into a JSON object
    public JSONObject toJson() {

        JSONObject json = new JSONObject();
        json.put("messageId", messageId);
        json.put("messageNumber", messageNumber);
        json.put("sender", sender);
        json.put("recipient", recipient);
        json.put("message", messageText);
        json.put("messageHash", messageHash);
        return json;
    }

    // Build a message from a JSON object read from the file
    public static Message fromJson(JSONObject json) {

        Message message = new Message(
                json.getString("messageId"),
                json.getInt("messageNumber"),
                json.getString("recipient"),
                json.getString("message"));

        message.setSender(json.optString("sender", ""));
        message.flag = FLAG_STORED;
        return message;
    }

    // Add this message to the end of the JSON file.
    // If the file does not exist yet, it is created.
    public boolean storeAsJson(String fileName) {

        try {
            JSONArray messages = readJsonArray(fileName);
            messages.put(toJson());
            writeJsonArray(fileName, messages);
            return true;

        } catch (IOException | JSONException e) {
            System.out.println("Error saving message: " + e.getMessage());
            return false;
        }
    }

    // Read the JSON file into a JSONArray.
    // Returns an empty array when the file does not exist yet.
    //
    // Reference: Leary, S. (2024) JSON-java (org.json). Available at:
    // https://github.com/stleary/JSON-java (Accessed: 11 November 2026).
    public static JSONArray readJsonArray(String fileName) throws IOException {

        Path path = Paths.get(fileName);

        if (!Files.exists(path)) {
            return new JSONArray();
        }

        String content = new String(Files.readAllBytes(path),
                StandardCharsets.UTF_8).trim();

        if (content.isEmpty()) {
            return new JSONArray();
        }

        return new JSONArray(content);
    }

    // Write a JSONArray to the file (4 spaces indentation so it is readable)
    public static void writeJsonArray(String fileName, JSONArray messages)
            throws IOException {

        Files.write(Paths.get(fileName),
                messages.toString(4).getBytes(StandardCharsets.UTF_8));
    }
}
