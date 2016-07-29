package com.mycardiopad.g1.mycardiopad.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Réalisé par serkan <br/>
 * Retourne la liste des fragments utilisés dans l'activité Feedback  <br/>
 */
public class Adapter_Feedback extends FragmentPagerAdapter {

    /**
     * Defining a FragmentPagerAdapter class for controlling the fragments to be shown when user swipes on the screen.
     */
    private final List fragments;

    /**
     * On fournit la list des fragment à afficher
     *
     * @param fragmentManager le fragment manager
     * @param fragments       la liste des fragments
     */
    public Adapter_Feedback(FragmentManager fragmentManager, List fragments) {
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
