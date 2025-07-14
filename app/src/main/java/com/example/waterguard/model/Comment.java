package com.example.waterguard.model;

public class Comment {
    private String text;
    private long timestamp;

    public Comment(String text) {
        this.text = text;
        this.timestamp = System.currentTimeMillis();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
} 