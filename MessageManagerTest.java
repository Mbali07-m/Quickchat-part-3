/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

package com.mycompany.quickchat;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Part 3 unit tests - uses the Part 3 test data (messages 1 - 5).
 *
 * @author Student
 */
public class MessageManagerTest {

    // ===== Part 3 test data =====
    private static final String RECIPIENT_1 = "+27834557896";
    private static final String MESSAGE_1 = "Did you get the cake?";          // Sent

    private static final String RECIPIENT_2 = "+27838884567";
    private static final String MESSAGE_2 =
            "Where are you? You are late! I have asked you to be on time.";  // Stored

    private static final String RECIPIENT_3 = "+27834484567";
    private static final String MESSAGE_3 = "Yohoooo, I am at your gate.";    // Disregard

    private static final String DEVELOPER_4 = "0838884567";
    private static final String MESSAGE_4 = "It is dinner time!";             // Sent

    private static final String RECIPIENT_5 = "+27838884567";
    private static final String MESSAGE_5 = "Ok, I am leaving without you.";  // Stored

    // A new JSON file in a temporary folder for every test,
    // so the tests never change your real stored_messages.json
    @TempDir
    Path tempDir;

    private String jsonFile;
    private MessageManager manager;

    private Message message1;
    private Message message2;
    private Message message3;
    private Message message4;
    private Message message5;

    @BeforeEach
    public void setUp() {

        jsonFile = tempDir.resolve("stored_messages.json").toString();
        manager = new MessageManager(jsonFile);

        message1 = new Message(0, RECIPIENT_1, MESSAGE_1);
        message2 = new Message(1, RECIPIENT_2, MESSAGE_2);
        message3 = new Message(2, RECIPIENT_3, MESSAGE_3);
        // Message 4 uses 0838884567 as its message ID (from the test data)
        message4 = new Message("0838884567", 3, DEVELOPER_4, MESSAGE_4);
        message5 = new Message(4, RECIPIENT_5, MESSAGE_5);
    }

    // Send / store / disregard the messages using their flags
    // and put them in the arrays (the same way the app does).
    private void populateArrays(int numberOfMessages) {

        Message[] messages = {message1, message2, message3, message4, message5};
        String[] choices = {"1", "2", "0", "1", "2"};   // Sent, Stored, Disregard, Sent, Stored

        for (int i = 0; i < numberOfMessages; i++) {
            messages[i].sendMessage(choices[i]);
            assertTrue(manager.addMessage(messages[i]));
        }
    }

    // =========================================
    // ARRAYS
    // =========================================

    @Test
    public void testSentMessagesArrayCorrectlyPopulated() {

        populateArrays(4);

        String[] sent = manager.getSentMessageTexts();

        assertEquals(2, sent.length);
        assertEquals("Did you get the cake?", sent[0]);
        assertEquals("It is dinner time!", sent[1]);
    }

    @Test
    public void testDisregardedMessagesArrayCorrectlyPopulated() {

        populateArrays(5);

        assertArrayEquals(new String[]{MESSAGE_3},
                manager.getDisregardedMessageTexts());
    }

    @Test
    public void testStoredMessagesArrayCorrectlyPopulated() {

        populateArrays(5);

        assertArrayEquals(new String[]{MESSAGE_2, MESSAGE_5},
                manager.getStoredMessageTexts());
    }

    // The stored messages are read back from the JSON file into an array
    @Test
    public void testStoredMessagesReadFromJsonFile() {

        populateArrays(5);

        MessageManager newManager = new MessageManager(jsonFile);

        assertEquals(2, newManager.loadStoredMessages());
        assertArrayEquals(new String[]{MESSAGE_2, MESSAGE_5},
                newManager.getStoredMessageTexts());

        Message fromFile = newManager.getStoredMessages()[0];
        assertEquals(message2.getMessageId(), fromFile.getMessageId());
        assertEquals(message2.getMessageHash(), fromFile.getMessageHash());
        assertEquals(RECIPIENT_2, fromFile.getRecipient());
    }

    @Test
    public void testMessageHashArrayCorrectlyPopulated() {

        populateArrays(5);

        assertArrayEquals(new String[]{
            message1.getMessageHash(), message2.getMessageHash(),
            message3.getMessageHash(), message4.getMessageHash(),
            message5.getMessageHash()
        }, manager.getMessageHashes());
    }

    @Test
    public void testMessageIdArrayCorrectlyPopulated() {

        populateArrays(5);

        String[] ids = manager.getMessageIDs();

        assertEquals(5, ids.length);
        assertEquals("0838884567", ids[3]);
    }

    // =========================================
    // LONGEST MESSAGE
    // =========================================

    @Test
    public void testDisplayLongestMessage() {

        populateArrays(4);

        assertEquals("Where are you? You are late! I have asked you to be on time.",
                manager.getLongestStoredMessage());
    }

    // =========================================
    // SEARCH
    // =========================================

    @Test
    public void testSearchForMessageId() {

        populateArrays(4);

        Message found = manager.searchByMessageId("0838884567");

        assertNotNull(found);
        assertEquals("It is dinner time!", found.getMessageText());
        assertEquals("Recipient: 0838884567\nMessage: It is dinner time!",
                manager.displayMessageById("0838884567"));
    }

    @Test
    public void testSearchForUnknownMessageId() {

        populateArrays(5);

        assertEquals("Message ID not found.",
                manager.displayMessageById("1111111111"));
    }

    @Test
    public void testSearchMessagesForRecipient() {

        populateArrays(5);

        assertArrayEquals(new String[]{
            "Where are you? You are late! I have asked you to be on time.",
            "Ok, I am leaving without you."
        }, manager.searchByRecipient("+27838884567"));
    }

    // =========================================
    // DELETE
    // =========================================

    @Test
    public void testDeleteMessageUsingHash() {

        populateArrays(5);

        assertEquals("Message: \"Where are you? You are late! I have asked you "
                + "to be on time.\" successfully deleted.",
                manager.deleteByHash(message2.getMessageHash()));

        // It is gone from the arrays ...
        assertArrayEquals(new String[]{MESSAGE_5}, manager.getStoredMessageTexts());
        assertEquals(4, manager.getMessageHashes().length);

        // ... and from the JSON file
        MessageManager newManager = new MessageManager(jsonFile);
        newManager.loadStoredMessages();
        assertArrayEquals(new String[]{MESSAGE_5}, newManager.getStoredMessageTexts());
    }

    @Test
    public void testDeleteUnknownHash() {

        populateArrays(5);

        assertEquals("Message hash not found.", manager.deleteByHash("00:0:NOTHERE"));
    }

    // =========================================
    // REPORT
    // =========================================

    @Test
    public void testDisplayReport() {

        populateArrays(5);

        String expected = "========== SENT MESSAGES REPORT ==========\n"
                + "Message 1\n"
                + "Message ID: " + message1.getMessageId() + "\n"
                + "Message Hash: " + message1.getMessageHash() + "\n"
                + "Recipient: " + RECIPIENT_1 + "\n"
                + "Message: " + MESSAGE_1 + "\n"
                + "----------------------------------------\n"
                + "Message 2\n"
                + "Message ID: 0838884567\n"
                + "Message Hash: " + message4.getMessageHash() + "\n"
                + "Recipient: " + DEVELOPER_4 + "\n"
                + "Message: " + MESSAGE_4 + "\n"
                + "----------------------------------------";

        assertEquals(expected, manager.displaySentReport());
    }

    @Test
    public void testDisplaySenderAndRecipientOfStoredMessages() {

        message2.setSender("Kyle Smith");
        message5.setSender("Kyle Smith");
        populateArrays(5);

        assertEquals("1) Sender: Kyle Smith | Recipient: +27838884567\n"
                + "2) Sender: Kyle Smith | Recipient: +27838884567",
                manager.displaySenderAndRecipients());
    }
}
