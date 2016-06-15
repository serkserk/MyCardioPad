package com.mycardiopad.g1.mycardiopad.fragment;

/**
 * Réalisé par nicolassalleron le 17/01/16. <br/>
 * Fragment correspondant à l'insterface du mois <br/>
 */

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.mycardiopad.g1.mycardiopad.R;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Capture;
import com.mycardiopad.g1.mycardiopad.database._Capture;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Fragment_HistoriqueMois extends Fragment {

    CombinedChart cc;
    CombinedData cdMois;
    BarData bdMois;
    LineData ldMois;
    private MyDBHandler_Capture myDBHandler_capture;
    private _Capture cap;
    private ProgressDialog barProgressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_historique_mois, container, false);
        cc = (CombinedChart) v.findViewById(R.id.chart);
        cc.setGridBackgroundColor(getResources().getColor(R.color.colorWhite));
        //On accède à la base de donnée locale
        myDBHandler_capture = new MyDBHandler_Capture(getContext(), null, null, 1);

        //Création des données pour la semaine


        cdMois = new CombinedData(getXAxisValues());


        //Barre de progression
        barProgressDialog = new ProgressDialog(getActivity());
        barProgressDialog.setTitle("Lecture de la base ...");
        barProgressDialog.setMessage("Récupération des informations ...");
        barProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        barProgressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                bdMois = new BarData(getXAxisValues(), getBarDataSet());
                ldMois = new LineData(getXAxisValues(), getLineDataSet());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cdMois.setData(bdMois);
                        cdMois.setData(ldMois);
                        //On associe la combinaison de données au graphique
                        cc.setDescription("");
                        cc.setData(cdMois);
                        cc.animateXY(1000, 1300);
                        barProgressDialog.dismiss();
                    }
                });
            }
        }).start();

        //On définit les propriétés des axes Y
        YAxis rightAxis = cc.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawLabels(false);
        YAxis leftAxis = cc.getAxisLeft();
        leftAxis.setDrawGridLines(false);

        //On accède à la base de donnée locale
        myDBHandler_capture = new MyDBHandler_Capture(getContext(), null, null, 1);

        cc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Click sur x:" + cc.getX() + ", y : " + cc.getX(), Toast.LENGTH_SHORT).show();
            }
        });
        return  v;
    }

    /**
     * Permet de définir une liste de coordonnées pour le tracage de la courbe
     * @return Un ArrayList contenant les coordonnées de la courbe
     */
    private ArrayList<ILineDataSet> getLineDataSet() {


        ArrayList<ILineDataSet> dataSets;
        ArrayList<Entry> valueLine = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        Entry[] tabEntry = new Entry[12];

        //Pour les jours de la semaine
        long nbLine = myDBHandler_capture.numberLine();
        for (int i = 0; i < 12; i++) {

            for (int j = 0;j<nbLine;j++){ // Pour l'ensemble des lignes
                cap = myDBHandler_capture.getMesure(j);
                if(cal.get(Calendar.YEAR) == cap.getYear() &&
                        cal.get(Calendar.MONTH) == cap.getMonth()){

                    //Dans ce cas on est sur la bonne date
                    String[] string = cap.get_cap().split("µ");
                    float FreqCardMoy = 0;
                    int z;

                    //récupération de la fréquence cardiaque
                    for(z = 0;z<string.length;z++){
                        FreqCardMoy += Float.valueOf(string[++z]);
                        z++;
                    }

                    //Fréquence cardique moyenne
                    FreqCardMoy = FreqCardMoy / (z/3);

                    if (tabEntry[i] != null){   //Mise en place dans la table
                        tabEntry [i] = new Entry(FreqCardMoy,i+2);
                    }else{
                        tabEntry[i] = new Entry(FreqCardMoy,i+2);
                    }
                }

            }
            if (tabEntry[i] != null)
                valueLine.add(tabEntry[i]);
            cal.add(Calendar.MONTH, 1); //Ajout d'un jour.
        }

        //Les lignes des mois
        if (valueLine.size() == 0){
            for (int i = 0;i<12;i++){
                valueLine.add(new BarEntry(0,i));
            }
        }

        LineDataSet ldt = new LineDataSet(valueLine, "Fréquence cardiaque moyenne");
        ldt.setColor(Color.rgb(255, 80, 80));
        ldt.setDrawValues(false);
        ldt.setAxisDependency(YAxis.AxisDependency.LEFT);
        ldt.setDrawCubic(true);
        ldt.setLineWidth(3.50f);
        ldt.setCircleRadius(3.5f);
        ldt.setCircleColor(Color.rgb(255, 10, 10));

        dataSets = new ArrayList<>();
        dataSets.add(ldt);
        return dataSets;
    }

    /**
     * Permet de définir une liste de valeurs pour l'affichage des barres
     * @return Un ArrayList contenant les informations des barres du graphiques
     */
    private ArrayList<IBarDataSet> getBarDataSet() {


        ArrayList<IBarDataSet> dataSets;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH,cal.getMinimum(Calendar.MONTH));


        BarEntry[] tabBar = new BarEntry[12];
        ArrayList<BarEntry> valueBar = new ArrayList<>();
        long nbLigne = myDBHandler_capture.numberLine();
        //Pour le mois.
        for (int i = 0; i < 12; i++) {

            for (int j = 0;j<nbLigne;j++){
                cap = myDBHandler_capture.getMesure(j);
                if(cal.get(Calendar.YEAR) == cap.getYear() &&
                        cal.get(Calendar.MONTH) == cap.getMonth()) {
                    //Dans ce cas on est sur la bonne date, on récupère le temps
                    if (tabBar[i] != null) {
                        tabBar[i] = new BarEntry(tabBar[i].getVal() + (float)
                                new Date(cap.getEndRecord() - cap.getStartRecord()).getMinutes(), i);
                    } else {
                        tabBar[i] = new BarEntry((float)
                                new Date(cap.getEndRecord() - cap.getStartRecord()).getMinutes(), i);
                    }
                }
            }
            if (tabBar[i] != null) valueBar.add(tabBar[i]);
            cal.add(Calendar.MONTH, 1); //Ajout d'un mois.
        }


        //Dans le cas ou il n'y a pas de temps on ajout quand même les barres
        if (valueBar.size() == 0){
            for (int i = 0;i<12;i++){
                valueBar.add(new BarEntry(0,i));
            }

        }

        BarDataSet bds = new BarDataSet(valueBar, "Durée de l'exercice dans le mois");
        bds.setColor(Color.rgb(75,160,255));
        bds.setAxisDependency(YAxis.AxisDependency.RIGHT);
        bds.setValueTextSize(14.0f);

        dataSets = new ArrayList<>();
        dataSets.add(bds);
        return dataSets;
    }

    /**
     * Définit les repères de l'axe des abscisses
     * @return Un ArrayList contenant les mois de l'année servant de repères
     */
    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("JANV");
        xAxis.add("FEVR");
        xAxis.add("MARS");
        xAxis.add("AVRL");
        xAxis.add("MAI");
        xAxis.add("JUIN");
        xAxis.add("JUIL");
        xAxis.add("AOUT");
        xAxis.add("SEPT");
        xAxis.add("OCTO");
        xAxis.add("NOVE");
        xAxis.add("DECE");
        return xAxis;
    }

}