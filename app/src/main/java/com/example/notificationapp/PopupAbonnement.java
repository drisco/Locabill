package com.example.notificationapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.widget.ImageView;
import android.widget.TextView;

public class PopupAbonnement  extends Dialog {
    TextView btnCancel;
    ImageView dismiss;
    @SuppressLint("MissingInflatedId")
    public PopupAbonnement(Activity activity) {
        super(activity, androidx.appcompat.R.style.Base_Theme_AppCompat_Dialog_Alert);
        setContentView(R.layout.alerte_abonnement);
        // Récupérer le bouton "Annuler" et ajouter un écouteur de clic
        btnCancel = findViewById(R.id.btnCancel);
        dismiss = findViewById(R.id.dismiss);

    }
    public TextView getRetour() {
        return btnCancel;
    }
    public ImageView getDismiss() {
        return dismiss;
    }
    public void setCancelText(String text) {
        btnCancel.setText(text);
    }


}



