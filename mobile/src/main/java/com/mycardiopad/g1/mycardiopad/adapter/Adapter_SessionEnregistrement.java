package com.mycardiopad.g1.mycardiopad.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mycardiopad.g1.mycardiopad.fragment.Fragment_SessionEnregistrement_Ecran1;
import com.mycardiopad.g1.mycardiopad.fragment.Fragment_SessionEnregistrement_Ecran2;
import com.mycardiopad.g1.mycardiopad.fragment.Fragment_SessionEnregistrement_Ecran3;

import java.util.ArrayList;

/**
 * Réalisé par nicolassalleron le 17/02/16. <br/>
 * Adapter mettant en place les différents écrans de la session enregistrement suivant que l'utilisateur ait une montre ou non  <br/>
 */
public class Adapter_SessionEnregistrement extends FragmentPagerAdapter {


    ArrayList<Fragment> fragmentArrayList ;

    public Adapter_SessionEnregistrement(FragmentManager fm, String fc) {
        super(fm);
        fragmentArrayList = new ArrayList<>();

        if(fc.equals("Avec FC")){   //Dans le cas ou il y a une montre connecté
            fragmentArrayList.add(new Fragment_SessionEnregistrement_Ecran1());
            fragmentArrayList.add(new Fragment_SessionEnregistrement_Ecran2());
            fragmentArrayList.add(new Fragment_SessionEnregistrement_Ecran3());
        }else {         //Dans le cas contraire
            fragmentArrayList.add(new Fragment_SessionEnregistrement_Ecran1());
        }


    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null;
        if(fragmentArrayList.size()>1){ //Dans le cas ou l'écran possède plusieurs écran
            switch (i) {
                case 0:
                    fragment = fragmentArrayList.get(0);
                    break;
                case 1:
                    fragment = fragmentArrayList.get(1);
                    break;
                case 2:
                    fragment = fragmentArrayList.get(2);
                    break;

            }
        }else {
            fragment = fragmentArrayList.get(0);    //Dans le cas contraire
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(fragmentArrayList.size()>1){
            switch (position) {
                case 0:
                    return "Chronomètre";
                case 1:
                    return "Cercle de fréquence";
                case 2:
                    return "Graphique";
            }
        }else {
            return "Enregistrement de la session";
        }
        return null;
    }

    /**
     * Permet de récuperer le fragment selon l'index
     * @param index numéro du fragment
     * @return le fragment correspondant à l'index
     */
    public Fragment getFragment(int index){
        return fragmentArrayList.get(index);
    }

}