package com.example.notificationapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.widget.TextView;

public class AlertPaiement extends Dialog {
    TextView btnCancel,titre,message;
    @SuppressLint("MissingInflatedId")
    public AlertPaiement(Activity activity) {
        super(activity, androidx.appcompat.R.style.Base_Theme_AppCompat_Dialog_Alert);
        setContentView(R.layout.alerte_paiement);

        // Récupérer le bouton "Annuler" et ajouter un écouteur de clic
        btnCancel = findViewById(R.id.btnCancel);
        titre = findViewById(R.id.titre);
        message = findViewById(R.id.message);
    }
    public TextView getRetour() {
        return btnCancel;
    }
    public void setCancelText(String text) {
        btnCancel.setText(text);
    }

    // Méthode pour définir le texte du titre
    public void setTitreText(String text) {
        titre.setText(text);
    }

    // Méthode pour définir le texte du message
    public void setMessageText(String text) {
        message.setText(text);
    }
}
