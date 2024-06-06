package com.example.notificationapp.models;

import com.google.gson.annotations.SerializedName;

public class ClientData {
    @SerializedName("totalPrice") private int totalPrice;
    @SerializedName("article") private String articles;
    @SerializedName("personal_Info") private String personalInfos;
    @SerializedName("numeroSend") private String numeroSend;
    @SerializedName("nomclient") private String nomClient;
    @SerializedName("return_url") private String returnUrl;

    // Constructeur, getters et setters

    public ClientData(int totalPrice, String articles, String personalInfos, String numeroSend, String nomClient, String returnUrl) {
        this.totalPrice = totalPrice;
        this.articles = articles;
        this.personalInfos = personalInfos;
        this.numeroSend = numeroSend;
        this.nomClient = nomClient;
        this.returnUrl = returnUrl;
    }

    public ClientData() {
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getArticles() {
        return articles;
    }

    public void setArticles(String articles) {
        this.articles = articles;
    }

    public String getPersonalInfos() {
        return personalInfos;
    }

    public void setPersonalInfos(String personalInfos) {
        this.personalInfos = personalInfos;
    }

    public String getNumeroSend() {
        return numeroSend;
    }

    public void setNumeroSend(String numeroSend) {
        this.numeroSend = numeroSend;
    }

    public String getNomClient() {
        return nomClient;
    }

    public void setNomClient(String nomClient) {
        this.nomClient = nomClient;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    @Override
    public String toString() {
        return "ClientData{" +
                "totalPrice=" + totalPrice +
                ", articles='" + articles + '\'' +
                ", personalInfos='" + personalInfos + '\'' +
                ", numeroSend='" + numeroSend + '\'' +
                ", nomClient='" + nomClient + '\'' +
                ", returnUrl='" + returnUrl + '\'' +
                '}';
    }
}
