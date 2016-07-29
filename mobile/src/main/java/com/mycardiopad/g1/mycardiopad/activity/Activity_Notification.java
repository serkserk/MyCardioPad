package com.mycardiopad.g1.mycardiopad.activity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mycardiopad.g1.mycardiopad.R;
import com.mycardiopad.g1.mycardiopad.util.NotificationReceiver;

/**
 * Réalisé par Serkan <br/>
 * Réglages des notifications de rappel de séances <br/>
 */

public class Activity_Notification extends AppCompatActivity {

    Toolbar toolbar;
    TextView state_notif;
    Switch switch_notif;
    LinearLayout linearLayout;
    TextView heures, minutes;
    Button set;

    SharedPreferences alarmTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Paramètre notification");

        state_notif = (TextView) findViewById(R.id.state_notif);
        switch_notif = (Switch) findViewById(R.id.switch_notif);
        linearLayout = (LinearLayout) findViewById(R.id.rappel);
        heures = (TextView) findViewById(R.id.rappel_heure);
        minutes = (TextView) findViewById(R.id.rappel_minute);
        set = (Button) findViewById(R.id.timepicker_launch);

        Intent intentTemp = new Intent(getApplicationContext(), NotificationReceiver.class);
        intentTemp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        boolean alarmUp = (PendingIntent.getBroadcast(getApplicationContext(), 100, intentTemp, PendingIntent.FLAG_NO_CREATE) != null);
        Log.d("Activity_Notification", alarmUp ? "true" : "false");

        if (alarmUp) {
            Log.d("Activity_Notification", "Alarm is already active");
            switch_notif.setChecked(true);
            state_notif.setText("Notification activé");
        } else {
            Log.d("Activity_Notification", "Alarm is not active");
            switch_notif.setChecked(false);
            state_notif.setText("Notification désactivé");
            linearLayout.setVisibility(View.GONE);
        }

        alarmTime = getSharedPreferences("userDetails", 0);
        SharedPreferences.Editor alarmTimeEdit = alarmTime.edit();
        alarmTimeEdit.putInt("heure", 10);
        alarmTimeEdit.putInt("minute", 30);
        alarmTimeEdit.apply();
        heures.setText("10");
        minutes.setText("30");

        switch_notif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (switch_notif.isChecked()) {
                    state_notif.setText("Notification activé");
                    NotificationReceiver.scheduleAlarms(getApplicationContext());
                    linearLayout.setVisibility(View.VISIBLE);
                    SharedPreferences.Editor alarmTimeEdit = alarmTime.edit();
                    alarmTimeEdit.putInt("heure", 10);
                    alarmTimeEdit.putInt("minute", 30);
                    alarmTimeEdit.apply();
                    heures.setText("10");
                    minutes.setText("30");
                    Toast.makeText(getApplicationContext(), "Rappel activé", Toast.LENGTH_SHORT).show();
                } else {
                    state_notif.setText("Notification désactivé");
                    NotificationReceiver.stopNotif(getApplicationContext());
                    linearLayout.setVisibility(View.INVISIBLE);

                    SharedPreferences.Editor alarmTimeEdit = alarmTime.edit();
                    alarmTimeEdit.clear();
                    alarmTimeEdit.apply();
                }
            }
        });

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(view);
            }
        });
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, 10, 30,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String heureS;
            String minuteS;

            if (hourOfDay < 10) {
                heureS = "0" + Integer.toString(hourOfDay);
            } else {
                heureS = Integer.toString(hourOfDay);
            }
            if (minute < 10) {
                minuteS = "0" + Integer.toString(minute);
            } else {
                minuteS = Integer.toString(minute);
            }
            heures.setText(heureS);
            minutes.setText(minuteS);

            SharedPreferences.Editor alarmTimeEdit = alarmTime.edit();
            alarmTimeEdit.putInt("heure", hourOfDay);
            alarmTimeEdit.putInt("minute", minute);
            alarmTimeEdit.apply();

            NotificationReceiver.stopNotif(getApplicationContext());
            NotificationReceiver.scheduleAlarms(getApplicationContext());

            Toast.makeText(getApplicationContext(), "Heure de rappel modifié", Toast.LENGTH_SHORT).show();
        }
    }
}
