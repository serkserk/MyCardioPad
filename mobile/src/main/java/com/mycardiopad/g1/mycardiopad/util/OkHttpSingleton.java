package com.mycardiopad.g1.mycardiopad.util;

import okhttp3.OkHttpClient;

/**
 * Réalisé par kevin le 21/03/2016. <br/>
 * Récupération d'une instance d'OkHttp <br/>
 */
public class OkHttpSingleton {

    public static OkHttpClient instance;

    private OkHttpSingleton() {

    }

    /**
     * @return l'instance de OkHttpClient en cours d'execution ou en créée une si celle-ci n'existe pas encore
     */
    public static OkHttpClient getInstance() {
        if (instance == null) {
            instance = new OkHttpClient();
        }
        return instance;
    }
}
