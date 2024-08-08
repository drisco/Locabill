package com.example.notificationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notificationapp.models.CityItem;
import com.example.notificationapp.models.HistItems;
import com.example.notificationapp.models.Model_tenant;
import com.example.notificationapp.models.Model_ticket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Historique extends AppCompatActivity implements Adapter_historique.ItemClickListener {
    List<Model_ticket> ticketsData;
    Adapter_historique studentAdapter;
    androidx.appcompat.widget.SearchView recherche;
    int incr;
    String idAdmin;
    RecyclerView recyclerView;
    PopupRegister popusCostum;

    RelativeLayout log;
    TextView message,title;
    ImageView headI;
    ImageView iconSearch;
    boolean isSearchBarVisible = false;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historique);
        recherche = findViewById(R.id.searchB);
        SharedPreferences donnes = getSharedPreferences("Admin", Context.MODE_PRIVATE);
        idAdmin = donnes.getString("id", "");
        FirebaseApp.initializeApp(this);
        recyclerView= findViewById(R.id.recyclerView);
        log= findViewById(R.id.log);
        headI= findViewById(R.id.headI);
        message= findViewById(R.id.message);
        ticketsData = new ArrayList<>();
        iconSearch = findViewById(R.id.search);
        title = findViewById(R.id.headT);

        iconSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inversez l'état actuel de la variable
                isSearchBarVisible = !isSearchBarVisible;

                if (isSearchBarVisible) {
                    // Afficher la barre de recherche
                    iconSearch.setImageResource(R.drawable.close);
                    title.setVisibility(View.GONE);
                    recherche.setVisibility(View.VISIBLE);
                } else {
                    // Masquer la barre de recherche
                    iconSearch.setImageResource(R.drawable.search);
                    title.setVisibility(View.VISIBLE);
                    recherche.setVisibility(View.GONE);
                }
            }
        });

        headI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Historique.this,MainActivity.class));
                finish();
            }
        });

        // Référence à la base de données Firebase
        popusCostum = new PopupRegister(Historique.this);
        popusCostum.setCancelable(false);
        popusCostum.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popusCostum.show();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("recu").child(idAdmin);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                popusCostum.cancel();
                // Effacer la liste existante avant de la remplir avec de nouvelles données
                ticketsData.clear();

                // Cette méthode est appelée chaque fois que les données changent
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        Model_ticket ticket = snapshot1.getValue(Model_ticket.class);
                        ticketsData.add(ticket);
                    }

                }
                if (ticketsData.isEmpty()){
                    popusCostum.cancel();
                    log.setVisibility(View.VISIBLE);
                }else{
                    popusCostum.cancel();
                    log.setVisibility(View.GONE);
                    studentAdapter = new Adapter_historique(Historique.this,ticketsData);
                    recyclerView.setLayoutManager(new LinearLayoutManager(Historique.this));
                    recyclerView.setAdapter(studentAdapter);
                    studentAdapter.notifyDataSetChanged();
                    studentAdapter.setClickListener(Historique.this);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Gérer les erreurs ici
            }
        });

        recherche.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });
    }

    private void filterList(String newText) {
        List<Model_ticket> filterUser = new ArrayList<>();
        for(Model_ticket user : ticketsData) {
            String nomUser = user.getNom().trim().toLowerCase();
            String dateUser = user.getDate().trim().toLowerCase();
            String searchText = newText.trim().toLowerCase();

            if(nomUser.contains(searchText) || dateUser.contains(searchText)) {
                filterUser.add(user);
            }
        }
        if (!ticketsData.isEmpty()){
            if(filterUser.isEmpty()){
                message.setVisibility(View.VISIBLE);
                studentAdapter.userNotFound(filterUser);

            }else{
                studentAdapter.setFilterUser(filterUser);
                message.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Model_ticket tiket = ticketsData.get(position);

        Intent intent =new Intent(Historique.this,DetailTikets.class);
        intent.putExtra("id", tiket.getId());
        intent.putExtra("nom", tiket.getNom());
        intent.putExtra("prenom", tiket.getPrenom());
        intent.putExtra("prix", tiket.getMontant());
        intent.putExtra("numero", tiket.getNumero());
        intent.putExtra("type_de_maison", tiket.getType());
        intent.putExtra("debut_de_loca", tiket.getHeure());
        intent.putExtra("chiffre", tiket.getMontantChiffre());
        intent.putExtra("date", tiket.getDate());
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        incr++;
        if (incr==1){
            super.onBackPressed();
            startActivity(new Intent(Historique.this,MainActivity.class));
            finish();
        }
    }

}