package com.mycardiopad.g1.mycardiopad.fragment;

/**
 * Réalisé par nicolassalleron le 17/01/16. <br/>
 * Affichage des cercles qui change suivant la communication avec la montre  <br/>
 */


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mycardiopad.g1.mycardiopad.R;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Programme;
public class Fragment_SessionEnregistrement_Ecran2 extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private int maxFreq = 170;
    private int minFreq = 50;
    private float rate ;
    View v;
    private ImageView imgMin, imgMax, imgRate;
    private int imgMaxWidth, imgMaxHeight;
    private int imgMinWidth;
    private int imgMinHeight;
    Handler uiHandler;
    LinearLayout.LayoutParams params;
    private TextView rateTv;
    Thread updateUI;
    boolean alive;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_lancer_session_enregistrement_fragment_cercle, container, false);
        MyDBHandler_Programme programme = new MyDBHandler_Programme(getActivity(),null,null,1);
        if(programme.numberLine() >0){
            maxFreq = programme.lastRowProgramme().get_maxFreq();
            minFreq = programme.lastRowProgramme().get_minFreq();
        }
        rate = 100; //Placement par défaut en attendant la montre
        alive = true;

        imgMin = (ImageView) v.findViewById(R.id.img_cercle_yellow);
        imgMax = (ImageView) v.findViewById(R.id.img_cercle_red);
        imgRate = (ImageView) v.findViewById(R.id.img_cercle_bleu);
        rateTv = (TextView) v.findViewById(R.id.rate);

        uiHandler = new Handler();

        //Calcul initial de la valeur des imagesView
        CalculValeursImagesViews();

        //Première mise à jour des imagesViews suivant la fréquence min max de l'utilisateur
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imgRate.getLayoutParams();
                params.width = (int) ((imgMaxWidth *rate)/ maxFreq);
                params.height = (int) ((imgMaxHeight *rate)/ maxFreq);
                imgRate.setLayoutParams(params);
                params = (LinearLayout.LayoutParams) imgMin.getLayoutParams();
                params.width = (imgMaxWidth *minFreq)/ maxFreq;
                params.height = (imgMaxHeight *minFreq)/ maxFreq;
                imgMin.setLayoutParams(params);
                Log.e("imgMin","width :"+((imgMaxWidth *minFreq)/ maxFreq)+" height : "+((imgMaxHeight *minFreq)/ maxFreq));
            }
        });


        // Enregistrement du fragment pour qu'il revoit les données de la montre
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(messageReceiver, messageFilter);

        //Thread d'update de l'UI
       updateUI= new Thread() {
            @Override
            public void run() {
                //noinspection InfiniteLoopStatement
                while(alive){
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                        if(params != null){
                            imgRate.setLayoutParams(params);
                        }
                        if (rateTv != null) {   //Animation concernant le text d'affichage de la fc
                            try {
                                rateTv.setText(String.valueOf(rate));
                                imgRate.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.pulse_low));
                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }

                            }
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
       updateUI.start();


        return v;
    }
    @Override
    public void onDestroyView() {
        super.onDestroy();
        alive = false;
        Log.e("OnDestroyView","OnDestroyView");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("OnDestroy","OnDestroy");
    }

    /**
     * Calcul de la valeur initial des imagesView
     */
    private void CalculValeursImagesViews() {
        ViewTreeObserver observer = imgMax.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imgMax.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                imgMaxWidth = imgMax.getMeasuredWidth();
                imgMaxHeight = imgMax.getMeasuredHeight();
                Log.e("Valeurs imgMax : ", " width " + imgMaxWidth + " height " + imgMaxHeight);
            }
        });
        observer = imgMin.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imgMin.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                imgMinWidth = imgMin.getMeasuredWidth();
                imgMinHeight = imgMin.getMeasuredHeight();
                Log.e("Valeurs imgMin : ", " width " + imgMinWidth + " height " + imgMinHeight);
            }
        });
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
     * Mise à jour de l'interface suivant les messages reçu par la montre
     */
    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String message = intent.getStringExtra("message");

            final String[] messageSplit = message.split("µ");
            if (messageSplit[0].equals("DIRECT")) {
                Log.e("Passage Mobile"," : "+message);
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        params = (LinearLayout.LayoutParams) imgRate.getLayoutParams();
                        rate = Float.valueOf(messageSplit[1]);
                        params.width = (int) ((imgMaxWidth * Float.parseFloat(messageSplit[1])) / (maxFreq));
                        params.height = (int) ((imgMaxHeight * Float.parseFloat(messageSplit[1])) / (maxFreq));
                        Log.e("WIDTH",String.valueOf((imgMaxWidth * Float.parseFloat(messageSplit[1])) / (maxFreq)));
                        Log.e("HEIGHT",String.valueOf((imgMaxHeight * Float.parseFloat(messageSplit[1])) / (maxFreq)));
                    }
                });
            }

        }
    }
}
