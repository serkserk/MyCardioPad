package com.mycardiopad.g1.mycardiopad.fragment;

/**
 * Réalisé par nicolassalleron le 17/01/16. <br/>
 * Affichage du graphique correspondant aux différentes fréquences relevées via la montre  <br/>
 */


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;
import com.mycardiopad.g1.mycardiopad.R;

import java.util.ArrayList;

public class Fragment_SessionEnregistrement_Ecran3 extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    int ligne = 0;
    GoogleApiClient api;
    ArrayList<Entry> entries;
    ArrayList<String> labels;
    LineChart lineChart;
    Handler uiHandler;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Construction d'une nouvelle GoogleApiClient pour l'API de la montre
        api = new GoogleApiClient.Builder(getActivity())
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        // Enregistrement du fragment pour qu'il revoit les données de la montre
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(messageReceiver, messageFilter);

    }
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lancer_sesson_enregistrement_fragment_graphique,container,false);
        uiHandler = new Handler();


        entries = new ArrayList<>();
        labels = new ArrayList<>();


        //Mise en place de notre graph
        lineChart = (LineChart) v.findViewById(R.id.chart);
        lineChart.setGridBackgroundColor(getResources().getColor(R.color.colorWhite));
        lineChart.setDrawGridBackground(false);
        return v;
    }


    @Override
    public void onClick(View v) {


        }

    @Override
    public void onStart() {
        super.onStart();
        if (api != null)
            api.connect();

    }

    /**
     * Récupération des données du fragment détruit
     * @param savedInstanceState objet ou sont sauvegardé nos données
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        if (savedInstanceState != null) {
            ligne = savedInstanceState.getInt("ligne");
        }
    }

    /**
     * Sauvegarde de l'état des différents champs du fragment
     * @param outState object d'enregistrement de l'état de sortie
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
            outState.putInt("ling",ligne);
    }


    /**
     * Permet ici de récupérer le titre du fragment
     * @return La string du fragment
     */
    @Override
    public String toString(){
        return "Session Enregistrement Fragment";
    }


    @Override
    public void onStop() {
        if (api != null && api.isConnected()) {
            api.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected( Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * Récupération des données de la montre et affichage dans le graphique
     */
    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {


            String message = intent.getStringExtra("message");
            final String [] messageSplit = message.split("µ");


            if (messageSplit[0].equals("DIRECT")) {
                //Maj de l'affichage
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        entries.add(new Entry(Float.valueOf(messageSplit[1]), entries.size()));
                        labels.add(String.valueOf(entries.size()));
                        LineDataSet dataset = new LineDataSet(entries, "Capture Montre");
                        dataset.setDrawCubic(true);
                        LineData data = new LineData(labels, dataset);
                        lineChart.setData(data);
                        lineChart.setDescription("Description");
                        lineChart.animateX(0);
                    }
                });
            }
        }
    }

}