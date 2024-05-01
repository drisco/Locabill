package com.example.notificationapp.models;

public class Model_tenant {
    private String id;
    private String idProprie;
    private String nom;
    private String prenom;
    private String prix;
    private String numero;
    private String localite;
    private String type_de_maison;
    private String debut_de_loca;
    private String caution;
    private String avance;
    private String statut;
    private String date;

    public Model_tenant() {
    }

    public Model_tenant(String id,String idProprie,String nom, String prenom, String prix,String numero,String localite, String type_de_maison, String debut_de_loca, String caution, String avance,String statut, String date) {
        this.id = id;
        this.idProprie = idProprie;
        this.nom = nom;
        this.prenom = prenom;
        this.prix = prix;
        this.numero = numero;
        this.localite = localite;
        this.type_de_maison = type_de_maison;
        this.debut_de_loca = debut_de_loca;
        this.caution = caution;
        this.avance = avance;
        this.statut = statut;
        this.date = date;
    }

    public String getIdProprie() {
        return idProprie;
    }

    public void setIdProprie(String idProprie) {
        this.idProprie = idProprie;
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

    public String getPrix() {
        return prix;
    }

    public void setPrix(String prix) {
        this.prix = prix;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getLocalite() {
        return localite;
    }

    public void setLocalite(String localite) {
        this.localite = localite;
    }

    public String getType_de_maison() {
        return type_de_maison;
    }

    public void setType_de_maison(String type_de_maison) {
        this.type_de_maison = type_de_maison;
    }

    public String getDebut_de_loca() {
        return debut_de_loca;
    }

    public void setDebut_de_loca(String debut_de_loca) {
        this.debut_de_loca = debut_de_loca;
    }

    public String getCaution() {
        return caution;
    }

    public void setCaution(String caution) {
        this.caution = caution;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
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

    @Override
    public String toString() {
        return "Model_tenant{" +
                "id='" + id + '\'' +
                ", idProprie='" + idProprie + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", prix='" + prix + '\'' +
                ", numero='" + numero + '\'' +
                ", localite='" + localite + '\'' +
                ", type_de_maison='" + type_de_maison + '\'' +
                ", debut_de_loca='" + debut_de_loca + '\'' +
                ", caution='" + caution + '\'' +
                ", avance='" + avance + '\'' +
                ", statut='" + statut + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
