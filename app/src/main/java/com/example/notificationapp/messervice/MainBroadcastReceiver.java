package com.example.notificationapp.messervice;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.notificationapp.Login;
import com.example.notificationapp.R;
import com.example.notificationapp.models.Message;
import com.example.notificationapp.models.Model_tenant;
import com.example.notificationapp.models.StatutRecu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainBroadcastReceiver extends BroadcastReceiver {
    SharedPreferences sharedPreferences;
    String idAdm;
    DatabaseReference databaseReference,databaseReference2,dtabaseMessage;
    Model_tenant tenant;
    Message tenant1;
    SharedPreferences.Editor editor;



    SharedPreferences sharedPreferences1;
    List<String> recipients = new ArrayList<>();

    String messageparseconde,listedesnumeros;
    @Override
    public void onReceive(Context context, Intent intent) {

        sharedPreferences1 = context.getSharedPreferences("rappel", Context.MODE_PRIVATE);
        editor = sharedPreferences1.edit();

         messageparseconde = sharedPreferences1.getString("messageparseconde", "");
         listedesnumeros = sharedPreferences1.getString("lesnumeros", "");

        String action = intent.getAction();
        Bundle extras = intent.getExtras();
        String reference = extras.getString("reference");
        sharedPreferences = context.getSharedPreferences("Admin", Context.MODE_PRIVATE);
        idAdm = sharedPreferences.getString("id", "");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("localites").child(idAdm);
        databaseReference2 = FirebaseDatabase.getInstance().getReference().child("statutdumois");
        dtabaseMessage = FirebaseDatabase.getInstance().getReference().child("message");

        if (!idAdm.isEmpty()){

                    if (reference != null) {

                        if (reference.equals("nouveaupaiement")){
                            //startMyService(context);
                        }else if (reference.equals("chaquefindumois")) {
                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    for (DataSnapshot citySnapshot : dataSnapshot.getChildren()) {
                                        List<Model_tenant> tenants = new ArrayList<>();
                                        for (DataSnapshot tenantSnapshot : citySnapshot.getChildren()) {
                                            tenant = tenantSnapshot.getValue(Model_tenant.class);
                                            tenants.add(tenant);
                                            ajouterRecuAuto(tenant);
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Gérer les erreurs
                                }
                            });

                        }else if (reference.equals("test")) {
                            messageDeRappel(context);

                        }
                    }

        }


    }

    //methode de text
    private void messageDeRappel(Context context) {

         if (!listedesnumeros.isEmpty() || !messageparseconde.isEmpty()){
            recipients = recupererNumeros(listedesnumeros);
            for (String recipient : recipients) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(recipient, null, messageparseconde, null, null);
            }
          }
         editor.clear();
         editor.apply();

    }

    private List<String> recupererNumeros(String listedesnumeros) {
        String[] numerosArray = listedesnumeros.split(",");

        List<String> result = new ArrayList<>();
        for (String numero : numerosArray) {
            result.add(numero.trim());
        }
        return result;
    }

    private void ajouterRecuAuto(Model_tenant tenant) {
        Date heure = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        SimpleDateFormat sdf2 = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        String dateFormatted = sdf2.format(heure);
        String heureActuelle = sdf.format(heure);
        DatabaseReference localiteReference = databaseReference2.child(tenant.getIdProprie()).child(tenant.getId()).push();
        String nouvelId = localiteReference.getKey();
        StatutRecu statut = new StatutRecu(nouvelId, tenant.getIdProprie(), tenant.getId(), tenant.getStatut(),dateFormatted,heureActuelle, tenant.getPrix());
        localiteReference.setValue(statut);
        databaseReference.child(tenant.getLocalite()).child(tenant.getId()).child("statut").setValue("impayé");
    }

    // Méthode pour démarrer le service
    /*private void startMyService(Context context) {
        Intent serviceIntent = new Intent(context, MesServices.class);
        context.startService(serviceIntent);
    }*/
}