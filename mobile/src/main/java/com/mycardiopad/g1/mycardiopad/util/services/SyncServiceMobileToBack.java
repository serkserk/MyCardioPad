package com.mycardiopad.g1.mycardiopad.util.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Capture;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Compte;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Programme;
import com.mycardiopad.g1.mycardiopad.database._Capture;
import com.mycardiopad.g1.mycardiopad.database._Compte;
import com.mycardiopad.g1.mycardiopad.database._Programme;
import com.mycardiopad.g1.mycardiopad.util.OkHttpSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by nicolassalleron on 18/01/16.
 * Communication avec le backOffice
 */
public class SyncServiceMobileToBack extends Service {

    // TODO: 25/01/16 Faire plusieurs cas d'utilisation, synchronisation de base par exemple
    //Base de données
    MyDBHandler_Capture dbCapture;
    MyDBHandler_Compte dbCompte;



    @Override
    public void onCreate() {
        super.onCreate();

        dbCapture = new MyDBHandler_Capture(getApplicationContext(), null, null, 1);
        dbCompte = new MyDBHandler_Compte(getApplicationContext(), null, null, 1);
        final _Compte compte = dbCompte.lastRowCompte();
        //Log.e("DB", dbCapture.lastRowCapture().get_Capture());
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                   // Vérifier ici le nombre de captures
                    Request request = new Request.Builder()
                            .url("http://journaldesilver.com/api/get_capture_count/?email=" + compte.get_email())
                            .get()
                            .build();

                    OkHttpSingleton.getInstance().newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            try {
                                JSONObject json = new JSONObject(response.body().string());
                                String status = json.getString("status");

                                if (status.equals("ok")) {
                                    int count = json.getInt("count");

                                    if (count < dbCapture.numberLine()) {
                                        // On enregistre les différences
                                        for (int i = count; i < dbCapture.numberLine(); i++) {
                                            _Capture capture = dbCapture.getMesure(i);
                                            sendToBack(capture);
                                        }
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });

                    //Vérification de MAJ du programme de l'utilisateur
                    updateProgramme(compte.get_email());

                    try {
                        Thread.sleep(100000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendToBack(_Capture capture) {

        if (dbCompte.numberLine() > 0) {
            _Compte compte = dbCompte.lastRowCompte();

            RequestBody formBody = new FormBody.Builder()
                    .add("email", compte.get_email())
                    .add("capture[date]", capture.getYear() + "-" + capture.getMonth() + "-" + capture.getDay())
                    .add("capture[startRecord]", capture.getStartRecord() + "")
                    .add("capture[endRecord]", capture.getEndRecord() + "")
                    .add("capture[capture]", capture.get_cap())
                    .add("capture[podometre]", capture.get_podo())
                    .build();

            Request request = new Request.Builder()
                    .url("http://journaldesilver.com/api/add_captures/")
                    .post(formBody)
                    .build();

            OkHttpSingleton.getInstance().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    JSONObject json;
                    try {
                        json = new JSONObject(response.body().string());
                        String status = json.getString("status");

                        if (status.equals("ok")) {
                            Log.e("Enregistrement fini", "done");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });
        }
    }

    /**
     * Mise à jour du programme de l'utilisateur
     * @param email l'adresse email de l'utilisateur
     */
    private void updateProgramme(String email) {
        Request request = new Request.Builder()
                .url("http://journaldesilver.com/api/get_current_ordonnance/?email=" + email)
                .get()
                .build();

        OkHttpSingleton.getInstance().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("errorInitProgramme", "Erreur lors de la récupération du programme");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                MyDBHandler_Programme db_programme = new MyDBHandler_Programme(getApplicationContext(), null, null, 1);

                try {
                    JSONObject json = new JSONObject(response.body().string());
                    String status = json.getString("status");

                    if (status.equals("ok")) {
                        JSONObject ordonnance = json.getJSONObject("ordonnance");

                        Date date_debut = new SimpleDateFormat("yyyy-MM-dd").parse(ordonnance.getString("dateDebut"));
                        if (db_programme.numberLine() > 0) {
                            _Programme programme = db_programme.lastRowProgramme();

                            if (programme.get_DateDebutPrescription() < date_debut.getTime()) {

                                programme.set_id(db_programme.numberLine());
                                programme.set_DateDebutPrescription(date_debut.getTime());
                                programme.set_maxFreq(ordonnance.getInt("maxFreq"));
                                programme.set_minFreq(ordonnance.getInt("minFreq"));
                                programme.set_ProgrammeJours(ordonnance.getString("exerciceJour"));
                                programme.set_ExerciceJour(ordonnance.getString("programmeJours"));

                                db_programme.updateProgramme(programme);
                            }
                        }
                        else {
                            _Programme programme = new _Programme();

                            programme.set_id(db_programme.numberLine());
                            programme.set_DateDebutPrescription(date_debut.getTime());
                            programme.set_maxFreq(ordonnance.getInt("maxFreq"));
                            programme.set_minFreq(ordonnance.getInt("minFreq"));
                            programme.set_ProgrammeJours(ordonnance.getString("exerciceJour"));
                            programme.set_ExerciceJour(ordonnance.getString("programmeJours"));

                            db_programme.addProgramme(programme);
                        }
                    }


                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}