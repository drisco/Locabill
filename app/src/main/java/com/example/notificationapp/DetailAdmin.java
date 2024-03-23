package com.example.notificationapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notificationapp.models.Model_code_pin;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DetailAdmin extends AppCompatActivity {
    TextView name,pseudo,number,modifier, code_pin,rappel;
    ImageView logout;
    EditText editTex,editTex1;
    Button btnVal;
    String idAdmin;
    int incr;
    SharedPreferences sharedPreferences;
    DatabaseReference databaseReference;
    private BottomSheetDialog bottomSheetDialog;

    SharedPreferences.Editor editor;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_admin);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("codepin");

        logout =findViewById(R.id.m4);
        name =findViewById(R.id.t1);
        pseudo =findViewById(R.id.t2);
        number =findViewById(R.id.t3);
        code_pin =findViewById(R.id.t5);
        rappel =findViewById(R.id.t6);
        modifier =findViewById(R.id.upda);
        sharedPreferences = getSharedPreferences("Admin", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
         idAdmin = sharedPreferences.getString("id", "");
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

        code_pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog = new BottomSheetDialog(DetailAdmin.this);
                View bottomSheetView = getLayoutInflater().inflate(R.layout.codepopup, null);
                bottomSheetDialog.setContentView(bottomSheetView);
                editTex = bottomSheetView.findViewById(R.id.editTex);
                editTex1 = bottomSheetView.findViewById(R.id.editTex1);
                btnVal = bottomSheetView.findViewById(R.id.btnVal);
                btnVal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!editTex.getText().toString().isEmpty() || !editTex1.getText().toString().isEmpty()){
                            if (!editTex.getText().toString().equals(editTex1.getText().toString())){
                                Toast.makeText(DetailAdmin.this, "Verifier le code pin", Toast.LENGTH_SHORT).show();

                            }else{
                                addCode(editTex.getText().toString());
                            }
                        }else{
                            Toast.makeText(DetailAdmin.this, "Remplisser tout les champs", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                bottomSheetDialog.show();
            }
        });

        rappel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

    private void addCode(String code) {
        DatabaseReference localiteReference = databaseReference.child(idAdmin).push();
        editor.putString("codepin", code);
        editor.apply();
        String nouvelId = localiteReference.getKey();
        Model_code_pin codePin = new Model_code_pin(nouvelId,code);
        localiteReference.setValue(codePin);
        startActivity(new Intent(DetailAdmin.this, Login.class));
        finish();
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