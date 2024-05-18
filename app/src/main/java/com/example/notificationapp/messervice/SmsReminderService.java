package com.example.notificationapp.messervice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.SmsManager;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SmsReminderService extends Service {
    private static final long INTERVAL_MS = 2 * 60 * 1000; // 2 minutes
    private static final String MESSAGE = "Votre message de rappel ici.";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Logique d'envoi de message SMS ici
        sendReminderToUsers();

        // Renvoyer START_STICKY pour redémarrer le service en cas de fermeture inattendue
        return START_STICKY;
    }

    private void sendReminderToUsers() {
        // Obtenez la liste des numéros de téléphone de vos utilisateurs
        List<String> userPhoneNumbers = getUsersPhoneNumbers();
        String message = "En cette fin de mois, nous tenions à vous rappeler que le loyer pour le mois de mars est dû d'ici le 10/04/2024.Afin d'éviter tout retard.En cas de difficultés financières ou pour discuter de modalités de paiement alternatives, n'hésitez pas à nous contacter dès que possible. La transparence et la communication sont essentielles pour maintenir une relation locataire-propriétaire harmonieuse.";
        // Utilisez SmsManager pour envoyer des messages à chaque utilisateur
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> parts = smsManager.divideMessage(message);

        for (String phoneNumber : userPhoneNumbers) {
            smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
        }
    }

    private List<String> getUsersPhoneNumbers() {
        // Implémentez la logique pour obtenir la liste des numéros de téléphone des utilisateurs
        // Cette liste peut provenir d'une base de données, d'un fichier de configuration, etc.
        List<String> userPhoneNumbers = new ArrayList<>();
        // Ajoutez les numéros de téléphone à la liste
        userPhoneNumbers.add("+2250504389406");
        userPhoneNumbers.add("+2250504463805");
        userPhoneNumbers.add("+2250576155481");
        // Ajoutez autant de numéros que nécessaire
        return userPhoneNumbers;
    }
}

