package com.example.notificationapp;

import java.util.List;

public class PaiementDetails {

    private boolean statut;
    private Data data;
    private String message;

    // Constructeur
    public PaiementDetails(boolean statut, Data data, String message) {
        this.statut = statut;
        this.data = data;
        this.message = message;
    }

    // Getters et Setters
    public boolean isStatut() {
        return statut;
    }

    public void setStatut(boolean statut) {
        this.statut = statut;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Classe interne pour la structure "data"
    public static class Data {
        private String _id;
        private String tokenPay;
        private String numeroSend;
        private String nomclient;
        private List<String> personal_Info;
        private String numeroTransaction;
        private int Montant;
        private int frais;
        private String statut;
        private String return_url;
        private String createdAt;

        // Constructeur
        public Data(String _id, String tokenPay, String numeroSend, String nomclient, List<String> personal_Info, String numeroTransaction, int Montant, int frais, String statut, String return_url, String createdAt) {
            this._id = _id;
            this.tokenPay = tokenPay;
            this.numeroSend = numeroSend;
            this.nomclient = nomclient;
            this.personal_Info = personal_Info;
            this.numeroTransaction = numeroTransaction;
            this.Montant = Montant;
            this.frais = frais;
            this.statut = statut;
            this.return_url = return_url;
            this.createdAt = createdAt;
        }

        // Getters et Setters
        public String getId() {
            return _id;
        }

        public void setId(String _id) {
            this._id = _id;
        }

        public String getTokenPay() {
            return tokenPay;
        }

        public void setTokenPay(String tokenPay) {
            this.tokenPay = tokenPay;
        }

        public String getNumeroSend() {
            return numeroSend;
        }

        public void setNumeroSend(String numeroSend) {
            this.numeroSend = numeroSend;
        }

        public String getNomclient() {
            return nomclient;
        }

        public void setNomclient(String nomclient) {
            this.nomclient = nomclient;
        }

        public List<String> getPersonal_Info() {
            return personal_Info;
        }

        public void setPersonal_Info(List<String> personal_Info) {
            this.personal_Info = personal_Info;
        }

        public String getNumeroTransaction() {
            return numeroTransaction;
        }

        public void setNumeroTransaction(String numeroTransaction) {
            this.numeroTransaction = numeroTransaction;
        }

        public int getMontant() {
            return Montant;
        }

        public void setMontant(int Montant) {
            this.Montant = Montant;
        }

        public int getFrais() {
            return frais;
        }

        public void setFrais(int frais) {
            this.frais = frais;
        }

        public String getStatut() {
            return statut;
        }

        public void setStatut(String statut) {
            this.statut = statut;
        }

        public String getReturn_url() {
            return return_url;
        }

        public void setReturn_url(String return_url) {
            this.return_url = return_url;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }
    }
}

