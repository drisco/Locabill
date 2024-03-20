package com.example.notificationapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DetailAdmin extends AppCompatActivity {
    TextView name,pseudo,number,modifier;
    ImageView logout;
    int incr;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_admin);
        logout =findViewById(R.id.m4);
        name =findViewById(R.id.t1);
        pseudo =findViewById(R.id.t2);
        number =findViewById(R.id.t3);
        modifier =findViewById(R.id.upda);
        sharedPreferences = getSharedPreferences("Admin", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String idAdmin = sharedPreferences.getString("id", "");
        String nom = sharedPreferences.getString("nom", "");
        String prenom = sharedPreferences.getString("prenom", "");
        String numero = sharedPreferences.getString("numero", "");
        String mdp = sharedPreferences.getString("mdp", "");

        name.setText(nom);
        pseudo.setText(prenom);
        number.setText("+225 "+numero);
        modifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DetailAdmin.this, "Update", Toast.LENGTH_SHORT).show();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.clear();
                editor.apply();
                startActivity(new Intent(DetailAdmin.this, LoginAdmin.class));
                finish();
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