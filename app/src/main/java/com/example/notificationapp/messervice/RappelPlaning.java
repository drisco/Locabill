package com.example.notificationapp.messervice;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;

public class RappelPlaning {

    public static void schedulepaiement(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MainBroadcastReceiver.class);
        intent.setAction("com.example.notificationapp.ACTION_CUSTOM");
        intent.putExtra("reference", "nouveaupaiement");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_IMMUTABLE);

        // Calculer le temps pour la première diffusion
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 1);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000, pendingIntent);
    }
    public static void scheduleChaquefindumois(Context context) {
        System.out.println("RAPELRAPELLELLELELELELELLELLELELELELELELLELELELELEL");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MainBroadcastReceiver.class);
        intent.setAction("com.example.notificationapp.ACTION_CUSTOM");
        intent.putExtra("reference", "chaquefindumois");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_IMMUTABLE);

        // Calculer le temps pour la fin du mois
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 0);

        // Si la date calculée est dans le passé, avancez d'un mois
        if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
            calendar.add(Calendar.MONTH, 1);
        }

        // Planifier l'alarme pour la fin du mois
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public static void scheduleChaqueUneMunite(Context context) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction("com.example.notificationapp.ACTION_CUSTOM");
        intent.putExtra("reference", "chaqueuneminute");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_IMMUTABLE);

        // Calculer le temps pour la première diffusion
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, 5);

        // Planifier les répétitions toutes les 5 minutes
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),   5 * 60 * 1000, pendingIntent);
    }

    public static void scheduleChaquelundi(Context context) {
        System.out.println("RAPELRAPELLELLELELELELELLELLELELELELELELLELELELELEL");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Créer une intention pour lancer le BroadcastReceiver
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction("com.example.notificationapp.ACTION_WEEKLY_REMINDER");
        intent.putExtra("reference", "chaquelundi");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_IMMUTABLE);

        // Créer un calendrier pour le prochain lundi à 6h du matin
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        // Si l'heure actuelle est après 6h du matin, avancez d'une semaine pour le prochain lundi
        if (Calendar.getInstance().after(calendar)) {
            calendar.add(Calendar.DATE, 7);
        }

        // Planifier une alarme hebdomadaire chaque lundi à 6h du matin
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
    }
    public static void scheduleApartirde25jusquafin(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Obtenez la date actuelle
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTimeInMillis(System.currentTimeMillis());

        // Configurez le calendrier pour le 25ème jour du mois actuel à 6h00
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.DAY_OF_MONTH, 25);
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // Si la date est déjà passée, avancez d'un mois
        if (currentDate.get(Calendar.DAY_OF_MONTH) >= 25) {
            calendar.add(Calendar.MONTH, 1);
        }

        // Planifier une alarme chaque jour à 6h00 à partir du 25ème jour jusqu'à la fin du mois
        while (calendar.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH)) {
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.setAction("com.example.notificationapp.ACTION_CUSTOM");
            intent.putExtra("reference", "messagemois");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_IMMUTABLE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            // Avancez d'un jour
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    public static void scheduleTest(Context context) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MainBroadcastReceiver.class);
        intent.setAction("com.example.notificationapp.ACTION_CUSTOM");
        intent.putExtra("reference", "test");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_IMMUTABLE);

        // Calculer le temps pour la première diffusion
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, 1);

        // Planifier les répétitions toutes les 5 minutes
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),  60 * 1000, pendingIntent);
    }


}

