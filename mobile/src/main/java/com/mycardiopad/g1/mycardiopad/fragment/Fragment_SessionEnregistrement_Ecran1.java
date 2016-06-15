package com.mycardiopad.g1.mycardiopad.fragment;

/**
 * Réalisé par nicolassalleron le 17/01/16. <br/>
 * Mise en place du chronomètre (affichage seulement) <br/>
 */


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mycardiopad.g1.mycardiopad.R;

public class Fragment_SessionEnregistrement_Ecran1 extends Fragment {

    View v;
    public TextView mChronometer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_lancer_session_enregistrement_fragment_stopwatch, container, false);
        mChronometer = (TextView) v.findViewById(R.id.txtStopWatch);
        return v;
    }

    /**
     * Methode pour mettre en place un chronomètre via une méthode parente
     * @param chronometer le chronomètre à mettre en place
     */
    public void setChronometer(String  chronometer) {
        this.mChronometer.setText(chronometer);
    }

}
