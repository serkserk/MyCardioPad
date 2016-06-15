package com.mycardiopad.g1.mycardiopad.fragment;

/**
 * Réalisé par nicolassalleron le 17/01/16. <br/>
 * Permet de mettre en place l'écran des semaines <br/>
 */


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
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
import com.liulishuo.magicprogresswidget.MagicProgressCircle;
import com.mycardiopad.g1.mycardiopad.R;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Capture;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Programme;
import com.mycardiopad.g1.mycardiopad.database._Capture;
import com.mycardiopad.g1.mycardiopad.util.AnimTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Fragment_HistoriqueSemaine extends Fragment {

    CombinedChart cc;
    CombinedData cdSemaine;
    BarData bdSemaine;
    LineData ldSemaine;
    MagicProgressCircle mpc;
    AnimTextView atv;
    AnimatorSet set;
    Animation gone,visible;
    RelativeLayout rl;
    MyDBHandler_Capture myDBHandler_capture;
    _Capture cap;
    private ProgressDialog barProgressDialog;
    private MyDBHandler_Programme myDBHandler_Programme;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_historique_semaine, container, false);
        cc = (CombinedChart) v.findViewById(R.id.chart);
        cc.setGridBackgroundColor(getResources().getColor(R.color.colorWhite));
        //On accède à la base de donnée locale
        myDBHandler_capture = new MyDBHandler_Capture(getContext(), null, null, 1);
        myDBHandler_Programme = new MyDBHandler_Programme(getContext(),null,null,1);
        //Création des données pour la semaine
        cdSemaine = new CombinedData(getXAxisValues());

        //Barre de progression
        barProgressDialog = new ProgressDialog(getActivity());
        barProgressDialog.setTitle("Lecture de la base ...");
        barProgressDialog.setMessage("Récupération des informations ...");
        barProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        barProgressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                bdSemaine = new BarData(getXAxisValues(), getBarDataSet());
                ldSemaine = new LineData(getXAxisValues(), getLineDataSet());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cdSemaine.setData(bdSemaine);
                        cdSemaine.setData(ldSemaine);
                        //On associe la combinaison de données au graphique
                        cc.setDescription("");
                        cc.setData(cdSemaine);
                        cc.animateXY(1000, 1300);
                        barProgressDialog.hide();
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

        //Création des éléments cercle indiquant la qualité des sessions
        mpc = (MagicProgressCircle) v.findViewById(R.id.mpc);
        atv = (AnimTextView) v.findViewById(R.id.atv);
        set = new AnimatorSet();

        //Affectation des animations pour l'apparition et la disparition de la vue contenant le cercle
        gone = AnimationUtils.loadAnimation(getContext(),R.anim.gone_animation);
        visible = AnimationUtils.loadAnimation(getContext(),R.anim.visible_animation);

        //On définit un RelativeLayout contenant le cercle afin de le faire apparaître ou disparaître en fonction de la SeekBar
        rl = (RelativeLayout) v.findViewById(R.id.rlmpc);
        rl.setVisibility(View.GONE);

        //Permet de faire apparaître la vue contenant le cercle de qualité en cas de clic sur le graphique
        cc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Click sur x:" + cc.getX() + ", y : " + cc.getX(), Toast.LENGTH_SHORT).show();
                rl.setVisibility(View.VISIBLE);
                rl.startAnimation(visible);

                int freqMax = myDBHandler_Programme.lastRowProgramme().get_maxFreq();
                int freqMin = myDBHandler_Programme.lastRowProgramme().get_minFreq();



                /***
                 * Somme du temps au dessus du max
                 * +
                 * Somme du temps en dessous de min
                 *
                 * DIVISER
                 *
                 * TEMPS TOTAL POUR LE MOMENT
                 */
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());


                boolean premierFoisMax = true;
                boolean premierFoisMin = true;

                long tempsTotalMax = 0;
                long tempsTotalMin = 0;
                long tempsDépart;
                long tempsfinal;

                long timeMinFirst = 0;
                long timeMaxFirst = 0;



                long timeMax;
                long timeMin;
                long tempsExercice = 0;



                //Pour les jours de la semaine
                long nbLine = myDBHandler_capture.numberLine();
                for (int i = 0; i < 7; i++) {
                    //Log.i("dateTag", sdf.format(cal.getTime()));
                    for (int j = 0;j<nbLine;j++){
                        cap = myDBHandler_capture.getMesure(j);
                        if(cal.get(Calendar.YEAR) == cap.getYear() &&
                                cal.get(Calendar.MONTH) == cap.getMonth() &&
                                cal.get(Calendar.DAY_OF_MONTH) == cap.getDay()){


                            //Dans ce cas on est sur la bonne journée

                            String[] string = cap.get_cap().split("µ");
                            tempsDépart = new Date(Long.parseLong(string[2])).getMinutes()*60+new Date(Long.parseLong(string[2])).getSeconds();
                            tempsfinal = new Date(Long.parseLong(string[string.length-1])).getMinutes()*60+new Date(Long.parseLong(string[string.length-1])).getSeconds();
                            tempsExercice += tempsfinal - tempsDépart;
                            //Récupération temps au dessus de la valeur min
                            for(int x = 1;x<string.length;x++){
                                if(Float.parseFloat(string[x])<(freqMin)){
                                    x+=1;
                                    if(premierFoisMin){ //Récupération de la première date < freqMin
                                        timeMinFirst = Long.parseLong(string[x]);
                                        premierFoisMin = false;
                                    }
                                }else {
                                    x+=1;
                                    if(!premierFoisMin){    //Fin de l'enregistrement de la date min
                                        long finDateMin = Long.parseLong(string[x]);
                                        timeMin = getDateDiff(new Date(finDateMin),new Date(timeMinFirst),TimeUnit.MILLISECONDS);
                                        tempsTotalMin += (new Date(timeMin).getSeconds());
                                        premierFoisMin = true;
                                    }
                                }
                                x+=1;


                            }
                            //Dans le cas ou l'enregistrement est sur true
                            if(!premierFoisMin){    //Fin de l'enregistrement de la date min
                                long finDateMin = Long.parseLong(string[string.length-1]);
                                timeMin = getDateDiff(new Date(finDateMin),new Date(timeMinFirst),TimeUnit.MILLISECONDS);
                                tempsTotalMin += (new Date(timeMin).getSeconds());
                                premierFoisMin = true;
                            }

                            //Récupération temps au dessus de la valeur max
                            for(int x = 1;x<string.length;x++){
                                if(Float.parseFloat(string[x])>freqMax){
                                    x+=1;
                                    if(premierFoisMax){ //Récupération de la première date < freqMin
                                        timeMaxFirst = Long.parseLong(string[x]);
                                        premierFoisMax = false;
                                    }
                                }else {
                                    x+=1;
                                    if(!premierFoisMax){    //Fin de l'enregistrement de la date min
                                        long finDateMax = Long.parseLong(string[x]);
                                        timeMax = getDateDiff(new Date(finDateMax),new Date(timeMaxFirst),TimeUnit.MILLISECONDS);
                                        tempsTotalMax += (new Date(timeMax).getSeconds());
                                        premierFoisMax = true;
                                    }
                                }
                                x+=1;


                            }
                            //Dans le cas ou l'enregistrement est sur true
                            if(!premierFoisMax){    //Fin de l'enregistrement de la date min
                                long finDateMax = Long.parseLong(string[string.length-1]);
                                timeMax = getDateDiff(new Date(finDateMax),new Date(timeMinFirst),TimeUnit.MILLISECONDS);
                                tempsTotalMax += (new Date(timeMax).getSeconds());
                                premierFoisMax = true;
                            }
                        }
                    }
                    cal.add(Calendar.DAY_OF_WEEK, 1); //Ajout d'un jour.
                }
                Log.e("TempTot", tempsExercice+"");
                Log.e("TempsMax",tempsTotalMax+"");
                Log.e("TempsMin", tempsTotalMin+"");

                float iq = (1 - (((float) tempsTotalMin+(float)tempsTotalMax)/(float)tempsExercice))*100;
                defineMagicCircle(mpc,atv,iq/101f,(int)iq); //101 pour que dans le cas ou nous avons un IQ = 100, le cercle s'affiche quand même à une valeur proche de 100

            }
        });
        //Permet de faire disparaître la vue contenant le cercle de qualité en cas de clic sur l'ensemble de l'écran hormis le graphique
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl.startAnimation(gone);
                rl.setVisibility(View.GONE);
            }
        });

        return  v;
    }

    /**
     * Permet de définir les propriétés du cercle ainsi que de la valeur numérique du cercle
     * @param mpc l'élément MagicProgressCircle que l'on doit définir
     * @param tv  l'élément AnimTextView (texte qui change dynamiquement) que l'on doit définir
     */
    private void defineMagicCircle(MagicProgressCircle mpc, AnimTextView tv, float cercle, int pourcentage) {
        tv.setMax(100);
        mpc.setStartColor(14430770);
        mpc.setEndColor(14430770);
        set.play(ObjectAnimator.ofFloat(mpc, "percent", 0, cercle));
        set.play(ObjectAnimator.ofInt(atv, "progress", 0, pourcentage));
        set.setDuration(1200);
        set.setInterpolator(new AccelerateInterpolator());
        set.start();
    }

    /**
     * Permet de définir une liste de coordonnées pour le tracage de la courbe
     * @return Un ArrayList contenant les coordonnées de la courbe
     */
    private ArrayList<ILineDataSet> getLineDataSet() {
        ArrayList<ILineDataSet> dataSets;


        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());

        Entry[] tabEntry = new Entry[7];
        ArrayList<Entry> valueLine = new ArrayList<>();

        //Pour les jours de la semaine
        long nbLine = myDBHandler_capture.numberLine();
        for (int i = 0; i < 7; i++) {
            //Log.i("dateTag", sdf.format(cal.getTime()));
            for (int j = 0;j<nbLine;j++){
                cap = myDBHandler_capture.getMesure(j);
                if(cal.get(Calendar.YEAR) == cap.getYear() &&
                        cal.get(Calendar.MONTH) == cap.getMonth() &&
                        cal.get(Calendar.DAY_OF_MONTH) == cap.getDay()){


                    //Dans ce cas on est sur la bonne date

                    String[] string = cap.get_cap().split("µ");
                    float FreqCardMoy = 0;
                    int z;
                    for(z = 0;z<string.length;z++){
                        //Log.e(string[z],"<- VALEUR");
                        FreqCardMoy += Float.valueOf(string[++z]);
                        z++;
                    }
                    FreqCardMoy = FreqCardMoy / ((float)z/3);

                    if (tabEntry[i] != null){
                        tabEntry [i] = new Entry(FreqCardMoy,i);
                    }else{
                        tabEntry[i] = new Entry(FreqCardMoy,i);
                    }







                }
            }
            if (tabEntry[i] != null) valueLine.add(tabEntry[i]);
            cal.add(Calendar.DAY_OF_WEEK, 1); //Ajout d'un jour.
        }
        //Log.e("Taille de ValueBar", String.valueOf(valueBar.size()));
        if (valueLine.size() == 0){
            for (int i = 0;i<7;i++){
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
        ArrayList<IBarDataSet> dataSets = null;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd.MM.yyyy");

        BarEntry[] tabBar = new BarEntry[7];
        ArrayList<BarEntry> valueBar = new ArrayList<>();

        //Pour les jours de la semaine
        long nbLine = myDBHandler_capture.numberLine();
        for (int i = 0; i < 7; i++) {
            //Log.i("dateTag", sdf.format(cal.getTime()));
            for (int j = 0;j<nbLine;j++){
                cap = myDBHandler_capture.getMesure(j);
                if(cal.get(Calendar.YEAR) == cap.getYear() &&
                        cal.get(Calendar.MONTH) == cap.getMonth() &&
                        cal.get(Calendar.DAY_OF_MONTH) == cap.getDay()){
                    //Dans ce cas on est sur la bonne date
                    if (tabBar[i] != null){
                        tabBar [i] = new BarEntry(tabBar[i].getVal() + (float)
                                new Date(cap.getEndRecord() - cap.getStartRecord()).getMinutes(),i);
                    }else{
                        tabBar[i] = new BarEntry((float)
                                new Date(cap.getEndRecord() - cap.getStartRecord()).getMinutes(),i);
                    }

                    //Log.e("Nombre de minutes", String.valueOf(new Date(cap.get_endRecord() - cap.get_startRecord()).getMinutes() + " Valeur de ligne dans db : " + String.valueOf(myDBHandler_capture.getMesure(j).get_id() )));

                }
            }
            if (tabBar[i] != null) valueBar.add(tabBar[i]);
            cal.add(Calendar.DAY_OF_WEEK, 1); //Ajout d'un jour.
        }
        //Log.e("Taille de ValueBar", String.valueOf(valueBar.size()));
        if (valueBar.size() == 0){
            for (int i = 0;i<7;i++){
                valueBar.add(new BarEntry(0,i));
            }

        }

        BarDataSet bds = new BarDataSet(valueBar, "Durée de l'exercice");
        bds.setColor(Color.rgb(75,160,255));
        bds.setAxisDependency(YAxis.AxisDependency.RIGHT);
        bds.setValueTextSize(14.0f);

        dataSets = new ArrayList<>();
        dataSets.add(bds);
        return dataSets;
    }

    /**
     * Définit les repères de l'axe des abscisses
     * @return Un ArrayList contenant les jours de la semaine servant de repères
     */
    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("LU");
        xAxis.add("MA");
        xAxis.add("ME");
        xAxis.add("JE");
        xAxis.add("VE");
        xAxis.add("SA");
        xAxis.add("DI");
        return xAxis;
    }

    /**
     * Retourne la différence entre deux dates
     * @param date1 la première date
     * @param date2 la deuxième date
     * @param timeUnit l'utilité de temps
     * @return la différence en long entre deux Dates
     */
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
}