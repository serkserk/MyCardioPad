package com.mycardiopad.g1.mycardiopad.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mycardiopad.g1.mycardiopad.fragment.Fragment_HistoriqueMois;
import com.mycardiopad.g1.mycardiopad.fragment.Fragment_HistoriqueSemaine;

/**
 * Réalisé par nicolassalleron le 17/02/16. <br/>
 * Adaptateur pour l'historique, permet d'afficher les semaines et le mois côte à côte  <br/>
 */
public class Adapter_Historique extends FragmentPagerAdapter {


    /**
     * Permet d'initialiser notre adapter de fragment
     * @param fm le fragment manager
     */
    public Adapter_Historique(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null;
        switch (i) {
            case 0:
                fragment = new Fragment_HistoriqueSemaine();    //Dans le cas ou nous voulons la semaine
                break;
            case 1:
                fragment =  new Fragment_HistoriqueMois();  //Mois
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }   //Nombre d'adapter du fragment

    @Override
    public CharSequence getPageTitle(int position) {    //Titre de chaque fragment
        switch (position) {
            case 0:
                return "Historique de la semaine";
            case 1:
                return "Historique de l'année";
        }
        return null;
    }
}