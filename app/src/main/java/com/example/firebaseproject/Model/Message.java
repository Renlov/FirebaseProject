package com.example.firebaseproject.Model;

public class Message {

    private String name;
    private String text;
    private String imageUrl;
    private String sender;
    private String recipient;

    public Message() {
    }

    public Message(String name, String text, String imageUrl, String sender, String recipient) {
        this.name = name;
        this.text = text;
        this.imageUrl = imageUrl;
        this.sender = sender;
        this.recipient = recipient;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
