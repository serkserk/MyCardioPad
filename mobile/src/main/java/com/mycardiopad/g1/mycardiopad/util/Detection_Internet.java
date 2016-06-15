package com.mycardiopad.g1.mycardiopad.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Réalisé par kevin le 27/03/2016. <br/>
 * Permet de vérifier la présence d'internet  <br/>
 */
public class Detection_Internet {

    private Context context;

    public Detection_Internet(Context context){
        this.context = context;
    }

    /**
     * Vérification si Internet est disponible
     * @return un boolean indiquant si le smartphone dispose d'une connexion internet
     */
    public boolean isConnect(){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (NetworkInfo anInfo : info)
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED)
                        return true;
        }
        return false;
    }
}
