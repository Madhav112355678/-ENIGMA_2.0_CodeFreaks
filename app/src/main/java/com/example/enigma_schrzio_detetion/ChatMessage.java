package com.example.enigma_schrzio_detetion;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class ChatMessage {
    private String id;
    private String senderId;
    private String senderName;
    private String text;
    private String fileUrl; // For medical reports (PDF/images)
    @ServerTimestamp
    private Date timestamp;

    public ChatMessage() {
        // Required for Firestore
    }

    public ChatMessage(String id, String senderId, String senderName, String text, String fileUrl) {
        this.id = id;
        this.senderId = senderId;
        this.senderName = senderName;
        this.text = text;
        this.fileUrl = fileUrl;
    }

    public String getId() {
        return id;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getText() {
        return text;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
