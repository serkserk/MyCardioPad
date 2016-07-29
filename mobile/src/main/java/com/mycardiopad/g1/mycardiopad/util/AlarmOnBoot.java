package com.mycardiopad.g1.mycardiopad.util;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Serkan<br/>
 * Relance l'alarme au démarrage (utilise après un reboot)<br/>
 */

public class AlarmOnBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("AlarmOnBoot", "onReceive");
        Intent intentTemp = new Intent(context, NotificationReceiver.class);
        intentTemp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        boolean alarmUp = (PendingIntent.getBroadcast(context, 100, intentTemp, PendingIntent.FLAG_NO_CREATE) != null);
        Log.d("AlarmOnBoot", alarmUp ? "true" : "false");

        if (alarmUp) {
            Log.d("AlarmOnBoot", "Alarm is already active");
        } else {
            Log.d("AlarmOnBoot", "Alarm is not active");
            NotificationReceiver.scheduleAlarms(context);
        }
    }
}
