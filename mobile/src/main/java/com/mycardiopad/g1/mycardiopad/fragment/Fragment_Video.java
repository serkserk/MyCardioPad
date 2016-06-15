package com.mycardiopad.g1.mycardiopad.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mycardiopad.g1.mycardiopad.R;

/**
 * Réalisé par kevin le 01/03/2016. <br/>
 * Mise en place de l'interface vidéo  <br/>
 */
public class Fragment_Video extends android.support.v4.app.Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_video,container,false);
        return v;
    }


}
