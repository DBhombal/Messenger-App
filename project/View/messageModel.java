package com.example.project.View;

// Message model class
public class messageModel {
    private int senderId;
    private int receiverId;
    private String messageText;
    private byte[] image;

    // Constructor
    public messageModel(int senderId, int receiverId, String messageText) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageText = messageText;
    }

    public messageModel(int senderId, int receiverId, byte[] image)
    {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.image = image;
    }

    // Getter methods
    public int getSenderId() {
        return senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public String getMessageText() {
        return messageText;
    }

    public byte[]  getImage() { return image; }

    // Setter methods
    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
}
