/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

package com.mycompany.quickchat;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Student
 */
public class MessageTest {

    // ===== Part 2 test data =====
    // Message 1
    private static final String RECIPIENT_1 = "+27718693002";
    private static final String MESSAGE_1 = "Hi Mike, can you join us for dinner tonight";

    // Message 2
    private static final String RECIPIENT_2 = "08575975889";
    private static final String MESSAGE_2 = "Hi Keegan, did you receive the payment?";

    // Builds a String of the given length (works on any Java version)
    private static String textOfLength(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append("a");
        }
        return sb.toString();
    }

    // =========================================
    // MESSAGE LENGTH
    // =========================================

    @Test
    public void testMessageLengthSuccess() {
        assertEquals("Message ready to send.",
                Message.validateMessageLength(MESSAGE_1));
    }

    @Test
    public void testMessageLengthFailure() {
        assertEquals("Message exceeds 250 characters by 10, please reduce size.",
                Message.validateMessageLength(textOfLength(260)));
    }

    @Test
    public void testMessageLengthOf250CharactersIsValid() {
        assertTrue(Message.checkMessageLength(textOfLength(250)));
    }

    @Test
    public void testMessageOver250CharactersIsInvalid() {
        assertFalse(Message.checkMessageLength(textOfLength(251)));
    }

    // =========================================
    // RECIPIENT NUMBER
    // =========================================

    @Test
    public void testRecipientCellSuccess() {
        assertEquals("Cell phone number successfully captured.",
                Message.validateRecipientCell(RECIPIENT_1));
    }

    @Test
    public void testRecipientCellFailure() {
        assertEquals("Cell phone number is incorrectly formatted or does not "
                + "contain an international code. Please correct the "
                + "number and try again.",
                Message.validateRecipientCell(RECIPIENT_2));
    }

    @Test
    public void testCorrectRecipientCellphoneNumber() {
        assertTrue(Message.checkRecipientCell(RECIPIENT_1));
    }

    @Test
    public void testIncorrectRecipientCellphoneNumber() {
        assertFalse(Message.checkRecipientCell(RECIPIENT_2));
    }

    // =========================================
    // MESSAGE HASH
    // =========================================

    // Message 1 test data should give 00:0:HITONIGHT
    @Test
    public void testMessageHashIsCorrect() {
        assertEquals("00:0:HITONIGHT",
                Message.createMessageHash("0012345678", 0, MESSAGE_1));
    }

    // Punctuation must be removed ("payment?" -> "PAYMENT")
    @Test
    public void testMessageHashRemovesPunctuation() {
        assertEquals("00:1:HIPAYMENT",
                Message.createMessageHash("0012345678", 1, MESSAGE_2));
    }

    // Loop through the test messages and check every hash created
    @Test
    public void testMessageHashLoop() {

        String[] recipients = {RECIPIENT_1, RECIPIENT_2};
        String[] texts = {MESSAGE_1, MESSAGE_2};
        String[] expectedWords = {"HITONIGHT", "HIPAYMENT"};

        for (int i = 0; i < texts.length; i++) {

            Message message = new Message(i, recipients[i], texts[i]);

            String expectedHash = message.getMessageId().substring(0, 2)
                    + ":" + i + ":" + expectedWords[i];

            assertEquals(expectedHash, message.getMessageHash());
        }
    }

    // =========================================
    // MESSAGE ID
    // =========================================

    @Test
    public void testMessageIdHasTenDigits() {
        Message message = new Message(0, RECIPIENT_1, MESSAGE_1);

        assertTrue(message.getMessageId().matches("[0-9]{10}"));
    }

    @Test
    public void testCheckMessageID() {
        Message message = new Message(0, RECIPIENT_1, MESSAGE_1);

        assertTrue(message.checkMessageID());
    }

    // =========================================
    // SEND / DISREGARD / STORE
    // =========================================

    @Test
    public void testSendMessageOption() {
        Message message = new Message(0, RECIPIENT_1, MESSAGE_1);

        assertEquals("Message successfully sent.", message.sendMessage("1"));
    }

    @Test
    public void testDisregardMessageOption() {
        Message message = new Message(1, RECIPIENT_2, MESSAGE_2);

        assertEquals("Press 0 to delete message.", message.sendMessage("0"));
    }

    @Test
    public void testStoreMessageOption() {
        Message message = new Message(0, RECIPIENT_1, MESSAGE_1);

        assertEquals("Message successfully stored.", message.sendMessage("2"));
    }

    // The stored message must be written to the JSON file
    @Test
    public void testStoredMessageIsSavedAsJson(@TempDir Path tempDir) throws Exception {
        Message message = new Message(0, RECIPIENT_1, MESSAGE_1);
        String fileName = tempDir.resolve("stored_messages.json").toString();

        assertTrue(message.storeAsJson(fileName));

        File file = new File(fileName);
        assertTrue(file.exists());

        String json = new String(Files.readAllBytes(file.toPath()));
        assertTrue(json.contains(message.getMessageId()));
        assertTrue(json.contains(message.getMessageHash()));
    }

    @Test
    public void testDetailsShownAfterSending() {
        Message message = new Message(0, RECIPIENT_1, MESSAGE_1);

        String details = message.getMessageDetails();

        assertTrue(details.contains("Message ID: " + message.getMessageId()));
        assertTrue(details.contains("Message Hash: " + message.getMessageHash()));
        assertTrue(details.contains("Recipient: " + RECIPIENT_1));
        assertTrue(details.contains("Message: " + MESSAGE_1));
    }
}
