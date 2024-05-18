package com.example.notificationapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.notificationapp.models.Model_tenant;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UpdateTenant extends AppCompatActivity {
    private EditText editTextNom, editTextPrenom, editTextPrix, editTextNumero,
            editTextTypeMaison, editTextDebutLoca, editTextCaution, editTextAvance;
    int incr;
    private TextView TextCommune;
    String id;
    DatabaseReference databaseReference;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Spinner spinnerSites;
    ImageView retour4;
    String idAdmin;
    private Button btnValider;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_tenant);

        sharedPreferences = getSharedPreferences("Admin", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        idAdmin = sharedPreferences.getString("id", "");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("localites").child(idAdmin);

        spinnerSites = findViewById(R.id.spinnerSites1);
        editTextNom = findViewById(R.id.editTextNom);
        editTextPrenom = findViewById(R.id.editTextPrenom);
        editTextPrix = findViewById(R.id.editTextPrix);
        editTextNumero = findViewById(R.id.editTextNumero);
        TextCommune = findViewById(R.id.TextCommune);
        editTextTypeMaison = findViewById(R.id.editTextTypeMaison);
        editTextDebutLoca = findViewById(R.id.editTextDebutLoca);
        editTextCaution = findViewById(R.id.editTextCaution);
        editTextAvance = findViewById(R.id.editTextAvance);
        retour4 = findViewById(R.id.retour4);

        retour4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UpdateTenant.this, List_of_tenants.class));
                finish();
            }
        });
        Intent intent = getIntent();
        editTextNom.setText(intent.getStringExtra("nom"));
        editTextPrenom.setText(intent.getStringExtra("prenom"));
        editTextPrix.setText(intent.getStringExtra("prix"));
        editTextNumero.setText(intent.getStringExtra("numero"));
        TextCommune.setText(intent.getStringExtra("localite"));
        editTextTypeMaison.setText(intent.getStringExtra("type_de_maison"));
        editTextDebutLoca.setText(intent.getStringExtra("debut_de_loca"));
        editTextCaution.setText(intent.getStringExtra("caution"));
        editTextAvance.setText(intent.getStringExtra("avance"));
         id = intent.getStringExtra("id");
        btnValider = findViewById(R.id.btnValider);
        String[] sitesArray ={"Abobo","Adjamé","Treichville","Marcory","Cocody","Korhogo","Bouaké","Yakro"};
        ArrayAdapter<String> spinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sitesArray);
        spinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSites.setAdapter(spinner);

        int updateSpinner =spinner.getPosition(TextCommune.getText().toString());
        spinnerSites.setSelection(updateSpinner);

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
                Update(nom,prenom,prix,numero,commune,typeMaison,debutLoca,caution,avance);
            }
        });
    }

    private void Update(String nom, String prenom, String prix, String numero, String commune, String typeMaison, String debutLoca, String caution, String avance) {
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
        DatabaseReference locataireReference = databaseReference.child(commune).child(id);

        Model_tenant locataireMaj = new Model_tenant(id,idAdmin, nom, prenom,prix, numero, commune, typeMaison, debutLoca, caution, avance,"impayé", finalFormattedDate,"123456789");
        locataireReference.setValue(locataireMaj);
        startActivity(new Intent(UpdateTenant.this, List_of_tenants.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        incr++;
        if (incr == 1) {
            Intent intent = new Intent(this, List_of_tenants.class);
            startActivity(intent);
            finish();
        }
    }
}