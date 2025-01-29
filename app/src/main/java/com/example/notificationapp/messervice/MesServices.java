
package com.example.notificationapp.messervice;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.notificationapp.EspaceLocataires;
import com.example.notificationapp.List_of_tenants;
import com.example.notificationapp.MainActivity;

import com.example.notificationapp.R;
import com.example.notificationapp.models.Message;
import com.example.notificationapp.models.Model_ticket;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MesServices extends Service {
    SharedPreferences sharedPreferences,shareControl;
    private final int notificationId = 1;
    SharedPreferences.Editor editorStock;
    private final String FOREGROUND_CHANNEL_ID = "foreground_service_channel";
    String control;
    DatabaseReference recuRef ;
    private Runnable timeCheckRunnable;
    private final String channelId = "countdown_notification_channel";
    private Handler handler;
    String idAdm,nom;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeService();
        startAsForegroundService();
    }

    private void initializeService() {
        handler = new Handler();
        recuRef = FirebaseDatabase.getInstance().getReference("recu");
        sharedPreferences = getSharedPreferences("Admin", Context.MODE_PRIVATE);
        shareControl = getSharedPreferences("shareControl", Context.MODE_PRIVATE);
        editorStock = shareControl.edit();
        idAdm = sharedPreferences.getString("id", "");
        nom = sharedPreferences.getString("nom", "");
        control = shareControl.getString("ancien", "");

        timeCheckRunnable = new Runnable() {
            @Override
            public void run() {
                updateTimeAndCheck();
                handler.postDelayed(this, 60000); // Vérifie toutes les minutes
            }
        };
    }

    private void updateTimeAndCheck() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("cheikpaiement").child(idAdm);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot villeSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot lcaSnapshot : villeSnapshot.getChildren()) {
                        String verifie = lcaSnapshot.child("verifie").getValue(String.class);
                        if ("vrai".equals(verifie)) {
                            envoyerNotificationAdmin();
                        }else {

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Gérez les erreurs de récupération
            }
        });

    }

    private void envoyerNotificationPlanifiee() {
        String message = "Rappel important : Fin du mois approche";

        Intent intent = new Intent(this, EspaceLocataires.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.calendar)
                .setContentTitle("Notification planifiée")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(notificationId + 100, builder.build());
        }
    }

    // ... Le reste du code reste inchangé ...

    @SuppressLint("ForegroundServiceType")
    private void startAsForegroundService() {
        createForegroundServiceChannel();
        startForeground(2, createForegroundNotification());
    }

    private void createForegroundServiceChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    FOREGROUND_CHANNEL_ID,
                    "Service de notifications",
                    NotificationManager.IMPORTANCE_LOW
            );
            serviceChannel.setDescription("Canal pour le service en arrière-plan");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private Notification createForegroundNotification() {
        Intent notificationIntent = new Intent(this, EspaceLocataires.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, FOREGROUND_CHANNEL_ID)
                .setContentTitle("")
                .setContentText("")
                .setContentIntent(pendingIntent)
                .build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.post(timeCheckRunnable);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(timeCheckRunnable);
        }
    }

    private void envoyerNotificationAdmin() {
        recuRef.child(idAdm).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Model_ticket> allMessages = new ArrayList<>();
                // Pour chaque nouvel enfant ajouté, récupérez ses données
                if (snapshot.exists()){
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {

                        for (DataSnapshot child : childSnapshot.getChildren()){
                            Model_ticket ticket = child.getValue(Model_ticket.class);
                            allMessages.add(ticket);
                            if (child.getChildrenCount() == 1) {
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
                    }



                    if (!allMessages.isEmpty()){

                        Model_ticket user=allMessages.get(0);
                        if (!control.equals(user.getHeure())){
                            editorStock.putString("ancien", user.getHeure());
                            editorStock.apply();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                createNotificationChannel();
                                envoyerNotification(user.getDate(), user.getNom(), user.getNumero(), user.getPrenom(), user.getMontant());
                            }
                        }else {
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
                .setSmallIcon(R.drawable.dial)
                .setContentTitle("Paiement effectué")
                .setContentText("Vous avez recu une somme de "+somme+" Fcfa "+" par Locataire M."+nom1 + " " + prenom1 )
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Vous avez recu une somme de "+somme+" Fcfa "+" par Locataire M."+nom1 + " " + prenom1+ "de son loyer " + " pour le mois de " + date1))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Créer une intention pour ouvrir l'activité appropriée lors de la clic de la notification
        Intent intent = new Intent(this, List_of_tenants.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Construire la notification
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(notificationId, builder.build());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("cheikpaiement").child(idAdm);
        Handler handler = new Handler(Looper.getMainLooper());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<DataSnapshot> villesToProcess = new ArrayList<>();
                for (DataSnapshot villeSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot lcaSnapshot : villeSnapshot.getChildren()) {
                        villesToProcess.add(lcaSnapshot);
                    }
                }

                // Commencer le traitement avec intervalle
                processWithDelay(villesToProcess, 0);
            }

            private void processWithDelay(List<DataSnapshot> items, int currentIndex) {
                if (currentIndex >= items.size()) {
                    System.out.println("Toutes les modifications sont terminées");
                    return;
                }

                // Modifier l'item actuel
                DataSnapshot currentItem = items.get(currentIndex);
                currentItem.child("verifie").getRef().setValue("faux")
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                System.out.println("Item " + currentIndex + " modifié avec succès");

                                // Programmer le prochain item après 1 minute
                                handler.postDelayed(() -> {
                                    processWithDelay(items, currentIndex + 1);
                                }, 60000); // 60000 ms = 1 minute
                            } else {
                                System.out.println("Erreur lors de la modification de l'item " + currentIndex);
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Erreur : " + error.getMessage());
            }
        });
    }
}


