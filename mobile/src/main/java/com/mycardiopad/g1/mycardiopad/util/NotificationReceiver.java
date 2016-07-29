package com.mycardiopad.g1.mycardiopad.util;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.mycardiopad.g1.mycardiopad.R;
import com.mycardiopad.g1.mycardiopad.activity.Activity_Main;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Programme;
import com.mycardiopad.g1.mycardiopad.database._Programme;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Serkan<br/>
 * Gestion des rappels de séances
 */

public class NotificationReceiver extends BroadcastReceiver {

    public static void scheduleAlarms(Context context) {

        Log.d("NotificationReceiver", "scheduleAlarms");

        SharedPreferences alarmTime;
        alarmTime = context.getSharedPreferences("userDetails", 0);
        int heure = alarmTime.getInt("heure", 10);
        int minute = alarmTime.getInt("minute", 30);

        Calendar alarmStartTime = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        alarmStartTime.set(Calendar.HOUR_OF_DAY, heure);
        alarmStartTime.set(Calendar.MINUTE, minute);
        alarmStartTime.set(Calendar.SECOND, 30);
        if (now.after(alarmStartTime)) {
            Log.d("Alarm", "Added a day");
            alarmStartTime.add(Calendar.DATE, 1);
        }

        Intent intentSchedule = new Intent(context, NotificationReceiver.class);

        PendingIntent pendingIntentSchedule = PendingIntent.getBroadcast(context, 100, intentSchedule, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntentSchedule);

        Log.d("NotificationReceiver", "Alarme at : " + Integer.toString(alarmStartTime.get(Calendar.HOUR_OF_DAY))
                + ":" + Integer.toString(alarmStartTime.get(Calendar.MINUTE)));
    }

    public static void sendNotif(Context context) {

        Log.d("NotificationReceiver", "sendNotif");

        //Programme
        MyDBHandler_Programme db;
        db = new MyDBHandler_Programme(context, null, null, 1);
        _Programme programme = db.lastRowProgramme();
        String exercice_Jours = programme.get_ExerciceJour();
        String[] jours = exercice_Jours.split("µ"); //Exercice concernant le jour
        String programmeJours = programme.get_ProgrammeJours(); //Récupération du programme des jours
        String[] joursDetail = programmeJours.split("/");     //Récupération du programme du Jour.
        Log.i("exercice_Jours", exercice_Jours);
        Log.i("jours", Arrays.toString(jours));
        Log.i("programmeJours", programmeJours);
        Log.i("joursDetail", Arrays.toString(joursDetail));

        //Jour de la semaine
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 2; //Commence à 1; Récupération du jour courant
        Log.i("Jour", Integer.toString(day));

        if (jours[day].equals("1")) {    //Send notification

            Log.i("if sport", "we're in");

            String msg = "Aujourd'hui, vous avez ";

            String[] exerciceDuJour = joursDetail[day].split("µ");
            Log.i("exerciceDuJour", Arrays.toString(exerciceDuJour));
            Log.i("exerciceDuJour longeur", Integer.toString(exerciceDuJour.length));

            int nbjours = (exerciceDuJour.length) / 3;
            Log.i("nbjours", Integer.toString(nbjours));

            int i = 0, z = 0;
            String[] sport = new String[nbjours];
            String[] temps = new String[nbjours];
            Log.i("sport", Integer.toString(sport.length));
            Log.i("temps", Integer.toString(temps.length));

            while (z < nbjours) {

                Log.i("while", "we're in");
                temps[z] = exerciceDuJour[i + 2];
                Log.i("temps[z]", exerciceDuJour[i + 2]);
                sport[z] = exerciceDuJour[i + 1];
                Log.i("sport[z]", exerciceDuJour[i + 1]);
                msg = msg + temps[z] + " min de " + sport[z] + ", ";
                Log.i("msg", msg);
                z++;
                i = i + 3;
                Log.i("z++", Integer.toString(z));
                Log.i("i=i+3", Integer.toString(i));
            }

            msg = msg + "courage !";
            Log.i("MSG", msg);

            String dayLongName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
            Log.i("jour", dayLongName);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Intent intentSend = new Intent(context, Activity_Main.class);
            intentSend.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntentSend = PendingIntent.getActivity(context, 100, intentSend, PendingIntent.FLAG_UPDATE_CURRENT);

            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            /*NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setContentIntent(pendingIntentSend)
                    .setSmallIcon(R.drawable.ic_notif)
                    .setContentTitle("Rappel séance d'éffort du "+dayLongName+" - MyCardioPad")
                    .setContentText(msg)
                    .setSound(soundUri)
                    .setColor(Color.RED)
                    .setPriority(1)
                    .setAutoCancel(true);
            notificationManager.notify(100, builder.build());*/

            Notification.Builder builder = new Notification.Builder(context);
            builder.setContentIntent(pendingIntentSend)
                    .setContentTitle("Rappel séance d'éffort du " + dayLongName + " - MyCardioPad")
                    .setContentText(msg)
                    .setSmallIcon(R.drawable.ic_notif)
                    .setColor(Color.RED)
                    .setSound(soundUri)
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_HIGH);
            Notification notification = new Notification.BigTextStyle(builder)
                    .bigText(msg).build();

            notificationManager.notify(100, notification);

        } else {  //dont send notification
            Log.i("If sport", "no sport");
        }

    }

    public static void stopNotif(Context context) {

        Log.d("NotificationReceiver", "stopNotif");
        Intent intentStop = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntentStop = PendingIntent.getBroadcast(context, 100, intentStop, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntentStop);
        pendingIntentStop.cancel();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(100);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("NotificationReceiver", "onReceive");
        this.sendNotif(context);
    }
}
