package com.mycardiopad.g1.mycardiopad.util.services;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Capture;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Compte;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Programme;
import com.mycardiopad.g1.mycardiopad.database._Capture;
import com.mycardiopad.g1.mycardiopad.database._Compte;
import com.mycardiopad.g1.mycardiopad.database._Programme;

import java.util.StringTokenizer;

/**
 * Réalisé par nicolassalleron le 18/01/16.
 * Communication vers la montre (synchronisation des bases de données)
 */
public class SyncServiceMobileToWatch extends WearableListenerService{

    //Base de données
    MyDBHandler_Capture dbCapture;
    //Connexion avec le client
    GoogleApiClient apiClient;

    /**
     * Création du service
     */
    @Override
    public void onCreate() {
        super.onCreate();
        apiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        apiClient.connect();
        dbCapture = new MyDBHandler_Capture(getApplicationContext(), null, null, 1);

        new Thread(new Runnable() {
            @SuppressWarnings("InfiniteLoopStatement")
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(60000); // La montre est intérroger toute les minutes
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    new SendToDataLayerThread("/messageService", "I", apiClient).start();
                }
            }
        }).start();
    }

    /**
     * Les messages reçus de la montre
     * @param messageEvent le message venant la montre
     */
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        if (messageEvent.getPath().equals("/messageAction")) {

            final String message = new String(messageEvent.getData());

            //On balance le message en broadcast
            Intent messageIntent = new Intent();
            messageIntent.setAction(Intent.ACTION_SEND);
            messageIntent.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);

        }else if(messageEvent.getPath().equals("/messageService")){
            //Traitement de la base de données
          /*
            I = Les identifiants des différents enregistrements sont retourné
            M = Ajoute dans la base de données suivant les identifiants
          */
            final String message = new String(messageEvent.getData());
            //Log.e("MessageService",message);
            String[] messageSplit = message.split("µ");
            switch (messageSplit[0]) {
                case "I":
                    for (int i = 1; i < messageSplit.length; i++) {
                        boolean bol = dbCapture.isExist(Integer.valueOf(messageSplit[i]));

                        if (!bol) {
                            String mes = "Mµ" + messageSplit[i];
                            new SendToDataLayerThread("/messageService", mes, apiClient).start(); //Demande des enregistrements pour les identifiants manquant
                        }
                    }
                    break;
                case "M":   //Ajout des enregistrements pour les identifiants manquant

                    _Capture cap = new _Capture();
                    cap.set_id(Long.valueOf(messageSplit[1]));
                    cap.setYear(Integer.parseInt(messageSplit[2]));
                    cap.setMonth(Integer.parseInt(messageSplit[3]));
                    cap.setDay(Integer.parseInt(messageSplit[4]));

                    StringTokenizer string = new StringTokenizer(messageSplit[5], "$");
                    String s = string.nextToken();
                    cap.setStartRecord(Long.valueOf(s));
                    string = new StringTokenizer(message, "$");
                    string.nextToken(); //Token 0

                    s = string.nextToken(); //Token 1
                    cap.set_cap(s);

                    s = string.nextToken(); //Token 2
                    cap.set_podo(s);

                    try{
                        s = string.nextToken();
                        cap.setEndRecord(Long.parseLong(s.split("µ")[0]));
                    }catch (Exception e){   //Bug dans le cas ou l'utilisateur n'a pas marché une seule fois !
                        cap.setEndRecord(Long.parseLong(cap.get_podo().split("µ")[0]));
                    }



                    //noinspection SynchronizeOnNonFinalField
                    synchronized (dbCapture) {
                        dbCapture.addCapture(cap);
                    }

                    break;
                case "CMPT":
                    if (new MyDBHandler_Compte(getApplicationContext(), null, null, 1).numberLine() != 0) {
                        _Compte compte = new MyDBHandler_Compte(getApplicationContext(), null, null, 1).getCompte(0);
                        String rep = "CMPTµ" +
                                1 + "µ" + compte.get_email() + "µ"
                                + compte.get_nom() + "µ"
                                + compte.get_prenom() + "µ"
                                + compte.get_sexe() + "µ"
                                + compte.get_taille() + "µ"
                                + compte.get_poids() + "µ"
                                + compte.get_compte_limite() + "µ"
                                + compte.get_total_session() + "µ"
                                + compte.get_total_pas() + "µ";
                        new SendToDataLayerThread("/messageService", rep, apiClient).start();
                    }
                    break;
                case "PRGM":
                    if (new MyDBHandler_Programme(getApplicationContext(), null, null, 1).numberLine() != 0) {
                        _Programme programme = new MyDBHandler_Programme(getApplicationContext(), null, null, 1).lastRowProgramme();
                        String rep = "PRGMµ"
                                + programme.get_ExerciceJour() + "$"
                                + programme.get_DateDebutPrescription() + "$"
                                + programme.get_id() + "$"
                                + programme.get_ProgrammeJours() + "$"
                                + programme.get_minFreq() + "$"
                                + programme.get_maxFreq() + "$";

                        new SendToDataLayerThread("/messageService", rep, apiClient).start();
                    }
                    break;
            }
        } else {
            super.onMessageReceived(messageEvent);
        }
    }

}