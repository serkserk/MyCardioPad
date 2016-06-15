package com.mycardiopad.g1.mycardiopad.fragment;

import android.test.ActivityInstrumentationTestCase2;

import com.mycardiopad.g1.mycardiopad.R;
import com.mycardiopad.g1.mycardiopad.activity.Activity_Main;

/**
 * Réalisé par nicolassalleron le 10/04/16.
 */
public class Fragment_LancerActivitesTest extends ActivityInstrumentationTestCase2<Activity_Main> {

    Activity_Main activity_main;


    /**
     * Vérification de l'existance de l'activité
     * Vérification du changement de classe avec visibilité
     */
    public Fragment_LancerActivitesTest() {
        super(Activity_Main.class);
    }

    public void testActivityExists() {
        activity_main = getActivity();
        assertNotNull(activity_main);
    }

    public void testFragmentActivites() {
        activity_main = getActivity();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity_main.displayView(new Fragment_LancerActivites());
            }
        });
        assertEquals(true,activity_main.isSessionEnregistrement && activity_main.fragment.isVisible()); //Le fragment est bien en place
    }


    public void testFragmentHost() {
        activity_main = getActivity();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity_main.displayView(R.id.nav_session_enregistrement);
            }
        });
        assertEquals(true,activity_main.isSessionEnregistrement); //Le fragment est Fragment_SessionEnregistrement_Host
    }


}