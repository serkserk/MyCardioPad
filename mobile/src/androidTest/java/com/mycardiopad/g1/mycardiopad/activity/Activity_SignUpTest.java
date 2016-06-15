package com.mycardiopad.g1.mycardiopad.activity;

import android.test.ActivityInstrumentationTestCase2;

/**
 * Réalisé par nicolassalleron le 09/04/16.
 */
public class Activity_SignUpTest extends ActivityInstrumentationTestCase2<Activity_SignUp> {

    /**
     * Test du nombre d'écran
     * Test de changement d'écran une fois que les informations sont remplies
     */
    public Activity_SignUpTest() {
            super(Activity_SignUp.class);
    }


    public void testNombreEcran(){
        assertEquals(3,getActivity().fragments.size());
    }


    public void testPassageEcran(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getActivity().btnSuivant.performClick();    //Passage élément suivant
            }
        });
        assertEquals(0,getActivity().pager.getCurrentItem());

    }






}