package com.example.notificationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notificationapp.models.CityItem;
import com.example.notificationapp.models.Model_tenant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class List_of_tenants extends AppCompatActivity implements ListeTenantAdapter.ItemClickListener, PopupMenu.OnMenuItemClickListener {
    public List<CityItem> cityItems;
    public RecyclerView recyclerView;
    PopupRegister popusCostum;

    public Adapter_tenants tenantAdapter;
    public ListeTenantAdapter listAdapter;
    Model_tenant user;
    int incr;
    String idAdmin;
    TextView empty;
    ImageView retour2;
    RelativeLayout log;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_tenants);
        retour2 =findViewById(R.id.retour2);
        log =findViewById(R.id.log);

        SharedPreferences donnes = getSharedPreferences("Admin", Context.MODE_PRIVATE);
        idAdmin = donnes.getString("id", "");

        retour2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(List_of_tenants.this, MainActivity.class));
                finish();
            }
        });
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("localites").child(idAdmin);

        // Écouteur d'événements pour lire les données depuis Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 cityItems = new ArrayList<>();

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
                popusCostum = new PopupRegister(List_of_tenants.this);
                popusCostum.setCancelable(false);
                popusCostum.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                popusCostum.show();
                // Mettez à jour l'adaptateur avec les données
                if (cityItems.isEmpty()){
                    popusCostum.cancel();
                    log.setVisibility(View.VISIBLE);

                }else {
                    popusCostum.cancel();
                    log.setVisibility(View.GONE);
                    listAdapter.setCityItems(cityItems);
                    listAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Gérer les erreurs
            }
        });
        listAdapter = new ListeTenantAdapter(new ArrayList<>());
            RecyclerView nestedRecyclerView = findViewById(R.id.recyclerView);
            nestedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            nestedRecyclerView.setAdapter(listAdapter);
            listAdapter.setClickListener(this);
    }



    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId()==R.id.supprimer){
            Toast.makeText(this, "supprimer", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder pop=new AlertDialog.Builder(this);
            pop.setMessage("Voulez-vous vraiment supprimer cet utilisateur?");
            pop.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            pop.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            pop.show();
        }else if (item.getItemId()==R.id.modifier){
            Intent intent =new Intent(List_of_tenants.this,UpdateTenant.class);
            intent.putExtra("id", user.getId());
            intent.putExtra("nom", user.getNom());
            intent.putExtra("prenom", user.getPrenom());
            intent.putExtra("prix", user.getPrix());
            intent.putExtra("numero", user.getNumero());
            intent.putExtra("localite", user.getLocalite());
            intent.putExtra("type_de_maison", user.getType_de_maison());
            intent.putExtra("debut_de_loca", user.getDebut_de_loca());
            intent.putExtra("caution", user.getCaution());
            intent.putExtra("avance", user.getAvance());
            intent.putExtra("date", user.getDate());
            startActivity(intent);
            finish();
        }else if(item.getItemId()==R.id.creer){
            Intent intent =new Intent(List_of_tenants.this,New_ticket.class);
            intent.putExtra("id", user.getId());
            intent.putExtra("nom", user.getNom());
            intent.putExtra("prenom", user.getPrenom());
            intent.putExtra("prix", user.getPrix());
            intent.putExtra("numero", user.getNumero());
            intent.putExtra("type_de_maison", user.getType_de_maison());
            intent.putExtra("debut_de_loca", user.getDebut_de_loca());
            intent.putExtra("caution", user.getCaution());
            intent.putExtra("avance", user.getAvance());
            intent.putExtra("date", user.getDate());
            startActivity(intent);
            finish();

        }else if (item.getItemId()==R.id.historique){
            Intent intent =new Intent(List_of_tenants.this,HistoriqueListeRecuGeneree.class);
            intent.putExtra("id", user.getId());
            intent.putExtra("nom", user.getNom());
            startActivity(intent);
            finish();

        }
        return true;
    }

    @Override
    public void onItemClick(View view, int cityItemPosition, int tenantPosition) {
        CityItem clickedCityItem = cityItems.get(cityItemPosition);
        user = clickedCityItem.getTenantList().get(tenantPosition);
        System.out.println("GLKOUKHOPH "+user.getNom());

        PopupMenu popupMenu = new PopupMenu(view.getContext(),view, Gravity.END, 0, R.style.PopupMenuStyle);
        popupMenu.inflate(R.menu.menu_tenants);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    @Override
    public void onBackPressed() {
        incr++;
        if (incr==1){
            super.onBackPressed();
            startActivity(new Intent(List_of_tenants.this,MainActivity.class));
            finish();
        }
    }
}