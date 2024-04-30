package com.example.notificationapp;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Bricefile extends AppCompatActivity {

    private CountDownTimer countdownTimer;
    private TextView countdownText;
    private final long initialTimeInMillis = 2 * 60 * 1000; // 2 minutes en millisecondes
    private final String channelId = "countdown_notification_channel";
    private final int notificationId = 1;
    private final int PERMISSION_REQUEST_CODE = 13;
    private SharedPreferences sharedPreferences;
    private long timeLeftInMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bricefile);

        createNotificationChannel();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        countdownText = findViewById(R.id.countdown_text);

        if (savedInstanceState != null) {
            timeLeftInMillis = savedInstanceState.getLong("timeLeftInMillis");
        } else {
            timeLeftInMillis = sharedPreferences.getLong("timeLeftInMillis", initialTimeInMillis);
        }

        startTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        countdownTimer.cancel();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("timeLeftInMillis", timeLeftInMillis);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTimer();
    }

    private void startTimer() {
        countdownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountdownText();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                countdownText.setText("00:00");
                updateCountdownText();

                if (ActivityCompat.checkSelfPermission(Bricefile.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Bricefile.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);

                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        sendNotification();
                    }
                }
            }
        }.start();
    }

    private void updateCountdownText() {
        long minutes = timeLeftInMillis / 1000 / 60;
        long seconds = (timeLeftInMillis / 1000) % 60;
        countdownText.setText(String.format("%02d:%02d", minutes, seconds));
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

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void sendNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("title")
                .setContentText("contenu")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(notificationId, builder.build());

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("timeLeftInMillis", timeLeftInMillis);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Si l'utilisateur accorde la permission, envoyer le SMS

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    sendNotification();
                }
            } else {
                // Si l'utilisateur refuse la permission, afficher un message
                Toast.makeText(getApplicationContext(), "Permission refusée pour envoyer des SMS.", Toast.LENGTH_SHORT).show();
            }

        }
 }
}