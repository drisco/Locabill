package com.example.notificationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notificationapp.models.Admin;
import com.example.notificationapp.models.Model_code_pin;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginAdmin extends AppCompatActivity {
    private TextInputEditText numero, passwordEdt;
    TextView btnlogin,register;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    int incr;
    ProgressBar idPBLoading;
    DatabaseReference databaseReference, databaseReference1;
    String codePinValue;
    @Override
    @SuppressLint("MissingInflatedId")

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);

        SharedPreferences donnes = getSharedPreferences("Admin", Context.MODE_PRIVATE);
        String idAdmin = donnes.getString("id", "");
        String nom = donnes.getString("nom", "");
        String numeroAd = donnes.getString("numero", "");
        String codepin = donnes.getString("codepin", "");
        System.out.println("HJEBRJIHRGJNGTENIJTREJGIERIGOI 0001 "+codepin);

        if (!idAdmin.isEmpty() || !nom.isEmpty()||!numeroAd.isEmpty()){

            if (!codepin.isEmpty()){
                startActivity(new Intent(LoginAdmin.this,Login.class));
                finish();
            }else{
                startActivity(new Intent(LoginAdmin.this,MainActivity.class));
                finish();
            }

        }
        passwordEdt = findViewById(R.id.idEdtPassword);
        numero = findViewById(R.id.idEdtUserNumero);
        idPBLoading = findViewById(R.id.idPBLoading);
        register = findViewById(R.id.idTVNewUser);
        btnlogin = findViewById(R.id.idBtnLogin);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Admin");
        databaseReference1 = FirebaseDatabase.getInstance().getReference().child("codepin");
        sharedPreferences = getSharedPreferences("Admin", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginAdmin.this, RegisterAdmin.class));
                finish();
            }
        });
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numero.getText().toString().isEmpty()|| passwordEdt.getText().toString().isEmpty()){

                }else {
                    idPBLoading.setVisibility(View.VISIBLE);
                    login(numero.getText().toString(),passwordEdt.getText().toString());

                }
            }
        });
    }

    private void login(String number, String password) {
        // Récupérez une référence à la base de données Firebase

// Vérifiez si un utilisateur existe avec le numéro de téléphone et le mot de passe fournis
        databaseReference.orderByChild("numeros").equalTo(number).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Vérifiez si dataSnapshot contient des données
                if (dataSnapshot.exists()) {
                    // L'utilisateur existe, parcourir les données pour vérifier le mot de passe
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        Admin user = userSnapshot.getValue(Admin.class);

                        databaseReference1.child(user.getId()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                    Model_code_pin codePin = childSnapshot.getValue(Model_code_pin.class);
                                    if (codePin != null) {
                                        System.out.println("HRGHGRKJHGIUREGJHUERGHJGRJVBGEGRVBFKDBGRJFGKVREJVB 222 "+codePin.getCode());
                                        codePinValue = codePin.getCode();
                                        editor.putString("codepin", codePinValue);
                                        editor.apply();
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                System.out.println("Erreur : " + databaseError.getMessage());
                            }
                        });

                        // Vérifiez si le mot de passe correspond
                        if (user != null && user.getPwd().equals(password)) {
                            idPBLoading.setVisibility(View.GONE);
                            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA "+codePinValue);

                            editor.putString("id", user.getId());
                            editor.putString("nom", user.getUserName());
                            editor.putString("prenom", user.getUserPrenom());
                            editor.putString("numero", user.getNumeros());
                            editor.putString("mdp", user.getPwd());
                            //editor.putString("codepin", codePinValue);
                            editor.apply();
                            startActivity(new Intent(LoginAdmin.this,MainActivity.class));
                            finish();
                        } else {
                            idPBLoading.setVisibility(View.GONE);
                            // Le mot de passe est incorrect, affichez un message d'erreur
                            Toast.makeText(getApplicationContext(), "Mot de passe incorrect.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    idPBLoading.setVisibility(View.GONE);
                    // Aucun utilisateur avec ce numéro de téléphone n'a été trouvé, affichez un message d'erreur
                    Toast.makeText(getApplicationContext(), "Aucun utilisateur trouvé avec ce numéro de téléphone.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }
    @Override
    public void onBackPressed() {
        incr++;
        if (incr==1){
            super.onBackPressed();
            //finish();
            finishAffinity();
        }
    }
}