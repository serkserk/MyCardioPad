package com.mycardiopad.g1.mycardiopad.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mycardiopad.g1.mycardiopad.fragment.Fragment_MonProgramme_Host;
import com.mycardiopad.g1.mycardiopad.fragment.Fragment_Video;

/**
 * Réalisé par kevin le 01/03/2016. <br/>
 * Permet d'afficher le programme et les vidéos côte à côte. <br/>
 */

public class Adapter_MonProgramme extends FragmentPagerAdapter {

    /**
     * Adapter pour le fragment mon programme
     * @param fm le gestionnaire de fragment
     */
    public Adapter_MonProgramme(FragmentManager fm) {
        super(fm);

    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null;
        switch (i) {
            case 0:
                fragment = new Fragment_MonProgramme_Host();    //Le fragment contenant également des fragments
                break;
            case 1:
                fragment =  new Fragment_Video() ;  //Le fragment pour Youtube
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {     //Nombre total de fragment
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Mon programme"; //Le programme
            case 1:
                return "Aide et vidéo"; //Les vidéos
        }
        return null;
    }
}