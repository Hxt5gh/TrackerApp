package com.hxt5gh.android.tracker.Models;

public class ClientClass {
    String name;
    String mNumber;
    String imageUrl;
    int priority;
    String pushId;

    public ClientClass(String name, String mNumber, String imageUrl, int priority) {
        this.name = name;
        this.mNumber = mNumber;
        this.imageUrl = imageUrl;
        this.priority = priority;

    }

    public ClientClass() {
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getmNumber() {
        return mNumber;
    }

    public void setmNumber(String mNumber) {
        this.mNumber = mNumber;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
