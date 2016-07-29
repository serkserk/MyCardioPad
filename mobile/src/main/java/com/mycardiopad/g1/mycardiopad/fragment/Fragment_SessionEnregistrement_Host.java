package com.mycardiopad.g1.mycardiopad.fragment;

/**
 * Réalisé par nicolassalleron le 17/01/16.  <br/>
 * Host qui prend en charge les trois élements de l'interface SessionEnregistrement  <br/>
 */


import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;
import com.mycardiopad.g1.mycardiopad.R;
import com.mycardiopad.g1.mycardiopad.adapter.Adapter_SessionEnregistrement;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Capture;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Compte;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Programme;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Succes;
import com.mycardiopad.g1.mycardiopad.database._Capture;
import com.mycardiopad.g1.mycardiopad.database._Succes;
import com.mycardiopad.g1.mycardiopad.util.Detection_Internet;
import com.mycardiopad.g1.mycardiopad.util.CustomToast;
import com.mycardiopad.g1.mycardiopad.util.OkHttpSingleton;
import com.mycardiopad.g1.mycardiopad.util.ServeurURL;
import com.mycardiopad.g1.mycardiopad.util.services.SendToDataLayerThread;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


@SuppressWarnings("ConstantConditions")
public class Fragment_SessionEnregistrement_Host extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, TextToSpeech.OnInitListener {

    private Adapter_SessionEnregistrement myPagerAdapter;
    private ViewPager myPager;
    private FloatingActionButton btnPauseStart;
    private boolean status = true;
    private GoogleApiClient api;
    private TextToSpeech tts;
    private MyDBHandler_Programme dbProgramme;
    private String time;
    public TextView mChronometer3;

    public static final int NOTIFICATION_ID = 1;

    private Timer timer;
    private long startTime;
    private long elapsed;
    private Snackbar snackbar;
    private MyDBHandler_Capture dbCapture;
    private int maxFreq = 170;
    private int minFreq = 50;

    public Bundle bundle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lancer_session_enregistrement_fragment_host, container, false);

        mChronometer3 = (TextView) v.findViewById(R.id.txtStopWatch3);



        //Définition du min, max et du temps
        dbProgramme = new MyDBHandler_Programme(getActivity(), null, null, 1);
        dbCapture = new MyDBHandler_Capture(getActivity(),null,null,1);
        bundle = getArguments();
        time = bundle.getString("time");
        if (time.equals("")) {
            time = "I"; // Dans le cas ou le temps n'est pas défini.
        }
        String fc = bundle.getString("mode");
        if ( !(fc.equals("Avec FC") ) )
            mChronometer3.setVisibility(View.GONE);

        tts = new TextToSpeech(getActivity(), this);    //Initialisation de la voix

        if(dbProgramme.numberLine() >0){
            maxFreq = dbProgramme.lastRowProgramme().get_maxFreq();
            minFreq = dbProgramme.lastRowProgramme().get_minFreq();
        }

        sendNotification();    //Notification

        MyDBHandler_Compte myDBHandler_compte = new MyDBHandler_Compte(getContext(), null, null, 1);
        final MyDBHandler_Succes myDBHandler_succes = new MyDBHandler_Succes(getContext(), null, null, 1);

        // Vérifie si le smartphone est connecté à Internet
        if (new Detection_Internet(getContext()).isConnect()) {

            //Retour du succès obtenu
            if (myDBHandler_succes.getSucces(2).get_obtenu() == 0) {
                RequestBody formBody = new FormBody.Builder()
                        .add("email", myDBHandler_compte.getCompte(0).get_email())
                        .add("success_id", "2")
                        .build();

                Request request_succes = new Request.Builder()
                        .url(ServeurURL.UPDATE_SUCCES)
                        .post(formBody)
                        .build();

                OkHttpSingleton.getInstance().newCall(request_succes).enqueue(new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            String status = new JSONObject(response.body().string()).getString("status");
                            if (status.equals("ok")) {
                                _Succes success = myDBHandler_succes.getSucces(2);
                                success.set_obtenu(1);
                                success.set_date_obtention(new Date());
                                myDBHandler_succes.updateSucces(success);
                                // Notification pour le deuxième succès
                                CustomToast succes = new CustomToast(getContext(), "Vous venez de débloquer un succès !"
                                        , R.drawable.ic_premiere_session);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

        //Mise en place du bouton
        btnPauseStart = (FloatingActionButton) v.findViewById(R.id.pause);
        btnPauseStart.setImageResource(R.drawable.ic_pause_circle_outline_white_24dp);
        btnPauseStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status) {   //Quand il est en pause
                    btnPauseStart.setImageResource(R.drawable.ic_play_circle_outline_white_48dp);
                    Snackbar.make(getView(), "Pause ! ", Snackbar.LENGTH_SHORT);
                    new SendToDataLayerThread("/messageAction", "S" + "µ" + //Pause vers la watch
                            "µ" +
                            "µ" +
                            "Pause !" + "µ", api).start();
                    status = false;
                    timer.cancel();
                    timer.purge();
                    tts.speak("Session en pause !", TextToSpeech.QUEUE_FLUSH, null);

                } else {    //Quand on reprend
                    status = true;
                    btnPauseStart.setImageResource(R.drawable.ic_pause_circle_outline_white_24dp);
                    Snackbar.make(getView(), "Reprise ! ", Snackbar.LENGTH_SHORT);
                    new SendToDataLayerThread("/messageAction", "R" + "µ" + //Reprise vers mobile
                            "µ" +
                            "µ" +
                            "Reprise !" + "µ", api).start();
                    timer = new Timer();
                    startTime = System.currentTimeMillis() - elapsed;
                    startTimer();
                    tts.speak("Reprise de la session !", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

        // Construction d'une nouvelle GoogleApiClient pour l'API de la montre
        api = new GoogleApiClient.Builder(getActivity())
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        //  Enregistrement du fragment pour qu'il revoit les données de la montre
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(messageReceiver, messageFilter);

        //  Mise en place Snackbar pour le lancement de la Session
        snackbar = Snackbar.make(getActivity().getCurrentFocus(),
                "", 4000);
        snackbar.setDuration(4000);
        snackbar.setCallback(new Snackbar.Callback() {
            @Override
            public void onShown(final Snackbar snackbar) {
                super.onShown(snackbar);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int i = 0;
                        while (i < 4) {
                            final int j = i;
                            if (isVisible()){
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (j == 3) {
                                            snackbar.setText("C'est parti ! ");

                                            timer = new Timer();
                                            startTime = System.currentTimeMillis();
                                            startTimer();
                                            startRecordWatch();

                                        } else {
                                            snackbar.setText("Démarrage de la session dans : " + (3 - j));
                                        }
                                    }
                                });
                            }
                            i++;
                            try {
                                Thread.sleep(1000); //Mise à jour
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }).start();
            }
        });
        snackbar.show();


        //Mise en place de l'adapteur pour les Ecran 1,2,3
        myPagerAdapter = new Adapter_SessionEnregistrement(this.getChildFragmentManager(), fc);
        myPager = (ViewPager) v.findViewById(R.id.SessionEnregistrementViewPager);
        myPager.setAdapter(myPagerAdapter);

        if(fc.equals("Avec FC"))    //On place les cercles au centre de la nouvelle vue
            myPager.setCurrentItem(0);

        //Mise en place du titre
        PagerTitleStrip pts = (PagerTitleStrip) v.findViewById(R.id.pager_title_strip);
        pts.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        pts.setTextSpacing(200);

        return v;
    }


    /**
     * Methode pour mettre en place un chronomètre via une méthode parente
     * @param chronometer le chronomètre à mettre en place
     */
    public void setChronometer(String  chronometer) {
        this.mChronometer3.setText(chronometer);
    }


    /**
     * Lancement de l'enregistrement sur la montre
     */
    private void startRecordWatch() {

        new SendToDataLayerThread("/messageAction", "G" + "µ" +
                maxFreq + "µ" +
                minFreq + "µ" +
                time + "µ" +
                "DESCRIPTION", api).start();
        tts.speak("Démarrage de la session ! Courage "+new MyDBHandler_Compte(getActivity(),null,null,1).getCompte(0).get_prenom(), TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * Gestionnaire d'initialisation du TTS
     * @param status le status de l'initialisation
     */
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.getDefault());

        } else {
            tts = null;
            Toast.makeText(getContext(), "Impossible d'initialiser le coach audio.", Toast.LENGTH_SHORT).show();
        }
    }




    /**
     * Récupération des données venant de la watch
     */
    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            final String[] messageSplit = message.split("µ");
            //On prévient l'utilisateur qu'il faut ralentir ou augmenter le FC
            if (messageSplit[0].equals("DIRECT")) {

                if (Long.parseLong(messageSplit[1]) > maxFreq) {
                    tts.speak("Veuillez ralentir", TextToSpeech.QUEUE_FLUSH, null);
                } else if (Long.parseLong(messageSplit[1]) < minFreq) {
                    tts.speak("Veuillez accélérer", TextToSpeech.QUEUE_FLUSH, null);
                }

            }else{ //Fin de la session, on enregistre

                tts.speak("Fin de la session", TextToSpeech.QUEUE_FLUSH, null);
                //Snackbar.make(getView(), "Enregistrement de la session !", Snackbar.LENGTH_SHORT).show();
                _Capture cap = new _Capture();
                cap.set_id(Long.valueOf(messageSplit[0]));
                cap.setYear(Integer.parseInt(messageSplit[1]));
                cap.setMonth(Integer.parseInt(messageSplit[2]));
                cap.setDay(Integer.parseInt(messageSplit[3]));

                StringTokenizer string = new StringTokenizer(messageSplit[4], "$");
                String s = string.nextToken();
                cap.setStartRecord(Long.valueOf(s));
                string = new StringTokenizer(message, "$");
                string.nextToken(); //Token 0

                s = string.nextToken(); //Token 1
                cap.set_cap(s);

                s = string.nextToken(); //Token 2
                cap.set_podo(s);

                s = string.nextToken();
                cap.setEndRecord(Long.parseLong(s.split("µ")[0]));

                dbCapture.addCapture(cap);
            }
        }
    }

    @Override
    public void onStop() {
        if (api != null && api.isConnected()) {
            api.disconnect();
        }
        tts.shutdown();
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (api != null)
            api.connect();
    }

    /**
     * Mise en place d'une notification pour indiquer qu'une session est en cours
     */
    public void sendNotification() {

        //Création de la notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext());

        //Permet de définir les éléments présents dans la notification
        builder.setContentTitle("MyCardioPad");
        builder.setContentText("Séance en cours !");
        builder.setSmallIcon(R.drawable.ic_account_circle_black_24dp);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

        //Permet de définir si la persistance de la notification
        builder.setOngoing(true);

        //Envoie la notification dans la barre de notification
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(getContext().NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    /**
     * On supprime le timer, et la snackbar si elle est affiché
     */
    @Override
    public void onDestroy() {
        super.onDestroyView();
        if(timer != null){
            timer.cancel();
            timer.purge();
            snackbar.dismiss();
        }

    }

    /**
     * Démarrage du Timer et mise à jour de l'interface
     */
    private void startTimer(){
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                try{
                    elapsed = System.currentTimeMillis() - startTime;

                    getActivity().runOnUiThread(new Runnable() {
                        @SuppressLint("DefaultLocale")
                        @Override
                        public void run() {
                            if ( bundle.getString("mode").equals("Avec FC") ){
                                mChronometer3.setText( String.format("%02d : %02d",
                                        TimeUnit.MILLISECONDS.toMinutes(elapsed),
                                        TimeUnit.MILLISECONDS.toSeconds(elapsed) -
                                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(elapsed))
                                )); }
                                else {
                                ((Fragment_SessionEnregistrement_Ecran1) myPagerAdapter.getFragment(0)).setChronometer(String.format("%02d : %02d",
                                        TimeUnit.MILLISECONDS.toMinutes(elapsed),
                                        TimeUnit.MILLISECONDS.toSeconds(elapsed) -
                                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(elapsed))
                                ));
                            }
                            }
                    });
                }catch(Exception e){ //Patch le plus dégueulasse du monde
                    Log.e("Fragment", "Le fragment est surement détruit");
                }

            }
        }, 0, 33);
    }
}