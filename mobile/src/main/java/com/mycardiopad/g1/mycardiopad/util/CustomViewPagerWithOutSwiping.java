package com.mycardiopad.g1.mycardiopad.util;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
/**
 * Réalisé par nicolassalleron le 15/03/16. <br/>
 * Permet de désactiver le swippe par défaut d'un viewPager <br/>
 * Permet ainsi de ne pas sauter d'étapes dans l'écran d'inscription  <br/>
 */
public class CustomViewPagerWithOutSwiping extends ViewPager {


    public CustomViewPagerWithOutSwiping(Context context) {
        super(context);
    }

    public CustomViewPagerWithOutSwiping(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //On désactive le swipping
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //On désactive le swipping
        return false;
    }
}