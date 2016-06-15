package com.mycardiopad.g1.mycardiopad.activity;

import android.Manifest;
import android.test.ActivityInstrumentationTestCase2;

/**
 * Réalisé par nicolassalleron le 09/04/16.
 */
public class Activity_SplashTest extends ActivityInstrumentationTestCase2<Activity_Splash> {

    private String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_CONTACTS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA};


    /**
     * Vérification des permissions
     */
    public Activity_SplashTest() {
        super(Activity_Splash.class);
    }

    public void testHasPermissions () throws Exception {
        Activity_Splash ac = getActivity();
        assertEquals(true,ac.hasPermissions(getActivity().getBaseContext(),PERMISSIONS));
        assertEquals(false,ac.hasPermissions(getActivity().getBaseContext(), Manifest.permission.STATUS_BAR));
    }


}