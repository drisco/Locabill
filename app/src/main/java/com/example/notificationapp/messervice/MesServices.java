package com.example.notificationapp.messervice;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.notificationapp.Login;
import com.example.notificationapp.R;
import com.example.notificationapp.models.Model_ticket;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MesServices extends Service {

    SharedPreferences sharedPreferences,shareControl;
    private final int notificationId = 1;
    SharedPreferences.Editor editorStock;
    String control;
    DatabaseReference recuRef ;
    private final String channelId = "countdown_notification_channel";

   /* private Handler handler = new Handler();
    // Intervalles de temps en millisecondes (5 minutes)
    private static final long INTERVALLE = 60 * 1000;

    // Tâche à exécuter à intervalles réguliers
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {


            handler.postDelayed(this, INTERVALLE);
        }
    };*/
    String idAdm,nom;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        recuRef = FirebaseDatabase.getInstance().getReference("recu");
        sharedPreferences = getSharedPreferences("Admin", Context.MODE_PRIVATE);
        shareControl = getSharedPreferences("shareControl", Context.MODE_PRIVATE);
        editorStock = shareControl.edit();
        idAdm = sharedPreferences.getString("id", "");
        nom = sharedPreferences.getString("nom", "");
        control = shareControl.getString("ancien", "");

        envoyerNotificationAdmin();
        return START_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private void envoyerNotificationAdmin() {
        recuRef.child(idAdm).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Model_ticket> allMessages = new ArrayList<>();
                // Pour chaque nouvel enfant ajouté, récupérez ses données
                if (snapshot.exists()){
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {

                            Model_ticket ticket = childSnapshot.getValue(Model_ticket.class);
                            allMessages.add(ticket);
                            if (childSnapshot.getChildrenCount() == 1) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    createNotificationChannel();
                                        envoyerNotification(ticket.getDate(), ticket.getNom(), ticket.getNumero(), ticket.getPrenom(), ticket.getMontant());
                                    }
                                }else{

                                Collections.sort(allMessages, new Comparator<Model_ticket>() {
                                        @Override
                                        public int compare(Model_ticket t1, Model_ticket t2) {
                                            return t2.getHeure().compareToIgnoreCase(t1.getHeure());
                                        }
                                    });

                                }
                            }



                        if (!allMessages.isEmpty()){
                            System.out.println("UDFHGJREUGDJKRGUHLIIRGHFIGHNHEFIDHGFVHUIEFHDGIVUHHFEIDUHGVUFHDGIHEFUIDHGVURHIFDGJIURHEFJIDUGJUIFDJG "+allMessages.size());

                            Model_ticket user=allMessages.get(0);
                            if (!control.equals(user.getHeure())){
                                editorStock.putString("ancien", user.getHeure());
                                editorStock.apply();
                                System.out.println("UDFHGJREUGDJKRGUHLIIRGHFIGHNHEFIDHGFVHUIEFHDGIVUHHFEIDUHGVUFHDGIHEFUIDHGVURHIFDGJIURHEFJIDUGJUIFDJG "+user.getHeure());
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    createNotificationChannel();
                                    envoyerNotification(user.getDate(), user.getNom(), user.getNumero(), user.getPrenom(), user.getMontant());
                                }
                            }else {
                                System.out.println("UDFHGJREUGDJKRGUHLIIRGHFIGHNHEFIDHGFVHUIEFHDGIVUHHFEIDUHGVUFHDGIHEFUIDHGVURHIFDGJIURHEFJIDUGJUIFDJG moro "+user.getHeure());
                                editorStock.putString("ancien", user.getHeure());
                                editorStock.apply();

                            }

                        }else{
                        }
                    }else{
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Nom_notification";
            String description = "decription";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    @SuppressLint("ForegroundServiceType")
    private void envoyerNotification(String date1, String nom1, String numero1, String prenom1, String somme) {
        System.out.println("UDFHGJREUGDJKRGUHLIIRGHFIGHNHEFIDHGFVHUIEFHDGIVUHHFEIDUHGVUFHDGIHEFUIDHGVURHIFDGJIURHEFJIDUGJUIFDJG "+somme);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Paiement effectué")
                .setContentText("Vous avez recu une somme de "+somme+" Fcfa "+" par Locataire M."+nom1 + " " + prenom1 )
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Vous avez recu une somme de "+somme+" Fcfa "+" par Locataire M."+nom1 + " " + prenom1+ "de son loyer " + " pour le mois de " + date1))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Créer une intention pour ouvrir l'activité appropriée lors de la clic de la notification
        Intent intent = new Intent(this, Login.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MesServices.this);

        // Construire la notification
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(notificationId, builder.build());
    }

}


