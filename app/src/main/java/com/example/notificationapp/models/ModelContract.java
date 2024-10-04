package com.example.notificationapp.models;

public class ModelContract {
    private String id;            // ID de l'utilisateur
    private String contrat;       // Texte du contrat (OCR)
    private String signatureUrl;
    private Boolean estSigne;// URL de l'image de la signature

    // Constructeur par défaut requis pour Firebase
    public ModelContract() {}

    // Constructeur avec les paramètres
    public ModelContract(String id, String contrat, String signatureUrl, Boolean estSigne) {
        this.id = id;
        this.contrat = contrat;
        this.signatureUrl = signatureUrl;
        this.estSigne = estSigne;
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContrat() {
        return contrat;
    }

    public void setContrat(String contrat) {
        this.contrat = contrat;
    }

    public String getSignatureUrl() {
        return signatureUrl;
    }

    public void setSignatureUrl(String signatureUrl) {
        this.signatureUrl = signatureUrl;
    }

    public Boolean getEstSigne() {
        return estSigne;
    }

    public void setEstSigne(Boolean estSigne) {
        this.estSigne = estSigne;
    }
}

