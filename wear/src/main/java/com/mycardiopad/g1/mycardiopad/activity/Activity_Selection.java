package com.mycardiopad.g1.mycardiopad.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mycardiopad.g1.mycardiopad.R;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Compte_Wear;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Programme_Wear;
import com.mycardiopad.g1.mycardiopad.database._Programme_Wear;
import com.mycardiopad.g1.mycardiopad.listview.SelectionActivityAdapter;
import com.mycardiopad.g1.mycardiopad.listview.SelectionActivityItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Réalisé par nicolassalleron le 14/02/16.  <br/>
 * Permet la sélection d'activité    <br/>
 */
public class Activity_Selection extends Activity implements WearableListView.ClickListener{

    private List<SelectionActivityItem> viewItemList = new ArrayList<>();
    WearableListView wearableListView;
    private MyDBHandler_Programme_Wear dbProgramme;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);


        //Adaptation en fonction de l'écran de la montre
        WatchViewStub stub = (WatchViewStub) findViewById(R.id.selection_watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                wearableListView = (WearableListView) findViewById(R.id.wearable_list_view);
                TextView txt = (TextView) findViewById(R.id.id_Title);

                txt.setText("Bienvenue "+ new MyDBHandler_Compte_Wear(getApplicationContext(), null).getCompte(0).get_prenom());

                String[] names = new String[]{"Marche", "Vélo",
                        "Course", "Natation", "Ski de fond", "Danse", "Tapis roulant", "Ergomètre (vélo classique)", "Autres appareils"};

                final int[] images = {R.drawable.ic_marche, R.drawable.ic_velo,
                        R.drawable.ic_jogging, R.drawable.ic_natation, R.drawable.ic_ski, R.drawable.ic_danse, R.drawable.ic_tapis, R.drawable.ic_ergometre, R.drawable.ic_exercise};


                ArrayList<String> arrayProgramme = new ArrayList<>();
                dbProgramme = new MyDBHandler_Programme_Wear(Activity_Selection.this, null);
                _Programme_Wear programme = dbProgramme.lastRowProgramme();

                String programmeJours = programme.get_ProgrammeJours(); //Récupération du programme des jours
                String exerciceJours = programme.get_ExerciceJour();    //Récupération des jours des exercices
                String[] joursExo = exerciceJours.split("µ");   //Découpage des jours
                String[] jours = programmeJours.split("/");     //Découpage des programmes

                Calendar cal = Calendar.getInstance();
                cal.setFirstDayOfWeek(Calendar.MONDAY);
                int currentDay = cal.get(Calendar.DAY_OF_WEEK)-2; //Commence à 1; Récupération du jour courant
                if (currentDay < 0)
                    currentDay = 6;
                String[] exerciceDuJour = new String[0];
                for (int i = 0;i <7;i++){
                    if((Integer.parseInt(joursExo[i])==1 && i == currentDay)) { //Dans le cas ou nous sommes
                        exerciceDuJour = jours[currentDay].split("µ");
                    }
                }


                for (int j = 0; j < exerciceDuJour.length; j++) {   //Ajout dans le tableau
                    arrayProgramme.add(exerciceDuJour[++j] + "µ" + exerciceDuJour[++j]);
                }
                boolean add =false;
                for (int x = 0; x < names.length; x++) {
                    for (int i = 0 ; i< arrayProgramme.size();i++){
                        if(names[x].equals(arrayProgramme.get(i).split("µ")[0])){   //Dans le cas ou il y a des minutes
                            viewItemList.add(new SelectionActivityItem(images[x],names[x],arrayProgramme.get(x).split("µ")[1]+"min"));
                            add = true;
                        }
                    }
                    if(add)
                        add = false;
                    else    //dans le cas contraire
                        viewItemList.add(new SelectionActivityItem(images[x],names[x],""));

                }
                //Mise en place de l'adapter
                wearableListView.setAdapter(new SelectionActivityAdapter(getApplicationContext(), viewItemList));
                wearableListView.setClickListener(Activity_Selection.this);
            }
        });
    }

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        Toast.makeText(this, "Click sur " + viewItemList.get(viewHolder.getLayoutPosition()).text, Toast.LENGTH_SHORT).show();
        //Mettre en place un impact de la sélection d'activité
        Intent i = new Intent(Activity_Selection.this, Activity_Wear_Main.class);
        startActivity(i);
    }

    @Override
    public void onTopEmptyRegionClick() {

    }
}
