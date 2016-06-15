package com.mycardiopad.g1.mycardiopad.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Programme;
import com.mycardiopad.g1.mycardiopad.database._Programme;
import com.mycardiopad.g1.mycardiopad.fragment.Fragment_MonProgramme_Ecran1;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * Réalisé par nicolassalleron le 17/02/16. <br/>
 * Ecran qui affiche les différentes activités recommandé pour l'utilisateur suivant les jours de la semaine  <br/>
 */
public class Adapter_MonProgramme_Pager_Inside extends FragmentPagerAdapter {


    private final MyDBHandler_Programme db;
    private final ArrayList<Fragment> mFragments;

    /**
     * L'adapter à l'intérieur du fragment Programme
     * @param fm le gestionnaire de fragment
     * @param ctx le contexte
     */
    public Adapter_MonProgramme_Pager_Inside(FragmentManager fm, Context ctx) {
        super(fm);

        db = new MyDBHandler_Programme(ctx,null,null,1);
        mFragments = new ArrayList<>();

        initFragment();

    }

    /**
     * Réalise la mise en place des différentes fragment à l'intérieur de l'activité du jour
     * Fragment dynamique suivant le nombre d'activités du médecin
     */
    private void initFragment() {
        _Programme programme = db.lastRowProgramme();

        String programmeJours = programme.get_ProgrammeJours(); //Récupération du programme des jours
        String exerciceJours = programme.get_ExerciceJour();    //Récupération des jours des exercices
        String[] joursExo = exerciceJours.split("µ");   //Découpage des jours
        String[] jours = programmeJours.split("/");     //Découpage des programmes

        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        int currentDay = cal.get(Calendar.DAY_OF_WEEK)-2; //Commence à 1; Récupération du jour courant
        if (currentDay < 0)
            currentDay = 6;
        for (int i = 0;i <7;i++){
            if((Integer.parseInt(joursExo[i])==1 && i == currentDay)){ //Dans le cas ou nous sommes
                String[] exerciceDuJour = jours[currentDay].split("µ");
                for (int j = 0;j <exerciceDuJour.length;j++){   //Affichage des fragments
                    Fragment_MonProgramme_Ecran1 frag = new Fragment_MonProgramme_Ecran1();
                    Bundle args = new Bundle();
                    args.putString("exercice",exerciceDuJour[++j]);
                    args.putString("duree",exerciceDuJour[++j]);
                    frag.setArguments(args);
                    mFragments.add(frag);
                }
            }else if(Integer.parseInt(joursExo[i])== 0 && i == currentDay ){ //Dans le cas ou il n'y a pas d'exercice aujourd'hui
                Fragment_MonProgramme_Ecran1 frag = new Fragment_MonProgramme_Ecran1();
                Bundle args = new Bundle();
                args.putString("exercice","Pas d'exercice aujourd'hui");
                args.putString("duree","");
                frag.setArguments(args);
                mFragments.add(frag);
            }
        }
    }

    @Override
    public Fragment getItem(int i) {
        return mFragments.get(i);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Mon programme";
    }
}