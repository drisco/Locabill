package com.example.notificationapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PopupMoney extends Dialog {
    RelativeLayout wave,mtn,orange,moov;
    ImageView rtour;
    @SuppressLint("MissingInflatedId")
    public PopupMoney(Activity activity) {
        super(activity, androidx.appcompat.R.style.Base_Theme_AppCompat_Dialog_Alert);
        setContentView(R.layout.moneyview);
        // Récupérer le bouton "Annuler" et ajouter un écouteur de clic
        wave = findViewById(R.id.wave);
        mtn = findViewById(R.id.mtn);
        orange = findViewById(R.id.orange);
        moov = findViewById(R.id.moov);
        rtour = findViewById(R.id.cancel);
    }
    public ImageView rtour() {
        return rtour;
    }
    public RelativeLayout wave() {
        return wave;
    }
    public RelativeLayout mtn() {
        return mtn;
    }
    public RelativeLayout orange() {
        return orange;
    }
    public RelativeLayout moov() {
        return moov;
    }
}
