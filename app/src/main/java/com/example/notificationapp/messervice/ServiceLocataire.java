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
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.notificationapp.EspaceLocataires;
import com.example.notificationapp.R;
import com.example.notificationapp.models.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ServiceLocataire extends Service {
    private final int notificationId = 1;
    private final String channelId = "countdown_notification_channel";
    private final String FOREGROUND_CHANNEL_ID = "foreground_service_channel";
    private DatabaseReference dtabaseMessage;
    private SharedPreferences sharedPreferences;
    private Message tenant1;
    private String idAdm, dateFormatee, heureFormatee, idls;
    private Handler handler;
    private Runnable timeCheckRunnable;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeService();
        startAsForegroundService();
    }

    private void initializeService() {
        handler = new Handler();
        sharedPreferences = getSharedPreferences("codeconfirm", Context.MODE_PRIVATE);
        idls = sharedPreferences.getString("id", "");
        idAdm = sharedPreferences.getString("idAdmin", "");
        dtabaseMessage = FirebaseDatabase.getInstance().getReference().child("message");

        timeCheckRunnable = new Runnable() {
            @Override
            public void run() {
                updateTimeAndCheck();
                handler.postDelayed(this, 60000); // Vérifie toutes les minutes
            }
        };
    }

    private void updateTimeAndCheck() {
        Date heure = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat heureFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        dateFormatee = dateFormat.format(heure);
        heureFormatee = heureFormat.format(heure);

        // Vérifier si c'est le moment d'envoyer la notification planifiée
        if (isScheduledNotificationTime()) {
            envoyerNotificationPlanifiee();
        } else {
            envoyerNotificationAdmin();
        }
    }

    private boolean isScheduledNotificationTime() {
        Calendar calendar = Calendar.getInstance();
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Vérifier si nous sommes après le 20ème jour du mois
        boolean isAfter20th = dayOfMonth >= 20;

        // Vérifier si c'est 9h00 ou 13h00
        boolean isScheduledHour = (hour == 9 && minute == 58) || (hour == 13 && minute == 0);

        return isAfter20th && isScheduledHour;
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
        if (idAdm.isEmpty()) return;
        dtabaseMessage.child(idAdm).child("messagejour")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            tenant1 = dataSnapshot.getValue(Message.class);

                            if (tenant1.getDate().equals(dateFormatee) && tenant1.getHeurre().equals(heureFormatee)) {
                                createNotificationChannel();
                                envoyerNotification(tenant1);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("ServiceLocataire", "Erreur Firebase: " + error.getMessage());
                    }
                });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Notifications messages",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Canal pour les notifications de messages");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void envoyerNotification(Message tenant1) {
        Intent intent = new Intent(this, EspaceLocataires.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.calendar)
                .setContentTitle("Nouveau message")
                .setContentText(tenant1.getMessage())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(tenant1.getMessage()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(notificationId, builder.build());
        }
    }
}