package com.example.notificationapp.models;

public class Admin {
    private String id;
    private String userName;
    private String userPrenom;
    private String numeros;
    private String pwd;
    private String date;
    private String abner;

    // Constructeur par défaut requis pour Firebase
    public Admin() {
    }

    public Admin(String id, String userName, String userPrenom, String numeros, String pwd, String date, String abner) {
        this.id = id;
        this.userName = userName;
        this.userPrenom = userPrenom;
        this.numeros = numeros;
        this.pwd = pwd;
        this.date = date;
        this.abner = abner;
    }

    // Getters et setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPrenom() {
        return userPrenom;
    }

    public void setUserPrenom(String userPrenom) {
        this.userPrenom = userPrenom;
    }

    public String getNumeros() {
        return numeros;
    }

    public void setNumeros(String numeros) {
        this.numeros = numeros;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAbner() {
        return abner;
    }

    public void setAbner(String abner) {
        this.abner = abner;
    }
}


