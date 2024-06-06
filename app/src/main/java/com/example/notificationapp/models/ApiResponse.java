package com.example.notificationapp.models;

public class ApiResponse {
    private boolean statut;
    private String token;
    private String message;
    private String url;

    public ApiResponse(boolean statut, String token, String message, String url) {
        this.statut = statut;
        this.token = token;
        this.message = message;
        this.url = url;
    }

    public boolean isStatut() {
        return statut;
    }

    public void setStatut(boolean statut) {
        this.statut = statut;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
