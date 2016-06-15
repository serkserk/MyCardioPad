package com.mycardiopad.g1.mycardiopad.util;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.mycardiopad.g1.mycardiopad.activity.Activity_Wear_Main;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Capture_Wear;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Compte_Wear;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Programme_Wear;
import com.mycardiopad.g1.mycardiopad.database._Capture_Wear;
import com.mycardiopad.g1.mycardiopad.database._Compte_Wear;
import com.mycardiopad.g1.mycardiopad.database._Programme_Wear;

/**
 * Réalisé par nicolassalleron le 18/01/16. <br/>
 * Service de synchronisation entre la montre et le mobile <br/>
 */
public class SyncService extends WearableListenerService{

    //Base de données
    MyDBHandler_Capture_Wear dbCapture;
    MyDBHandler_Compte_Wear dbCompte;
    MyDBHandler_Programme_Wear dbProgramme;
    String response;
    GoogleApiClient apiClient;
    private boolean mainAfficher;


    @Override
    public void onCreate() {
        super.onCreate();
        apiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        apiClient.connect();
        dbCapture = new MyDBHandler_Capture_Wear(getApplicationContext(), null);
        dbCompte = new MyDBHandler_Compte_Wear(getApplicationContext(), null);
        dbProgramme = new MyDBHandler_Programme_Wear(getApplicationContext(), null);

        new Thread(new Runnable() {
            @Override
            public void run() {     //Récupère le programme tant qu'il n'y en a pas dans la montre
                //Attention il ne fait pas la mise à jour du programme pour le moment
                while (dbCompte.numberLine() == 0 || dbProgramme.numberLine()==0){
                    new SendToDataLayerThread("/messageService","CMPT",apiClient).start();
                    new SendToDataLayerThread("/messageService","PRGM",apiClient).start();
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


    }

    /**
     *  I = Retourne les identifiants des mesures
     *  M = Get, demande la capture pour un identifiant précis
     *  PRGM = Programme
     *  CMPT = Compte
     * @param messageEvent le message venant du mobile
     */
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equals("/messageAction")) {
            final String message = new String(messageEvent.getData());
            if(message.equals("ActivityLaunch")){
                mainAfficher = true;
            }
            if(!mainAfficher){  //Lance l'activité de capture

                Intent dialogIntent = new Intent(this, Activity_Wear_Main.class);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(dialogIntent);

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //On balance le message en broadcast
            Intent messageIntent = new Intent();
            messageIntent.setAction(Intent.ACTION_SEND);
            messageIntent.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);

        }else {
            if (messageEvent.getPath().equals("/messageService")) {
                final String message = new String(messageEvent.getData());
                String[] messageSplit = message.split("µ");

                switch (messageSplit[0]) {
                    case "I":
                        response = "Iµ";
                        long nbLine = dbCapture.numberLine();
                        for (int i = 0; i < nbLine; i++) {
                            //Log.e("id",""+dbCapture.getMesure(i).get_id());
                            response += String.valueOf(dbCapture.getMesure(i).get_id()) + 'µ';
                        }
                        break;
                    case "M": {

                        _Capture_Wear cap = dbCapture.getMesure(Integer.parseInt(messageSplit[1]));
                        response = "Mµ" + cap.get_id()
                                + "µ" + cap.getYear()
                                + "µ" + cap.getMonth()
                                + "µ" + cap.getDay()
                                + "µ" + cap.getStartRecord() //Token 0
                                + "$" + cap.get_cap() //Token 1
                                + "$" + cap.get_podo() //Token 2
                                + "$" + cap.getEndRecord() + "µ"; //Token 3

                        break;
                    }
                    case "L": {
                        _Capture_Wear cap = dbCapture.lastRowCapture();
                        response = "Mµ" + cap.get_id()
                                + "µ" + cap.getYear()
                                + "µ" + cap.getMonth()
                                + "µ" + cap.getDay()
                                + "µ" + cap.getStartRecord()
                                + "$" + cap.get_cap()
                                + "$" + cap.get_podo()
                                + "$" + cap.getEndRecord() + "µ";
                        break;
                    }
                    case "CMPT":
                        _Compte_Wear compte = new _Compte_Wear();
                        compte.set_id(1);
                        compte.set_email(messageSplit[2]);
                        compte.set_nom(messageSplit[3]);
                        compte.set_prenom(messageSplit[4]);
                        compte.set_sexe(Integer.parseInt(messageSplit[5]));
                        compte.set_taille(Integer.parseInt(messageSplit[6]));
                        compte.set_poids(Integer.parseInt(messageSplit[7]));
                        compte.set_compte_limite(Integer.parseInt(messageSplit[8]));
                        compte.set_total_session(Integer.parseInt(messageSplit[9]));
                        compte.set_total_pas(Integer.parseInt(messageSplit[10]));

                        dbCompte.addCompte(compte);
                        response = "";
                        break;
                    case "PRGM":
                        String messageProgramme = message.substring(5);

                        //noinspection UnusedAssignment
                        messageSplit = new String[messageProgramme.split("\\$").length];
                        messageSplit = messageProgramme.split("\\$");

                        _Programme_Wear programme = new _Programme_Wear();
                        programme.set_ExerciceJour(messageSplit[0]); //1 = Jour de Sport, 0 = Jour de repos

                        programme.set_DateDebutPrescription(Long.parseLong(messageSplit[1]));
                        programme.set_id(Integer.parseInt(messageSplit[2]));
                        programme.set_ProgrammeJours(messageSplit[3]); //Dimanche

                        programme.set_minFreq(Integer.parseInt(messageSplit[4]));
                        programme.set_maxFreq(Integer.parseInt(messageSplit[5]));
                        programme.set_id(dbProgramme.numberLine());

                        dbProgramme.addProgramme(programme);
                        response = "";


                        break;
                }
                new SendToDataLayerThread("/messageService", response, apiClient).start();

            } else {
                super.onMessageReceived(messageEvent);
            }
        }
    }

}