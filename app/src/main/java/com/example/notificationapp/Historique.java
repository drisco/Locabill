package com.example.notificationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.notificationapp.models.CityItem;
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
    int incr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historique);


        List<CityItem> classes = new ArrayList<>();

        /*// Remplir la liste avec des données
        classes.add(new CityItem("Class 1", createStudentList(8)));
        classes.add(new CityItem("Class 2", createStudentList(5)));
        classes.add(new CityItem("Class 3", createStudentList(7)));*/
        FirebaseApp.initializeApp(this);

        // Référence à la base de données Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("localites");

        // Écouteur d'événements pour lire les données depuis Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<CityItem> cityItems = new ArrayList<>();

                for (DataSnapshot citySnapshot : dataSnapshot.getChildren()) {
                    String cityName = citySnapshot.getKey();
                    List<Model_tenant> tenants = new ArrayList<>();

                    for (DataSnapshot tenantSnapshot : citySnapshot.getChildren()) {
                        Model_tenant tenant = tenantSnapshot.getValue(Model_tenant.class);
                        tenants.add(tenant);
                    }

                    CityItem cityItem = new CityItem(cityName, tenants);
                    cityItems.add(cityItem);
                }

                // Mettez à jour l'adaptateur avec les données
                classAdapter.setCityItems(cityItems);
                classAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Gérer les erreurs
            }
        });

        // Initialisez l'adaptateur avec une liste vide (sera mise à jour après récupération des données)
        classAdapter = new AdapterCityGroup(new ArrayList<>());

        // Configurez le RecyclerView
        RecyclerView nestedRecyclerView = findViewById(R.id.recyclerView);
        nestedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        nestedRecyclerView.setAdapter(classAdapter);

        //DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("localites");

    }

    private List<Model_tenant> createStudentList(int numberOfStudents) {
        List<Model_tenant> students = new ArrayList<>();
        for (int i = 0; i < numberOfStudents; i++) {
            students.add(new Model_tenant("1", "John", "Doe", "35000","+2250102589655","Abobo", "Appartement", "01/01/2024", "900000", "900000", "01/01/2024"));
        }
        return students;
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