package com.example.notificationapp.messervice;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
    String monmessage;
    String idAdm,idls;

    @Override
    public void onReceive(Context context, Intent intent) {
        dtabaseMessage = FirebaseDatabase.getInstance().getReference().child("message");
        sharedPreferences = context.getSharedPreferences("codeconfirm", Context.MODE_PRIVATE);
        idAdm = sharedPreferences.getString("idAdmin", "");
        idls = sharedPreferences.getString("id", "");
        Bundle extras = intent.getExtras();
        String reference = extras.getString("reference");

        createNotificationChannel(context);

        if (!idls.isEmpty()){
            if (reference !=null){
                if (reference.equals("chaquelundi")) {
                    monmessage="Bonjour monsieur,\nJ'espère que vous avez passé un excellent week-end ! Je tenais à vous souhaiter un bon début de semaine. Si vous avez des questions ou des préoccupations concernant votre logement, n'hésitez pas à me contacter. Je suis là pour vous aider.\nBonne semaine à vous !";
                    messageParDefaut(context,monmessage);

                }else if (reference.equals("messagemois")) {
                    monmessage="Bonjour monsieur,\nNous voilà déjà à la moitié du mois ! Je voulais prendre un moment pour vous rappeler que le loyer est dû à la fin de ce mois. Si vous avez des questions ou des préoccupations, ou si vous avez besoin d'aide pour quoi que ce soit, n'hésitez pas à me faire signe.\nMerci de votre attention et passez une excellente fin de mois !";
                    messageParDefaut(context,monmessage);
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

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Nom_notification";
            String description = "description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }



    private void messagedulundi(Context context, Message tenant) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.locabill)
                    .setContentTitle("Rappel de Paiement de Loyer")
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


    private void messageParDefaut(Context context,String message) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.locabill)
                    .setContentTitle("Rappel de Paiement de Loyer")
                    .setContentText(message)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
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


