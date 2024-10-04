package com.example.notificationapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.notificationapp.models.ModelContract;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VoirContrat extends AppCompatActivity {
    TextView contrat;
    ImageView signpro,snloca;
    DatabaseReference databaseRef;
    SharedPreferences sharedPreferences;
    String idAdm,idLca,veri;
    ScrollView contenu;
    int incr;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_voir_contrat);


        Intent intent = getIntent();
        idAdm = intent.getStringExtra("idAdm");
        idLca = intent.getStringExtra("idLca");
        veri = intent.getStringExtra("veri");

        contenu=findViewById(R.id.contenu);
        snloca=findViewById(R.id.snloca);
        contrat=findViewById(R.id.contrat);
        signpro=findViewById(R.id.signPro);
        checkIfContractExists(idAdm,idLca);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void checkIfContractExists(String userId, String idLca) {
        databaseRef = FirebaseDatabase.getInstance().getReference("contrats").child(userId);

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Le contrat existe
                    ModelContract contract = dataSnapshot.getValue(ModelContract.class);
                    if (contract != null && contract.getEstSigne()) {
                        // Afficher le BottomSheet si le contrat existe
                        Glide.with(VoirContrat.this).load(contract.getSignatureUrl()).into(signpro);
                        contrat.setText(contract.getContrat());
                        showBottomSheet(contract);
                    }
                }else{
                    contenu.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseDatabase", "Erreur lors de la vérification du contrat", databaseError.toException());
            }
        });
    }

    private void showBottomSheet(ModelContract contract) {
        databaseRef = FirebaseDatabase.getInstance().getReference("contrats").child(idLca);
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Le contrat existe
                    ModelContract contraL = dataSnapshot.getValue(ModelContract.class);
                    if ( contraL.getEstSigne()) {
                        Glide.with(VoirContrat.this).load(contraL.getSignatureUrl()).into(snloca);
                    }else {

                    }
                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseDatabase", "Erreur lors de la vérification du contrat", databaseError.toException());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        incr++;
        if (incr == 1) {

            if (veri.equals("veriLoca")){
                Intent intent = new Intent(this, EspaceLocataires.class);
                startActivity(intent);
                finish();
            }else{
                Intent intent = new Intent(this, List_of_tenants.class);
                startActivity(intent);
                finish();
            }

        }
    }

}