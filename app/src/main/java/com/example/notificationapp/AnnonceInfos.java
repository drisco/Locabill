package com.example.notificationapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notificationapp.models.Annonce;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AnnonceInfos extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AnnonceAdapter annonceAdapter;
    private List<Annonce> annonces;
    SharedPreferences sharedPreferences1;
    String idAdmin;
    TextView tmesssage;
    private int incr;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_annonce_infos);
        SharedPreferences donnes = getSharedPreferences("Admin", Context.MODE_PRIVATE);
        idAdmin = donnes.getString("id", "");
        recyclerView = findViewById(R.id.recyclerView);
        tmesssage = findViewById(R.id.tmesssage);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        annonces = new ArrayList<>();

        annonceAdapter = new AnnonceAdapter(annonces, this);
        recyclerView.setAdapter(annonceAdapter);

        fetchAnnoncesFromDatabase();

    }

    private void fetchAnnoncesFromDatabase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("annonces").child(idAdmin);
        databaseRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                annonces.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot snap : snapshot.getChildren()){
                        Annonce annonce = snap.getValue(Annonce.class);
                        annonces.add(annonce);
                    }
                }
                if (annonces.isEmpty()){
                    tmesssage.setVisibility(View.VISIBLE);
                }
                annonceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
    @Override
    public void onBackPressed() {
        incr++;
        if (incr==1){
            super.onBackPressed();
            startActivity(new Intent(AnnonceInfos.this,DetailAdmin.class));
            finish();
        }
    }
}