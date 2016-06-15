package com.mycardiopad.g1.mycardiopad.activity;

import android.test.ActivityInstrumentationTestCase2;

import com.mycardiopad.g1.mycardiopad.R;

/**
 * Réalisé par nicolassalleron le 09/04/16.
 */
public class Activity_MainTest extends ActivityInstrumentationTestCase2<Activity_Main> {


    /**
     * Vérifivation instanciation de l'activité
     * Vérification changement de fragment
     */
    public Activity_MainTest(){
        super(Activity_Main.class);
    }


    public void testInitialisation() throws Exception {

        Activity_Main ac = getActivity();
        assertNotNull(ac);
    }

    public void testChangementView(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getActivity().displayView(R.id.nav_reglages);
                assertEquals(getActivity().fragment != null, true); //Instanciation correcte
                assertEquals( (getActivity().fragment).toString(),"Reglages");    //Fragment correct
            }
        });
    }

}