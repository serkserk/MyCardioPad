package com.mycardiopad.g1.mycardiopad.util;

/**
 * Created by Azap Serkan, ie04114
 * Cette classe contient tous les liens pour les différentes requêtes réalisées par l'application
 */
public class ServeurURL {
    public static final String MAIN_URL = "http://mycardiopad.16mb.com/";   //Lien a changer si changement de host en gardant les fichiers php

    public static final String SIGNUP = MAIN_URL + "api/signup/";
    public static final String LOGIN = MAIN_URL + "api/login/?email=";
    public static final String GET_USER_ORDONANCE = MAIN_URL + "api/get_current_ordonnance/?email=";
    public static final String GET_USER_SUCCES = MAIN_URL + "api/get_user_success/?email=";
    public static final String UPLOADED_IMAGE = MAIN_URL + "uploaded_images/";
    public static final String FORGOT_PASSWORD = MAIN_URL + "api/forgot_password/index.php/?email=";
    public static final String GET_CURRENT_ORDONANCE = MAIN_URL + "api/get_current_ordonnance/?email=";
    public static final String UPDATE_SUCCES = MAIN_URL + "api/update_success/";
    public static final String ABOUT = MAIN_URL + "about/android.html";
    public static final String GET_CAPTURE_COUNT = MAIN_URL + "api/get_capture_count/?email=";
    public static final String ADD_CAPTURE = MAIN_URL + "api/add_captures/";
    public static final String ADD_FEEDBACK = MAIN_URL + "api/add_feedbacks/";
}
