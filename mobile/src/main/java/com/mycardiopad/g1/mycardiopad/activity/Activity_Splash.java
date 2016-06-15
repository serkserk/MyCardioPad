package com.mycardiopad.g1.mycardiopad.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.mycardiopad.g1.mycardiopad.R;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Compte;
import com.mycardiopad.g1.mycardiopad.util.Logger;
import com.mycardiopad.g1.mycardiopad.util.services.SyncServiceMobileToBack;
import com.mycardiopad.g1.mycardiopad.util.services.SyncServiceMobileToWatch;

import java.util.ArrayList;


/**
 * Réalisé par nicolassalleron le 11/06/15.  <br/>
 * Ecran de démarrage de l'application. <br/>
 * Permet de vérifier les permissions de l'utilisateur vis à vis du système <br/>
 * Permet de vérifier si l'utilisateur possède un compte sur l'application ou non <br/>
 */
public class Activity_Splash extends Activity{

    private String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_CONTACTS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA};
    boolean show = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); //Pas de titre
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, //En plein écran
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash); //Attachement du layout
        //On set les paramètres par défault
        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.preferences, false);

        //Vérification des permissions
        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }else{
           start();
        }

    }

    /**
     * Permet le démarrage du reste de l'application si l'ensemble des permissions sont acceptés
     */
    private void start() {
        String TAG = "Activity_Splash";
        Logger.write(TAG,"Lancement Application");
        startService(new Intent(this, SyncServiceMobileToWatch.class));
        startService(new Intent(this, SyncServiceMobileToBack.class));
        final MyDBHandler_Compte dbHandler_compte = new MyDBHandler_Compte(this,"",null,1);
        View parentLayout = findViewById(R.id.root_splash);
        final Class myclass;
        if(dbHandler_compte.numberLine() == 0){
            Snackbar.make(parentLayout,"Pas de compte actuellement",Snackbar.LENGTH_SHORT).show();
            myclass = Activity_Login.class;
        }
        else{
            Snackbar.make(parentLayout,"Chargement de vos informations",Snackbar.LENGTH_SHORT).show();
            myclass = Activity_Main.class;
        }
        int splashInterval = 2000;
        new Handler().postDelayed(new Runnable() { //New Runnable qui sera activé après 1seconde


            @Override
            public void run() {
                // TODO Auto-generated method stub
                Intent i = new Intent(Activity_Splash.this, myclass);
                startActivity(i);


                //Fermeture du Splash Screen
                Activity_Splash.this.finish();
                    }


        }, splashInterval);
    }

    /**
     * Vérification des différentes permissions
     * @param context le contexte de l'application
     * @param permissions les différentes permissions
     * @return vrai si l'ensemble est accepté faux sinon
     */
    public boolean hasPermissions(Context context, String... permissions) {
        //Recherche des différentes permissions
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                Log.e("CheckFor : ",permission);
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Résultat de la requête des permissions
     * @param requestCode code de la requête
     * @param permissions nom de la permission
     * @param grantResults résultat venant du systeme de l'acceptation ou non de la permission
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull final String permissions[], @NonNull int[] grantResults) {

        ArrayList<String> permRestante = new ArrayList<>();

        for (int i = 0; i < permissions.length; i++){
            if(grantResults[i]==PackageManager.PERMISSION_DENIED)
                permRestante.add(permissions[i]);
        }

        //Construction du tableau des permissions refusées restante
        String [] askPermission = new String[permRestante.size()];
        for (int i = 0; i< permRestante.size(); i++){
            askPermission[i]= permRestante.get(i);
        }

        if(askPermission.length>0){
            showDialogOK(askPermission);
        }
        if(permRestante.size()==0)
            start();

    }

    /**
     * Affichage d'un dialog pour insister sur la permission
     * @param i la permission
     */
    private void showDialogOK(final String[] i) {

        show = true;

        DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        dialog.dismiss();
                        ActivityCompat.requestPermissions(Activity_Splash.this, i, 1);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        Toast.makeText(getApplicationContext(), "Il est nécessaire d'accepter les permissions dans les préférences ou de relancer l'application.", Toast.LENGTH_LONG).show();
                        finish();
                        break;
                }
            }
        };

        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setMessage("Il est nécessaire d'accepter les permissions pour le bon fonctionnement de l'application.")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener);

        dialog.create();
        dialog.show();



    }


}