package com.example.notificationapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PopusCostum extends Dialog {

    private TextView retour,comf;

    public PopusCostum(Activity activity){
        super(activity, androidx.appcompat.R.style.Base_Theme_AppCompat_Dialog_Alert);
        setContentView(R.layout.costumer_popup);
        retour = findViewById(R.id.retour);
        comf = findViewById(R.id.conf);
    }


    public TextView getRetour() {
        return retour;
    }

    public TextView getComf() {
        return comf;
    }


    public void build(){
        show();

    }
}
