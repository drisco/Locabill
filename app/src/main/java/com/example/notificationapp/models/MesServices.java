package com.example.notificationapp.models;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.notificationapp.MainActivity;
import com.example.notificationapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
public class MesServices extends Service {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    DatabaseReference databaseReference;
    String idAdm,idLca,ville,numero,date ,statut,id,somme,somme1,caution,avance,debutUsage,type,nom,prenom;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences("codeconfirm", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        idAdm = sharedPreferences.getString("idAdmin", "");
        idLca = sharedPreferences.getString("id", "");
        ville = sharedPreferences.getString("ville", "");
        numero = sharedPreferences.getString("numero", "");
        somme1 = sharedPreferences.getString("somme", "");
        nom = sharedPreferences.getString("nom", "");
        prenom = sharedPreferences.getString("prenom", "");
        caution = sharedPreferences.getString("caution", "");
        debutUsage = sharedPreferences.getString("debutUsage", "");
        avance = sharedPreferences.getString("avance", "");
        type = sharedPreferences.getString("type", "");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Admin").child(idAdm);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    sendNofication();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Méthode pour envoyer une notification à l'administrateur
    private void sendNofication() {
        // Créer une intention pour ouvrir une activité lorsque la notification est cliquée
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                    .setSmallIcon(R.drawable.locabill)
                    .setContentTitle("Notification de paiement")
                    .setContentText("Le paiement de "+somme1+"Fcfa "+"effectué par "+nom+" "+prenom)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            notificationManager.notify(1, builder.build());
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}


