package com.example.notificationapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.notificationapp.fragments.HistoriqueFragment;
import com.example.notificationapp.fragments.PaiementFragment;

public class EspaceLocataires extends AppCompatActivity {

    ImageView profil,deconnexion,traitPay,traitHis;
    TextView nomEtPrenom,editer;
    private Fragment historiqueFragment;
    private Fragment paiementFragment;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_espace_locataires);
        profil =findViewById(R.id.profil);
        deconnexion =findViewById(R.id.deconnexion);
        nomEtPrenom =findViewById(R.id.nomEtPrenom);
        editer =findViewById(R.id.editer);
        traitPay =findViewById(R.id.traitPay);
        traitHis =findViewById(R.id.traitHis);
        historiqueFragment = new HistoriqueFragment();
        paiementFragment = new PaiementFragment();
        showHistoriqueFragment(null);
        sharedPreferences = getSharedPreferences("codeconfirm",Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        SharedPreferences donnes =getSharedPreferences("codeconfirm", Context.MODE_PRIVATE);
        String nom= donnes.getString("nom", "");
        String prenom = donnes.getString("prenom", "");
        nomEtPrenom.setText(nom+" "+prenom);

        deconnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.clear();
                editor.apply();
                startActivity(new Intent(EspaceLocataires.this,RegisterAdmin.class));
                finish();
            }
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Méthode pour afficher le fragment Historique
    public void showHistoriqueFragment(View view) {
        traitPay.setVisibility(View.GONE);
        traitHis.setVisibility(View.VISIBLE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, historiqueFragment);
        transaction.commit();
    }

    // Méthode pour afficher le fragment Paiement
    public void showPaiementFragment(View view) {
        traitHis.setVisibility(View.GONE);
        traitPay.setVisibility(View.VISIBLE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, paiementFragment);
        transaction.commit();
    }
}