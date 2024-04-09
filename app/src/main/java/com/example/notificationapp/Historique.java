package com.example.notificationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.RelativeLayout;

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

public class Historique extends AppCompatActivity {
    private AdapterCityGroup classAdapter;
    Adapter_historique studentAdapter;
    int incr;
    String idAdmin;
    RecyclerView recyclerView;
    PopupRegister popusCostum;
    RelativeLayout log;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historique);
        SharedPreferences donnes = getSharedPreferences("Admin", Context.MODE_PRIVATE);
        idAdmin = donnes.getString("id", "");
        FirebaseApp.initializeApp(this);
        recyclerView= findViewById(R.id.recyclerView);
        log= findViewById(R.id.log);

        // Référence à la base de données Firebase
        popusCostum = new PopupRegister(Historique.this);
        popusCostum.setCancelable(false);
        popusCostum.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popusCostum.show();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("recu").child(idAdmin);
        List<Model_ticket> ticketsData = new ArrayList<>();
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
                        System.out.println("BNXN?CVNCXN?CVXNCNCNVNNVNVNBVNNB "+ticket.getCaution());
                    }

                }
                System.out.println("BNXN?CVNCXN?CVXNCNCNVNNVNVNBVNNB "+ticketsData);
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
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Gérer les erreurs ici
            }
        });


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