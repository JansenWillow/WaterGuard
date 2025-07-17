package com.example.waterguard.model;

public class Comment {
    public static final int TYPE_VIDEO = 1;
    public static final int TYPE_ANSWER = 2;
    public static final int TYPE_REPLY = 3;

    private String text;
    private long timestamp;
    private int type;
    private boolean isRight;
    private String avatarUrl;

    public Comment(String text, int type, boolean isRight, String avatarUrl) {
        this.text = text;
        this.type = type;
        this.isRight = isRight;
        this.avatarUrl = avatarUrl;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isRight() {
        return isRight;
    }

    public void setRight(boolean right) {
        isRight = right;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}