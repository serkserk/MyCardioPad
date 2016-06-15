package com.mycardiopad.g1.mycardiopad.fragment;

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
import com.mycardiopad.g1.mycardiopad.adapter.Adapter_MonProgramme;

/**
 * Réalisé par kevin le 01/03/2016. <br/>
 * Ecran prenant en charge l'host de MonProgramme et les vidéos Youtube <br/>
 */
public class Fragment_MonProgrammeEtVideo_Host extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_programme_contener,container,false);

        //Mise en place du Pager Adapter
        Adapter_MonProgramme myPagerAdapter = new Adapter_MonProgramme(this.getChildFragmentManager());
        ViewPager myPager = (ViewPager) v.findViewById(R.id.viewpager_programme);
        myPager.setAdapter(myPagerAdapter);

        //Mise en place du titre
        PagerTitleStrip pts = (PagerTitleStrip) v.findViewById(R.id.pager_title_strip);
        pts.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        pts.setTextSpacing(200);
        return v;
    }

}
