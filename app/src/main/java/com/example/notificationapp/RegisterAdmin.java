package com.example.notificationapp;

import androidx.annotation.NonNull;
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
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notificationapp.fragments.HistoriqueFragment;
import com.example.notificationapp.fragments.PaiementFragment;
import com.example.notificationapp.fragments.RegistLocataireFrament;
import com.example.notificationapp.fragments.RegistProprieFragment;
import com.example.notificationapp.models.Admin;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RegisterAdmin extends AppCompatActivity {
    ImageView retour,traitPay,traitHis;
    private Fragment regisLocataireFragment;
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

        regisLocataireFragment = new RegistLocataireFrament();
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
    public void showLocataireFragment(View view) {
        traitHis.setVisibility(View.GONE);
        traitPay.setVisibility(View.VISIBLE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerA, regisLocataireFragment);
        transaction.commit();
    }
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