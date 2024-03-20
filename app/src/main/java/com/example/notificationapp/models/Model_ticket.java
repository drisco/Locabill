package com.example.notificationapp.models;

import java.util.List;

public class Model_ticket {
    private String id;
    private String nom;
    private String prenom;
    private String montant;
    private String numero;
    private String type;
    private String debutLoca;
    private String caution;
    private String avance;
    private String date;

    // Constructeur par défaut nécessaire pour Firebase
    public Model_ticket() {
    }

    // Constructeur avec tous les champs
    public Model_ticket(String id, String nom, String prenom, String montant, String numero, String type, String debutLoca, String caution, String avance, String date) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.montant = montant;
        this.numero = numero;
        this.type = type;
        this.debutLoca = debutLoca;
        this.caution = caution;
        this.avance = avance;
        this.date = date;
    }

    // Getters et setters pour chaque champ
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getMontant() {
        return montant;
    }

    public void setMontant(String montant) {
        this.montant = montant;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDebutLoca() {
        return debutLoca;
    }

    public void setDebutLoca(String debutLoca) {
        this.debutLoca = debutLoca;
    }

    public String getCaution() {
        return caution;
    }

    public void setCaution(String caution) {
        this.caution = caution;
    }

    public String getAvance() {
        return avance;
    }

    public void setAvance(String avance) {
        this.avance = avance;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}


