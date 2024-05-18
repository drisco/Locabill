package com.example.notificationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
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
    PopupRegister popup;
    DatabaseReference databaseReference, databaseReference1;
    String codePinValue;
    @Override
    @SuppressLint("MissingInflatedId")

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);

        sharedPreferences = getSharedPreferences("Admin", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        String idAdmin = sharedPreferences.getString("id", "");
        String nom = sharedPreferences.getString("nom", "");
        String numeroAd = sharedPreferences.getString("numero", "");
        String codepin = sharedPreferences.getString("codepin", "");
        SharedPreferences loca = getSharedPreferences("codeconfirm", Context.MODE_PRIVATE);
        String idloca = loca.getString("id", "");
        String code = loca.getString("codepin", "");


        if (!idAdmin.isEmpty() || !nom.isEmpty()||!numeroAd.isEmpty()){
            if (!codepin.isEmpty()){
                startActivity(new Intent(LoginAdmin.this,Login.class));
                finish();
            }else{
                startActivity(new Intent(LoginAdmin.this,MainActivity.class));
                finish();
            }

        }else {
            if (!idloca.isEmpty()){
                if (!code.isEmpty()){
                    startActivity(new Intent(LoginAdmin.this,Login.class));
                    finish();
                }else {
                    startActivity(new Intent(LoginAdmin.this,EspaceLocataires.class));
                    finish();
                }

            }
        }
        passwordEdt = findViewById(R.id.idEdtPassword);
        numero = findViewById(R.id.idEdtUserNumero);
        register = findViewById(R.id.idTVNewUser);
        btnlogin = findViewById(R.id.idBtnLogin);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Admin");
        databaseReference1 = FirebaseDatabase.getInstance().getReference().child("codepin");
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
                    popup = new PopupRegister(LoginAdmin.this);
                    popup.setCancelable(false);
                    popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    popup.show();
                    login(numero.getText().toString(),passwordEdt.getText().toString());
                    popup.dismiss();
                }
            }
        });
    }

    private void login(String number, String password) {
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
                            popup.dismiss();
                            editor.putString("id", user.getId());
                            editor.putString("nom", user.getUserName());
                            editor.putString("prenom", user.getUserPrenom());
                            editor.putString("numero", user.getNumeros());
                            editor.putString("mdp", user.getPwd());
                            editor.putString("proprie", "proprie");
                            editor.putString("codepin", codePinValue);
                            editor.apply();
                            startActivity(new Intent(LoginAdmin.this,MainActivity.class));
                            finish();
                        } else {
                            popup.dismiss();
                            // Le mot de passe est incorrect, affichez un message d'erreur
                            Toast.makeText(getApplicationContext(), "Mot de passe incorrect.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    popup.dismiss();
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