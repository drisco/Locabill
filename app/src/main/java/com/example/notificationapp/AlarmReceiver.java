package com.example.notificationapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        // Récupérer les données supplémentaires de l'intent
        List<String> recipients1 = intent.getStringArrayListExtra("recipients1");
        List<String> recipients = intent.getStringArrayListExtra("recipients");
        String message = intent.getStringExtra("message");
        String ok = intent.getStringExtra("ok");

        // Envoyer le SMS à chaque destinataire
        for (String recipient : recipients) {
            SmsManager smsManager = SmsManager.getDefault();
            //smsManager.sendTextMessage(recipient, null, message, null, null);
        }

        // Vous pouvez ajouter d'autres actions ici en fonction de vos besoins
    }
}


