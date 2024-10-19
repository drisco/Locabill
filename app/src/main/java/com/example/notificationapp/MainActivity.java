package com.example.notificationapp;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
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

import com.example.notificationapp.messervice.AlarmReceiver;
import com.example.notificationapp.messervice.MainBroadcastReceiver;
import com.example.notificationapp.messervice.RappelPlaning;

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
    ImageView profil;
    ImageView r2;
    String idAdmin;
    EditText etToken;
    List<String> recipients = new ArrayList<>();
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);

        }

        rl1=findViewById(R.id.rl1);
        profil=findViewById(R.id.profil);
        rl2=findViewById(R.id.rl2);
        rlt2=findViewById(R.id.rlt2);
        r2=findViewById(R.id.r2);
        TextView foot=findViewById(R.id.foot);
        sharedPreferences1 = getSharedPreferences("rappel", Context.MODE_PRIVATE);

        String messageparseconde = sharedPreferences1.getString("messageparseconde", "");
        String listedesnumeros = sharedPreferences1.getString("lesnumeros", "");

        SharedPreferences donnes = getSharedPreferences("Admin", Context.MODE_PRIVATE);
        idAdmin = donnes.getString("id", "");

        if (!messageparseconde.isEmpty() || !listedesnumeros.isEmpty()){
            Intent intent = new Intent(getApplicationContext(), MainBroadcastReceiver.class);
            intent.setAction("com.example.notificationapp.models.ACTION_CUSTOM");
            RappelPlaning.scheduleTest(this);

        }


        profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, DetailAdmin.class));
                finish();
            }
        });

        foot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*startActivity(new Intent(MainActivity.this, TestActivity.class));
                finish();*/
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

    /*private List<String> recupererNumeros(String listedesnumeros) {
        String[] numerosArray = listedesnumeros.split(",");

        List<String> result = new ArrayList<>();
        for (String numero : numerosArray) {
            result.add(numero.trim());
        }
        return result;
    }*/

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

    // Méthode pour gérer la réponse de l'utilisateur à la demande d'autorisation
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Si l'utilisateur accorde la permission, envoyer le SMS
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
   /* private void sendSMS() {
        List<String> phoneNumbers = new ArrayList<>();
        phoneNumbers.add("+2250504389406");
        phoneNumbers.add("+2250504463805");
        phoneNumbers.add("+2250576155481");
        System.out.println("XNNXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX "+phoneNumbers);

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
    };*/
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