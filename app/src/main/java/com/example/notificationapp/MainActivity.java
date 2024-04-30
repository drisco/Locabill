package com.example.notificationapp;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 140;
    private static final int PERMISSION_REQUEST_CO = 10;
    /*private static final long INTERVAL_MS = 2 * 60 * 1000;*/ // 2 minutes en millisecondes
    private static final long INTERVAL_MS = 1 * 60 * 1000;
    int incr;
    private Handler handler;
    RelativeLayout rl1,rl2,rlt2;
    SharedPreferences sharedPreferences1;
    CircleImageView profil;
    ImageView r2;
    String idAdmin;
    EditText etToken;
    List<String> recipients = new ArrayList<>();
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rl1=findViewById(R.id.rl1);
        profil=findViewById(R.id.profil);
        rl2=findViewById(R.id.rl2);
        rlt2=findViewById(R.id.rlt2);
        r2=findViewById(R.id.r2);
        TextView foot=findViewById(R.id.foot);
        sharedPreferences1 = getSharedPreferences("rappel", Context.MODE_PRIVATE);

        SharedPreferences donnes = getSharedPreferences("Admin", Context.MODE_PRIVATE);
        idAdmin = donnes.getString("id", "");

        String messagejour = sharedPreferences1.getString("messagejour", "Aucun rappel n'a été planifier");
        String messagesemaine = sharedPreferences1.getString("messagesemaine", "");
        String messagemois = sharedPreferences1.getString("messagemois", "");
        String selectedDayOfWeek = sharedPreferences1.getString("selectedDayOfWeek", "");
        String selectedDayOfMonth = sharedPreferences1.getString("selectedDayOfMonth", "");

        String messageparseconde = sharedPreferences1.getString("messageparseconde", "");
        String listedesnumeros = sharedPreferences1.getString("lesnumeros", "");

        if (!listedesnumeros.isEmpty() || !messageparseconde.isEmpty()){
             recipients = recupererNumeros(listedesnumeros);
            scheduleSMSEvery30Seconds(messageparseconde,recipients);
            System.out.println("vnvcbncvnbnbcnbcnbvcnnvcnvcvncnvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvb "+recipients);
        }
       

        if (!selectedDayOfMonth.isEmpty()){
            scheduleSMSOnWeek(selectedDayOfWeek,messagesemaine,recipients);
            scheduleSMSOnSelectedDate(selectedDayOfMonth,messagemois,recipients);
            scheduleSMSDaily(messagejour,recipients);
        }

        profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, DetailAdmin.class));
            }
        });

        foot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Bricefile.class));
            }
        });
        rl1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,List_of_tenants.class));
            }
        });
        rl2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,Historique.class));
            }
        });
        rlt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Creat_new_tenant.class));
            }
        });
        r2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Creat_new_tenant.class));
            }
        });
        // Créer un Handler pour envoyer périodiquement le SMS
        /*handler = new Handler();
        handler.postDelayed(sendSMSRunnable, INTERVAL_MS);*/
        if (checkPermission()) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();

        }

    }

    private List<String> recupererNumeros(String listedesnumeros) {
        String[] numerosArray = listedesnumeros.split(",");

        List<String> result = new ArrayList<>();
        for (String numero : numerosArray) {
            result.add(numero.trim());
        }
        return result;
    }

    private void scheduleSMSEvery30Seconds(String message, List<String> recipients) {
        System.out.println("BVVCBNNBVCBNVCVCNBVBNCVBNVCBNBNVCBNVCBNVCBNCVBNCVBNVCBNVCBNBNCVN "+recipients);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putStringArrayListExtra("recipients", (ArrayList<String>) recipients);
        intent.putExtra("message", message);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent,  PendingIntent.FLAG_IMMUTABLE);

        // Utilisez AlarmManager pour planifier l'envoi de SMS toutes les 30 secondes
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP, // Type d'alarme : RTC_WAKEUP permet de réveiller l'appareil s'il est en veille
                System.currentTimeMillis(), // Début de l'alarme (maintenant)
                30 * 1000, // Intervalle entre les répétitions (30 secondes)
                pendingIntent
        );
    }


    private void scheduleSMSDaily(String messagejour, List<String> recipients) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putStringArrayListExtra("recipients", (ArrayList<String>) recipients);
        intent.putExtra("message", messagejour);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent,  PendingIntent.FLAG_IMMUTABLE);


        // Définissez l'heure à laquelle vous souhaitez envoyer le SMS chaque jour
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9); // Heure d'envoi du SMS (9h00)
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // Utilisez AlarmManager pour planifier l'envoi de SMS chaque jour à l'heure spécifiée
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }


    private void scheduleSMSOnSelectedDate(String selectedDate, String messagemois, List<String> recipients) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putStringArrayListExtra("recipients", (ArrayList<String>) recipients);
        intent.putExtra("message", messagemois);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent,  PendingIntent.FLAG_IMMUTABLE);


        // Définissez la date à laquelle vous souhaitez envoyer le SMS chaque mois
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());

            // Exemple : planifiez l'envoi de SMS chaque 25 du mois à l'heure spécifiée
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(selectedDate)); // Utilisez la date sélectionnée
            calendar.set(Calendar.HOUR_OF_DAY, 10); // Heure d'envoi du SMS (10h00)
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            // Utilisez AlarmManager pour planifier l'envoi de SMS chaque mois à la date spécifiée
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 30, pendingIntent);

    }


    private void scheduleSMSOnWeek(String selectedDayOfWeek, String messagesemaine, List<String> recipients) {
        int jour = getJourSemaineCalendrier(selectedDayOfWeek);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putStringArrayListExtra("recipients", (ArrayList<String>) recipients);
        intent.putExtra("message", messagesemaine);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent,  PendingIntent.FLAG_IMMUTABLE);
         // Définissez l'heure à laquelle vous souhaitez envoyer le SMS chaque mercredi
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_WEEK, jour);
            calendar.set(Calendar.HOUR_OF_DAY, 9); // Heure d'envoi du SMS (9h00)
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            // Utilisez AlarmManager pour planifier l'envoi de SMS chaque mercredi à l'heure spécifiée
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);

    }

    private int getJourSemaineCalendrier(String jourSemaine) {
        switch (jourSemaine) {
            case "Lundi":
                return Calendar.MONDAY;
            case "Mardi":
                return Calendar.TUESDAY;
            case "Mercredi":
                return Calendar.WEDNESDAY;
            case "Jeudi":
                return Calendar.THURSDAY;
            case "Vendredi":
                return Calendar.FRIDAY;
            case "Samedi":
                return Calendar.SATURDAY;
            case "Dimanche":
                return Calendar.SUNDAY;
            default:
                return -1;
        }
    }



    private void requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CO);
    }

    private boolean checkPermission() {
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // Demander la permission d'envoyer des SMS
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_CODE);
        } else {
            // Si les autorisations sont déjà accordées, envoyer un SMS
            sendSMS();
        }
    }

    // Méthode pour gérer la réponse de l'utilisateur à la demande d'autorisation
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Si l'utilisateur accorde la permission, envoyer le SMS
                sendSMS();
            } else {
                // Si l'utilisateur refuse la permission, afficher un message
                Toast.makeText(getApplicationContext(), "Permission refusée pour envoyer des SMS.", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                // after requesting permissions we are showing
                // users a toast message of permission granted.
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    // Méthode pour envoyer un SMS
    private void sendSMS() {
        List<String> phoneNumbers = new ArrayList<>();
        phoneNumbers.add("+2250504389406");
        phoneNumbers.add("+2250504463805");
        phoneNumbers.add("+2250576155481");

        String message = "En cette fin de mois, nous tenions à vous rappeler que le loyer pour ce mois est dû d'ici le 10 du mois prochain.Afin d'éviter tout retard.En cas de difficultés financières ou pour discuter de modalités de paiement alternatives, n'hésitez pas à nous contacter dès que possible. La transparence et la communication sont essentielles pour maintenir une relation locataire-propriétaire harmonieuse.";
        try {
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(message);
            for (String phoneNumber : phoneNumbers) {
                smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
            }

            Toast.makeText(getApplicationContext(), "SMS envoyés avec succès.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Erreur lors de l'envoi des SMS. Veuillez réessayer.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // Runnable pour envoyer le SMS périodiquement
    private final Runnable sendSMSRunnable = new Runnable() {
        @Override
        public void run() {
            sendSMS(); // Appeler la méthode pour envoyer le SMS
            handler.postDelayed(this, INTERVAL_MS); // Programmer le prochain envoi
        }
    };
    @Override
    public void onBackPressed() {
        incr++;
        if (incr==1){
            super.onBackPressed();
            //finish();
            finishAffinity();
        }
    }
}