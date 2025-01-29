package com.example.notificationapp.models;

public class Message {
    String id;
    String message;
    String date;
    String heurre;
    public Message() {
    }

    public Message(String id, String message, String date, String heurre) {
        this.id = id;
        this.message = message;
        this.date = date;
        this.heurre = heurre;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHeurre() {
        return heurre;
    }

    public void setHeurre(String heurre) {
        this.heurre = heurre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
