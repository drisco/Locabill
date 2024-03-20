package com.example.notificationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;

import androidx.biometric.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Executor;

public class Login extends AppCompatActivity {
    TextView tvInputTip;
    ImageView iv_lock,iv_ok,iv_error,biometric;
    LinearLayout ll;
    int incr;
    private StringBuilder passwordBuilder = new StringBuilder();
     BiometricPrompt biometricPrompt;
     androidx.biometric.BiometricPrompt.PromptInfo promptInfo;
     Executor executor;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tvInputTip = findViewById(R.id.tv_input_tip);
        iv_lock = findViewById(R.id.iv_lock);
        iv_ok = findViewById(R.id.iv_ok);
        iv_error = findViewById(R.id.iv_error);
        ll = findViewById(R.id.ll);
        biometric = findViewById(R.id.numberB);
        biometric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ll.setVisibility(View.GONE);
                BiometricManager biometricManager = BiometricManager.from(Login.this);
                switch (biometricManager.canAuthenticate()){
                    case  BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                        Toast.makeText(Login.this, "Device doesn't have fingerprint", Toast.LENGTH_SHORT).show();
                        break;
                    case  BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                        Toast.makeText(Login.this, "Not working", Toast.LENGTH_SHORT).show();
                        //break;

                    case  BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                        Toast.makeText(Login.this, "No fingerprint assigned", Toast.LENGTH_SHORT).show();
                }

                    executor =ContextCompat.getMainExecutor(Login.this);
                    biometricPrompt= new BiometricPrompt(Login.this, executor, new BiometricPrompt.AuthenticationCallback() {
                        @Override
                        public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                            super.onAuthenticationError(errorCode, errString);
                        }

                        @Override
                        public void onAuthenticationSucceeded(@NonNull androidx.biometric.BiometricPrompt.AuthenticationResult result) {
                            super.onAuthenticationSucceeded(result);
                            startActivity(new Intent(Login.this,MainActivity.class));
                            finish();
                        }

                        @Override
                        public void onAuthenticationFailed() {
                            super.onAuthenticationFailed();
                        }

                    });

                    promptInfo= new BiometricPrompt.PromptInfo.Builder().setTitle("Déverrouiller App").setDescription("Connexion via empreint digital").setNegativeButtonText("Entrer Code Secret")
                            .build();
                    biometricPrompt.authenticate(promptInfo);

            }
        });
        /*scheduleSmsReminder();*/


        // Récupération des vues représentant les chiffres de la saisie du mot de passe
        TextView[] numberTextViews = new TextView[10];
        numberTextViews[0] = findViewById(R.id.number0);
        numberTextViews[1] = findViewById(R.id.number1);
        numberTextViews[2] = findViewById(R.id.number2);
        numberTextViews[3] = findViewById(R.id.number3);
        numberTextViews[4] = findViewById(R.id.number4);
        numberTextViews[5] = findViewById(R.id.number5);
        numberTextViews[6] = findViewById(R.id.number6);
        numberTextViews[7] = findViewById(R.id.number7);
        numberTextViews[8] = findViewById(R.id.number8);
        numberTextViews[9] = findViewById(R.id.number9);


        // Ajout des écouteurs de clics aux vues représentant les chiffres
        for (int i = 0; i < numberTextViews.length; i++) {
            final int digit = i; // Le chiffre représenté par cette vue
            numberTextViews[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    appendToPassword(String.valueOf(digit));
                }
            });
        }
    }

/*
    private void scheduleSmsReminder() {
        Intent intent = new Intent(this, SmsReminderService.class);
        PendingIntent pendingIntent = PendingIntent.getService(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                2 * 60 * 1000,
                pendingIntent
        );
    }
*/


    private void appendToPassword(String digit) {
        // Ajoutez le chiffre cliqué à la saisie du mot de passe
        passwordBuilder.append(digit);
        String maskedPassword = generateMaskedPassword(passwordBuilder.length());

        tvInputTip.setText(maskedPassword);
        // Vérifiez si la longueur de la saisie du mot de passe correspond à la longueur du mot de passe attendu
        if (passwordBuilder.length() == 4) {
            // Vérifiez si la saisie correspond au mot de passe attendu
            if (passwordBuilder.toString().equals("5556")) {
                iv_lock.setVisibility(View.GONE);
                iv_error.setVisibility(View.GONE);
                iv_ok.setVisibility(View.VISIBLE);
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                passwordBuilder.setLength(0);
                tvInputTip.setText("");
            } else {
                iv_lock.setVisibility(View.GONE);
                iv_ok.setVisibility(View.GONE);
                iv_error.setVisibility(View.VISIBLE);
                passwordBuilder.setLength(0);
                tvInputTip.setText("");

            }
        }else {
            iv_lock.setVisibility(View.VISIBLE);
            iv_ok.setVisibility(View.GONE);
            iv_error.setVisibility(View.GONE);
        }
    }

    private String generateMaskedPassword(int length) {
        StringBuilder maskedPasswordBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            maskedPasswordBuilder.append("*");
        }
        return maskedPasswordBuilder.toString();
    }

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