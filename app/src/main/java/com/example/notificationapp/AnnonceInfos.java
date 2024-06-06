package com.example.notificationapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AnnonceInfos extends AppCompatActivity implements AnnonceAdapter.ItemClickListener{
    private RecyclerView recyclerView;
    private AnnonceAdapter annonceAdapter;
    private List<Annonce> annonces;
    AlertPaiement popup;
    SharedPreferences sharedPreferences1;
    String idAdmin;
    TextView tmesssage,headT;
    ImageView search,searchannule,headI;
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
        headI = findViewById(R.id.headI);
        headT = findViewById(R.id.headT);
        search = findViewById(R.id.search);
        searchannule = findViewById(R.id.searchannule);
        SearchView searchView = findViewById(R.id.searchView);
        tmesssage = findViewById(R.id.tmesssage);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        annonces = new ArrayList<>();
        annonceAdapter = new AnnonceAdapter(annonces, this);
        annonceAdapter.setClickListener(this);
        recyclerView.setAdapter(annonceAdapter);

        headI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AnnonceInfos.this,DetailAdmin.class));
                finish();
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search.setVisibility(View.GONE);
                headT.setVisibility(View.GONE);
                searchannule.setVisibility(View.VISIBLE);
                searchView.setVisibility(View.VISIBLE);
            }
        });
        searchannule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchannule.setVisibility(View.GONE);
                searchView.setVisibility(View.GONE);
                headT.setVisibility(View.VISIBLE);
                search.setVisibility(View.VISIBLE);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Appel pour effectuer la recherche en fonction de "query"
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return false;
            }
        });
        fetchAnnoncesFromDatabase();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void filterList(String newText) {
        ArrayList<Annonce> filterUser = new ArrayList<>();
        for(Annonce tach:annonces){
            if(tach.getNom().toLowerCase().contains(newText.toLowerCase()) ||
                    tach.getPrenom().contains(newText.toLowerCase())||
                    tach.getVille().contains(newText.toLowerCase())||
                    tach.getNumero().contains(newText.toLowerCase())
                    ||tach.getNom().contains(newText.toLowerCase())){
                filterUser.add(tach);
            }
        }

        if (!annonces.isEmpty()){
            if(filterUser.isEmpty()){
                annonceAdapter.userNotFound(filterUser);
            }else{
                annonceAdapter.setFilterUser(filterUser);
                // empty.setVisibility(View.GONE);
            }
        }
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

    @Override
    public void onItemClick(View view, int position) {
        Annonce tiket = annonces.get(position);
        popup = new AlertPaiement(AnnonceInfos.this);
        popup.setCancelable(true);
        popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.show();
        popup.setTitreText("Attention!!!");
        popup.setMessageText("Voullez-vous vraiment supprimer ce poste? si oui cliquez sur le boutton supprimer");
        popup.setCancelText("Supprimer");
        popup.setCancelBackground(R.drawable.bg_circle_red);
        popup.setCancelTextColor(R.color.white);
        popup.getRetour().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.cancel();
                deleteRequest(tiket.getId(),idAdmin);
            }
        });
    }

    private void deleteRequest(String id, String idAdmin) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("annonces").child(idAdmin);
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapchild : snapshot.getChildren()){
                    for (DataSnapshot snap : snapchild.getChildren()){
                        Annonce idDelete = snap.getValue(Annonce.class);
                        if (idDelete.getId().equals(id)){
                            snap.getRef().removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}