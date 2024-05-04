package com.example.notificationapp;

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
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.notificationapp.R;
import com.example.notificationapp.models.CityItem;
import com.example.notificationapp.models.Model_tenant;
import com.example.notificationapp.models.Model_ticket;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoriqueListeRecuGeneree extends AppCompatActivity {
    RecyclerView recyclerView;
    Adapter_tickets adapterTickets;
    PopupRegister popusCostum;
    ImageView retour2;
    String idAdmin,id_2;
    RelativeLayout log;
    int incr;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historique_liste_recu_generee);
        SharedPreferences donnes = getSharedPreferences("Admin", Context.MODE_PRIVATE);
        idAdmin = donnes.getString("id", "");
        Intent intent = getIntent();
        id_2 = intent.getStringExtra("id");
        recyclerView =findViewById(R.id.recyclerView);
        retour2 =findViewById(R.id.retour2);
        log =findViewById(R.id.log);

        retour2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HistoriqueListeRecuGeneree.this,List_of_tenants.class));
                finish();
            }
        });

        popusCostum = new PopupRegister(HistoriqueListeRecuGeneree.this);
        popusCostum.setCancelable(false);
        popusCostum.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popusCostum.show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("recu").child(idAdmin).child(id_2);
        List<Model_ticket> ticketsData = new ArrayList<>();

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                popusCostum.cancel();
                // Effacer la liste existante avant de la remplir avec de nouvelles données
                ticketsData.clear();

                // Cette méthode est appelée chaque fois que les données changent
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Model_ticket ticket = snapshot.getValue(Model_ticket.class);
                    ticketsData.add(ticket);
                }

                if (ticketsData.isEmpty()){
                    popusCostum.cancel();
                    log.setVisibility(View.VISIBLE);
                }else{
                    popusCostum.cancel();
                    // Mettre à jour l'adaptateur avec les nouvelles données
                    adapterTickets = new Adapter_tickets(HistoriqueListeRecuGeneree.this,ticketsData);
                    recyclerView.setLayoutManager(new LinearLayoutManager(HistoriqueListeRecuGeneree.this));
                    recyclerView.setAdapter(adapterTickets);
                    adapterTickets.notifyDataSetChanged();
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
        super.onBackPressed();
        incr++;
        if (incr == 1) {
            Intent intent = new Intent(this, List_of_tenants.class);
            startActivity(intent);
            finish();
        }
    }
}