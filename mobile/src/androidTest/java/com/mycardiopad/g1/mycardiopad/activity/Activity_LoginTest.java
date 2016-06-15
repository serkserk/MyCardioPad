package com.mycardiopad.g1.mycardiopad.activity;


import android.test.ActivityInstrumentationTestCase2;

import com.mycardiopad.g1.mycardiopad.util.ServeurURL;

import org.junit.Assert;

import okhttp3.Request;


/**
 * Réalisé par Vishnupriya le 08/04/16.
 */
public class Activity_LoginTest extends ActivityInstrumentationTestCase2<Activity_Login> {

    Activity_Login activity_login = new Activity_Login();

    /**
     * Vérification si email valide
     * Vérification du password
     * Vérification communication serveur
     */
    public Activity_LoginTest() {
        super(Activity_Login.class);
    }


    public void testIsEmailValid() {
        Assert.assertEquals(activity_login.isEmailValid("name@email.com"), true);
        Assert.assertEquals(activity_login.isEmailValid("nameemail.com"), false);
    }

    public void testConditionPassword() {
        Assert.assertEquals(activity_login.isPasswordValid("azerty"), false);
        Assert.assertEquals(activity_login.isPasswordValid("Azerty"), false);
        Assert.assertEquals(activity_login.isPasswordValid("AZERTY"), false);
        Assert.assertEquals(activity_login.isPasswordValid("12345678"), false);
        Assert.assertEquals(activity_login.isPasswordValid("azerty1"), false);
        Assert.assertEquals(activity_login.isPasswordValid("Azerty1"), false);
        Assert.assertEquals(activity_login.isPasswordValid("Azerty19"), true);
    }

    public void testCheckServeur(){
        getActivity().okHttpCall(createRequest("ns@pmspos.com","lolilol"));
        Assert.assertEquals(false,getActivity().valeurEnvoi[0]);
    }

    private Request[] createRequest(String email, String password){
        // Requête REST
        return new Request[]{new Request.Builder()
                .url( ServeurURL.LOGIN + email + "&mot_de_passe=" + activity_login.md5(password))
                .get()
                .build()};
    }



}

