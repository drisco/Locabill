package com.example.notificationapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.example.notificationapp.fragments.RegistLocataireFrament;
import com.example.notificationapp.fragments.RegistProprieFragment;

public class RegisterAdmin extends AppCompatActivity {
    ImageView retour,traitPay,traitHis;
   // private Fragment regisLocataireFragment;
    private Fragment registProrietFragment;
    int incr;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_admin);
        traitPay =findViewById(R.id.traitPay);
        traitHis =findViewById(R.id.traitHis);
        retour =findViewById(R.id.retour);

        //regisLocataireFragment = new RegistLocataireFrament();
        registProrietFragment = new RegistProprieFragment();
        showPropriétaireFragment(null);
        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterAdmin.this, LoginAdmin.class));
            }
        });
    }
    // Méthode pour afficher le fragment Historique
    public void showPropriétaireFragment(View view) {
        traitPay.setVisibility(View.GONE);
        traitHis.setVisibility(View.VISIBLE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerA, registProrietFragment);
        transaction.commit();
    }

    // Méthode pour afficher le fragment Paiement
    /*public void showLocataireFragment(View view) {
        traitHis.setVisibility(View.GONE);
        traitPay.setVisibility(View.VISIBLE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerA, regisLocataireFragment);
        transaction.commit();
    }*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        incr++;
        if (incr == 1) {
            Intent intent = new Intent(this, LoginAdmin.class);
            startActivity(intent);
            finish();
        }
    }
}