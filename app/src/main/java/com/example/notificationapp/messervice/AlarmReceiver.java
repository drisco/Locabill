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

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.notificationapp.Login;
import com.example.notificationapp.R;
import com.example.notificationapp.models.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {
    DatabaseReference dtabaseMessage;
    Message tenant1;
    private final String channelId = "countdown_notification_channel";
    private final  int NOTIFICATION_ID = 123;
    SharedPreferences sharedPreferences;
    String idAdm,idls;

    @Override
    public void onReceive(Context context, Intent intent) {
        dtabaseMessage = FirebaseDatabase.getInstance().getReference().child("message");
        sharedPreferences = context.getSharedPreferences("codeconfirm", Context.MODE_PRIVATE);
        idAdm = sharedPreferences.getString("idAdmin", "");
        idls = sharedPreferences.getString("id", "");
        Bundle extras = intent.getExtras();
        String reference = extras.getString("reference");

        if (!idls.isEmpty()){
            if (reference !=null){
                if (reference.equals("chaquelundi")) {
                    dtabaseMessage.child(idAdm).child("messagesemaine").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            List<Message> tenants = new ArrayList<>();
                            if (dataSnapshot.exists()){
                                tenant1 = dataSnapshot.getValue(Message.class);
                                tenants.add(tenant1);
                                messagedulundi(context,tenant1);
                            }

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Gérer les erreurs
                        }
                    });

                }else if (reference.equals("messagemois")) {
                    dtabaseMessage.child(idAdm).child("messagemois").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            List<Message> tenants = new ArrayList<>();

                            if (dataSnapshot.exists()){
                                tenant1 = dataSnapshot.getValue(Message.class);
                                tenants.add(tenant1);
                                messageDeRappel(context,tenant1);

                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Gérer les erreurs
                        }
                    });
                } else if (reference.equals("chaqueuneminute")) {
                    dtabaseMessage.child(idAdm).child("messagejour").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            List<Message> tenants = new ArrayList<>();
                            if (dataSnapshot.exists()){
                                tenant1 = dataSnapshot.getValue(Message.class);
                                tenants.add(tenant1);
                                messagedulundi(context,tenant1);
                            }

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Gérer les erreurs
                        }
                    });

                }
            }

        }
        // Récupérer les données supplémentaires de l'intent
       /* List<String> recipients = intent.getStringArrayListExtra("recipients");
        String message = intent.getStringExtra("message");

        // Envoyer le SMS à chaque destinataire
        for (String recipient : recipients) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(recipient, null, message, null, null);
        }*/

    }
    private void messageDeRappel(Context context, Message tenant) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,channelId)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("Rappel du jour")
                    .setContentText(tenant.getMessage())
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(tenant.getMessage()))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            // Créer une intention pour ouvrir l'activité appropriée lors de la clic de la notification
            Intent intent = new Intent(context, Login.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_IMMUTABLE);
            builder.setContentIntent(pendingIntent);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            // Construire la notification
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
        /*if (!listedesnumeros.isEmpty() || !messageparseconde.isEmpty()){
            recipients = recupererNumeros(listedesnumeros);
            for (String recipient : recipients) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(recipient, null, messageparseconde, null, null);
            }
        }*/
    }

    private void messagedulundi(Context context, Message tenant) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("Semaine fruiteuse")
                    .setContentText(tenant.getMessage())
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(tenant.getMessage()))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            // Créer une intention pour ouvrir l'activité appropriée lors de la clic de la notification
            Intent intent = new Intent(context, Login.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            builder.setContentIntent(pendingIntent);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            // Construire la notification
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

}


