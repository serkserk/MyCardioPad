package com.mycardiopad.g1.mycardiopad.fragment;

/**
 * Réalisé par nicolassalleron le 17/01/16. <br/>
 * Permet de mettre en place le viewPager et les titres de l'écran Historique<br/>
 */


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mycardiopad.g1.mycardiopad.R;
import com.mycardiopad.g1.mycardiopad.adapter.Adapter_Historique;

public class Fragment_Historique_Host extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_historique_contener,container,false);

        //Mise en place de l'adaptateur
        Adapter_Historique myPagerAdapter = new Adapter_Historique(this.getChildFragmentManager());
        ViewPager myPager = (ViewPager) v.findViewById(R.id.viewpager_historique);
        myPager.setAdapter(myPagerAdapter);
        //Mise en place des titres
        PagerTitleStrip pts = (PagerTitleStrip) v.findViewById(R.id.pager_title_strip);
        pts.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        pts.setTextSpacing(200);
        return v;
    }


}