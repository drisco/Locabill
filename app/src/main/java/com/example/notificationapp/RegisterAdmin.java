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
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private TextInputEditText userName,numero,Userprenom, passwordEdt, confirmPwdEdt;
    private TextView loginTV;
    private TextView registerBtn;
    int incr;
    PopusCostum popusCostum;
    DatabaseReference databaseReference;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_admin);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Admin");
        sharedPreferences = getSharedPreferences("Admin", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        // initializing all our variables.
        userName = findViewById(R.id.UserName);
        Userprenom = findViewById(R.id.Userprenom);
        numero = findViewById(R.id.UserEmail);
        passwordEdt = findViewById(R.id.idEdtPassword);
        confirmPwdEdt = findViewById(R.id.idEdtConfirmPassword);
        loginTV = findViewById(R.id.idTVLoginUser);
        registerBtn = findViewById(R.id.idBtnRegister);


        loginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // opening a login activity on clicking login text.
                Intent i = new Intent(RegisterAdmin.this, LoginAdmin.class);
                startActivity(i);
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userNames = userName.getText().toString();
                String userPrenom = Userprenom.getText().toString();
                String numeros = numero.getText().toString();
                String pwd = passwordEdt.getText().toString();
                String cnfPwd = confirmPwdEdt.getText().toString();
                if (!userNames.isEmpty() || !pwd.isEmpty() || !cnfPwd.isEmpty()){

                    // checking if the password and confirm password is equal or not.
                    if (!pwd.equals(cnfPwd)) {
                        Toast.makeText(RegisterAdmin.this, "Veuillez verifier vos mots de passe..", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(userNames) &&TextUtils.isEmpty(userPrenom) &&TextUtils.isEmpty(numeros) && TextUtils.isEmpty(pwd) && TextUtils.isEmpty(cnfPwd)) {

                        // checking if the text fields are empty or not.
                        Toast.makeText(RegisterAdmin.this, "Veuillez saisir vos identifiants..", Toast.LENGTH_SHORT).show();
                    }else {
                        popusCostum = new PopusCostum(RegisterAdmin.this);
                        popusCostum.setCancelable(false);
                        popusCostum.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        popusCostum.show();
                        addAdmin(userNames,userPrenom,numeros,pwd);
                    }
            }
            }
        });
    }

    private void addAdmin(String userNames, String userPrenom, String numeros, String pwd) {
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
        databaseReference.orderByChild("numeros").equalTo(numeros).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Vérifiez si dataSnapshot contient des données
                if (dataSnapshot.exists()) {
                    popusCostum.cancel();
                    // L'utilisateur existe déjà avec ce numéro de téléphone, affichez un message d'erreur
                    Toast.makeText(getApplicationContext(), "Ce numéro de téléphone est déjà associé à un compte.", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseReference localiteReference = databaseReference.push();
                    String nouvelId = localiteReference.getKey();
                    editor.putString("id", nouvelId);
                    editor.putString("nom", userNames);
                    editor.putString("prenom", userPrenom);
                    editor.putString("numero", numeros);
                    editor.putString("mdp", pwd);
                    editor.apply();
                    Admin nouveauAdmin =new Admin(nouvelId,userNames,userPrenom,numeros,pwd, finalFormattedDate);
                    localiteReference.setValue(nouveauAdmin);
                    popusCostum.cancel();
                    startActivity(new Intent(RegisterAdmin.this,MainActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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