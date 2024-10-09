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
import com.example.notificationapp.models.Model_tenant;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginAdmin extends AppCompatActivity {
    private TextInputEditText numero, passwordEdt;
    TextView btnlogin,register;
    SharedPreferences sharedPreferences,sharedPreferencesLoc;
    SharedPreferences.Editor editor,editorLoc;
    int incr;
    PopupRegister popup;


    DatabaseReference databaseReference, databaseReference1,reference;
    String idAdmin, id,ville,nom,prenom,numeros,somme,caution,avance,debutUsage,type,codeloca,codeLocalId,passwords;

    String codePinValue,codePinId;
    @Override
    @SuppressLint("MissingInflatedId")

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);

        sharedPreferences = getSharedPreferences("Admin", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        sharedPreferencesLoc = getSharedPreferences("codeconfirm", Context.MODE_PRIVATE);
        editorLoc = sharedPreferencesLoc.edit();

        String idAdmin = sharedPreferences.getString("id", "");
        String nom = sharedPreferences.getString("nom", "");
        String numeroAd = sharedPreferences.getString("numero", "");
        String codepin = sharedPreferences.getString("codepin", "");
        String codepinLo = sharedPreferencesLoc.getString("codepin", "");
        String idloca = sharedPreferencesLoc.getString("id", "");

        System.out.println("BCVBVCBVBVXBVBVBVXBVBVXBVBBBBBBXXXXXXXXXXXXXXXXXXXX "+codepinLo);
        /*SharedPreferences loca = getSharedPreferences("codeconfirm", Context.MODE_PRIVATE);
        String idloca = loca.getString("id", "");
        String code = loca.getString("codepin", "");*/


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
                if (!codepinLo.isEmpty()){
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
        reference = FirebaseDatabase.getInstance().getReference().child("localites");

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
                if (!numero.getText().toString().isEmpty()|| !passwordEdt.getText().toString().isEmpty()){
                    popup = new PopupRegister(LoginAdmin.this);
                    popup.setCancelable(false);
                    popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    popup.show();
                    login(numero.getText().toString(),passwordEdt.getText().toString());

                }else {
                    Toast.makeText(LoginAdmin.this, "Veuillez remplir tout les champs", Toast.LENGTH_SHORT).show();
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
                                        codePinId = codePin.getId();
                                        editor.putString("codepin", codePinValue);
                                        editor.putString("codepinId", codePinId);
                                        editor.apply();

                                    }
                                    break;
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
                            editor.putString("codepinId", codePinId);
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
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()){
                                boolean isNumber = false;


                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    // Récupérer les données de l'utilisateur
                                    for (DataSnapshot noeud2 :snapshot.getChildren()){
                                        for (DataSnapshot noeud3 :noeud2.getChildren()){
                                            Model_tenant utilisateur = noeud3.getValue(Model_tenant.class);
                                            // Faire ce que vous voulez avec les données de l'utilisateur
                                            if (utilisateur != null) {
                                                popup.dismiss();
                                                if (utilisateur.getNumero().equals(number)){

                                                    passwords= utilisateur.getPasword();
                                                    idAdmin=utilisateur.getIdProprie();
                                                    id=utilisateur.getId();
                                                    ville=utilisateur.getLocalite();
                                                    numeros=utilisateur.getNumero();
                                                    somme=utilisateur.getPrix();
                                                    nom=utilisateur.getNom();
                                                    prenom=utilisateur.getPrenom();
                                                    type=utilisateur.getType_de_maison();
                                                    caution=utilisateur.getCaution();
                                                    avance=utilisateur.getAvance();
                                                    debutUsage=utilisateur.getDebut_de_loca();
                                                    databaseReference1.child(id).addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                                Model_code_pin codePin = childSnapshot.getValue(Model_code_pin.class);
                                                                if (codePin != null) {
                                                                    codeloca = codePin.getCode();
                                                                    codeLocalId = codePin.getId();
                                                                }
                                                                break;
                                                            }
                                                        }
                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                            System.out.println("Erreur : " + databaseError.getMessage());
                                                        }
                                                    });

                                                    if (password.equals(passwords)){
                                                        popup.dismiss();
                                                        editorLoc.clear();
                                                        editorLoc.apply();
                                                        editorLoc.putString("idAdmin",idAdmin );
                                                        editorLoc.putString("id",id );
                                                        editorLoc.putString("ville", ville);
                                                        editorLoc.putString("nom", nom);
                                                        editorLoc.putString("prenom", prenom);
                                                        editorLoc.putString("caution", caution);
                                                        editorLoc.putString("avance", avance);
                                                        editorLoc.putString("debutUsage", debutUsage);
                                                        editorLoc.putString("type", type);
                                                        editorLoc.putString("numero", numeros);
                                                        editorLoc.putString("somme", somme);
                                                        editorLoc.putString("codepin", codeloca);
                                                        editorLoc.putString("codepinId", codeLocalId);
                                                        editorLoc.putString("mdp", password);
                                                        editorLoc.apply();

                                                        startActivity(new Intent(LoginAdmin.this,EspaceLocataires.class));
                                                        finish();

                                                    }else {
                                                        popup.dismiss();
                                                        Toast.makeText(LoginAdmin.this, "Le mot de passe est incorrect", Toast.LENGTH_SHORT).show();
                                                    }


                                                }
                                            }
                                            break;
                                        }
                                    }
                                }

                            }
                            // Pour chaque enfant de "idNoeud3"
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Gestion des erreurs
                            popup.dismiss();
                            Log.e("TAG", "Erreur lors de la récupération des données", databaseError.toException());
                        }
                    });

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