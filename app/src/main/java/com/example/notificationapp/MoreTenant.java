package com.example.notificationapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.widget.TextView;

public class MoreTenant extends AppCompatActivity {
    TextView nomTextView,prenomTextView,avanceTextView,cautionTextView,numeroTextView,debutTextView,typeTextView;
    int incr;
    private String nom,prenom,avance,caution,numero,debut,type,prix;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_tenant);
        TextView mTextView = findViewById(R.id.tv1);
        String mString = "Régénerer les sondes";
        SpannableString mSpannableString = new SpannableString(mString);
        mSpannableString.setSpan(new UnderlineSpan(), 0, mSpannableString.length(), 0);
        mTextView.setText(mSpannableString);

         /*nomTextView = findViewById(R.id.nomTextView);
         prenomTextView = findViewById(R.id.prenomTextView);
         avanceTextView = findViewById(R.id.avanceTextView);
         cautionTextView = findViewById(R.id.cautionTextView);
         numeroTextView = findViewById(R.id.numeroTextView);
         numeroTextView = findViewById(R.id.numeroTextView);
         debutTextView = findViewById(R.id.debutTextView);
         typeTextView = findViewById(R.id.typeTextView);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
             nom = bundle.getString("nom");
             prenom = bundle.getString("prenom");
             avance = bundle.getString("avance");
             caution = bundle.getString("caution");
             numero = bundle.getString("numero");
             debut = bundle.getString("debut");
             type = bundle.getString("type");
             prix = bundle.getString("prix");

        }
        nomTextView.setText("Nom : " + nom);

        prenomTextView.setText("Prénom : " + prenom);

        avanceTextView.setText("Avance : " + avance);

        cautionTextView.setText("Caution : " + caution);

        numeroTextView.setText("Numéro : " + numero);

        debutTextView.setText("Début : " + debut);

        typeTextView.setText("Type : " + type);*/
    }
    @Override
    public void onBackPressed() {
        incr++;
        if (incr==1){
            super.onBackPressed();
            startActivity(new Intent(MoreTenant.this,MainActivity.class));
            finish();
        }
    }
}