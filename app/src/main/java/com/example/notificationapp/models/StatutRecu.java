package com.example.notificationapp.models;

public class StatutRecu {
    String id;
    String idProprie;
    String idLoca;
    String statut;
    String date;
    String heure;
    String somme;

    public StatutRecu() {
    }

    public StatutRecu(String id, String idProprie, String idLoca, String statut, String date, String heure, String somme) {
        this.id = id;
        this.idProprie = idProprie;
        this.idLoca = idLoca;
        this.statut = statut;
        this.date = date;
        this.heure = heure;
        this.somme = somme;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdProprie() {
        return idProprie;
    }

    public void setIdProprie(String idProprie) {
        this.idProprie = idProprie;
    }

    public String getIdLoca() {
        return idLoca;
    }

    public void setIdLoca(String idLoca) {
        this.idLoca = idLoca;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

    public String getSomme() {
        return somme;
    }

    public void setSomme(String somme) {
        this.somme = somme;
    }
}
