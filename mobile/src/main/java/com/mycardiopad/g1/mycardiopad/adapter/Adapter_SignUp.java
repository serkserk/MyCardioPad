package com.mycardiopad.g1.mycardiopad.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Réalisé par nicolassalleron le 17/02/16. <br/>
 * Retourne la liste des fragments utilisés dans l'activité SignUp  <br/>
 */
public class Adapter_SignUp extends FragmentPagerAdapter {

    private final List fragments;

    /**
     * On fournit la list des fragment à afficher
     * @param fragmentManager le fragment manager
     * @param fragments la liste des fragments
     */
    public Adapter_SignUp(FragmentManager fragmentManager, List fragments) {
        super(fragmentManager);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) { //La position des fragments dans la liste.
        return (Fragment) this.fragments.get(position);
    }

    @Override
    public int getCount() {     //La taille total des fragments
        return this.fragments.size();
    }
}
