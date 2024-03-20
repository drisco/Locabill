package com.example.notificationapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notificationapp.models.Model_tenant;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Creat_new_tenant extends AppCompatActivity {

    private EditText editTextNom, editTextPrenom, editTextPrix, editTextNumero,
            editTextTypeMaison, editTextDebutLoca, editTextCaution, editTextAvance;
    private TextView TextCommune;
    DatabaseReference databaseReference;
    Spinner spinnerSites;
    PopusCostum popusCostum;
    ImageView retour3;
    int incr;
    String idAdmin;
    private Button btnValider;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_new_tenant);

         databaseReference = FirebaseDatabase.getInstance().getReference().child("localites");

        spinnerSites = findViewById(R.id.spinnerSites);
        editTextNom = findViewById(R.id.editTextNom);
        editTextPrenom = findViewById(R.id.editTextPrenom);
        editTextPrix = findViewById(R.id.editTextPrix);
        editTextNumero = findViewById(R.id.editTextNumero);
        TextCommune = findViewById(R.id.TextCommune);
        editTextTypeMaison = findViewById(R.id.editTextTypeMaison);
        editTextDebutLoca = findViewById(R.id.editTextDebutLoca);
        editTextCaution = findViewById(R.id.editTextCaution);
        editTextAvance = findViewById(R.id.editTextAvance);
        retour3 = findViewById(R.id.retour3);


        SharedPreferences donnes = getSharedPreferences("Admin", Context.MODE_PRIVATE);
        idAdmin = donnes.getString("id", "");
        retour3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Creat_new_tenant.this, MainActivity.class));
                finish();
            }
        });

        String[] sitesArray ={"Abobo","Adjamé","Treichville","Marcory","Cocody","Korhogo","Bouaké","Yakro"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sitesArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSites.setAdapter(adapter);

        spinnerSites.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Mettre à jour le texte du TextView avec la valeur sélectionnée du Spinner
                TextCommune.setText((String) parentView.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Ne rien faire ici si rien n'est sélectionné
            }
        });


        btnValider = findViewById(R.id.btnValider);
        btnValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nom = editTextNom.getText().toString();
                String prenom = editTextPrenom.getText().toString();
                String prix = editTextPrix.getText().toString();
                String numero = editTextNumero.getText().toString();
                String commune = TextCommune.getText().toString(); // Assurez-vous que TextCommune est de type TextView
                String typeMaison = editTextTypeMaison.getText().toString();
                String debutLoca = editTextDebutLoca.getText().toString();
                String caution = editTextCaution.getText().toString();
                String avance = editTextAvance.getText().toString();
                if (nom.isEmpty() || prenom.isEmpty()||prix.isEmpty()||numero.isEmpty()||commune.isEmpty()||typeMaison.isEmpty()
                ||debutLoca.isEmpty()||caution.isEmpty()||avance.isEmpty()){
                    Toast.makeText(Creat_new_tenant.this, "Vérifiez les champs", Toast.LENGTH_SHORT).show();
                }else {
                    New_tenant(nom,prenom,prix,numero,commune,typeMaison,debutLoca,caution,avance);
                }
            }
        });
    }

    private void New_tenant(String nom, String prenom, String prix, String numero, String commune, String typeMaison, String debutLoca, String caution, String avance) {

        LocalDate currentDate = null;
        String formattedDate = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentDate = LocalDate.now();
        }
        DateTimeFormatter dateFormatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dateFormatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy");
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formattedDate = currentDate.format(dateFormatter);
        }
        String finalFormattedDate = formattedDate;
        popusCostum = new PopusCostum(this);
        popusCostum.setCancelable(false);
        popusCostum.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        DatabaseReference localiteReference = databaseReference.child(idAdmin).child(commune).push();
        String nouvelId = localiteReference.getKey();

        Model_tenant nouveauLocataire = new Model_tenant(nouvelId, nom, prenom, prix, numero,commune, typeMaison, debutLoca, caution, avance, finalFormattedDate);
        popusCostum.getRetour().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            popusCostum.cancel();
            }
        });
        popusCostum.getComf().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                localiteReference.setValue(nouveauLocataire);
                startActivity(new Intent(Creat_new_tenant.this,List_of_tenants.class));
                finish();
            }
        });
        popusCostum.show();
    }
    @Override
    public void onBackPressed() {
        incr++;
        if (incr==1){
            super.onBackPressed();
            startActivity(new Intent(Creat_new_tenant.this,MainActivity.class));
            finish();
        }
    }
}