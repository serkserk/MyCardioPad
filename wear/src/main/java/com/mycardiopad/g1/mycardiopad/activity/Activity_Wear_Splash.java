package com.mycardiopad.g1.mycardiopad.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.mycardiopad.g1.mycardiopad.R;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Compte_Wear;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Programme_Wear;
import com.mycardiopad.g1.mycardiopad.util.SyncService;

/**
 * Réalisé par nicolassalleron le 14/02/16.  <br/>
 * Affichage du numéro de version  <br/>
 * Récupération des bases de données en provenance du mobile <br/>
 */
public class Activity_Wear_Splash extends Activity {

    private MyDBHandler_Compte_Wear dbCompte;
    private MyDBHandler_Programme_Wear dbProgramme;
    String point = ".";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); //Pas de titre
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, //En plein écran
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash); //Attachement du layout
        final TextView tv = (TextView) findViewById(R.id.textView3);


        startService(new Intent(this, SyncService.class)); //Récupération des informations du mobile
        dbCompte = new MyDBHandler_Compte_Wear(getApplicationContext(), null);
        dbProgramme = new MyDBHandler_Programme_Wear(getApplicationContext(), null);

        if(dbCompte.numberLine()==0 || dbProgramme.numberLine() == 0){  //Vérification si présence ou non d'un programme et d'un compte
            new Thread(new Runnable() {
                @Override
                public void run() {

                    while (dbCompte.numberLine()==0 && dbProgramme.numberLine() == 0){
                        if(getResources().getString(R.string.attente_splash).length()+3
                                == tv.getText().length()){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv.setText(R.string.attente_splash);
                                }
                            });

                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv.append(point);
                                }
                            });

                        }
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Intent i = new Intent(Activity_Wear_Splash.this, Activity_Selection.class);

                    startActivity(i);
                    //Fermeture du Splash Screen
                    finish();
                }
            }).start();
        }else {
            start();
        }



    }

    /**
     * Lancement de l'application
     */
    private void start() {
        int splashInterval = 1000;
        new Handler().postDelayed(new Runnable() { //New Runnable qui sera activé après 1 seconde


            @Override
            public void run() {
                // TODO Auto-generated method stub
                Intent i = new Intent(Activity_Wear_Splash.this, Activity_Selection.class);
                startActivity(i);


                //Fermeture du Splash Screen
                this.finish();
            }

            private void finish() {
                // TODO Auto-generated method stub

            }
        }, splashInterval);
    }
}