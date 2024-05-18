package com.example.notificationapp.models;

import java.util.List;

public class Annonce {
    private String id;
    private String nom;
    private String prenom;
    private String ville;
    private String numero;
    private String titre;
    private String description;
    private String date;
    private List<String> imageUris;

    public Annonce() {
    }

    public Annonce(String id, String nom, String prenom, String ville, String numero, String titre, String description, String date, List<String> imageUris) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.ville = ville;
        this.numero = numero;
        this.titre = titre;
        this.description = description;
        this.date = date;
        this.imageUris = imageUris;
    }

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

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getImageUris() {
        return imageUris;
    }

    public void setImageUris(List<String> imageUris) {
        this.imageUris = imageUris;
    }
}
