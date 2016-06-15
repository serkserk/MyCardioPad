package com.mycardiopad.g1.mycardiopad.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mycardiopad.g1.mycardiopad.R;

/**
 * Réalisé par Vishnupriya le 21/03/16.  <br/>
 * Affichage de la licence des différentes librairies utilisés  <br/>
 */
public class Fragment_WebLicence extends Fragment {

    WebView webViewLicence ;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Affiche la view correspondant au fragment
        View v = inflater.inflate(R.layout.fragment_weblicence, container, false);

        webViewLicence = (WebView) v.findViewById(R.id.weblicence);
        webViewLicence.setWebViewClient(new WebViewClient());
        webViewLicence.setWebChromeClient(new WebChromeClient());
        webViewLicence.getSettings().setJavaScriptEnabled(true);
        webViewLicence.clearCache(true);
        // Chargement de l'url
        webViewLicence.loadUrl("http://journaldesilver.com/about/android.html");
        webViewLicence.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    WebView webView = (WebView) v;

                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            if (webView.canGoBack()) {
                                // Retour en arrière
                                webView.goBack();
                                return true;
                            } else {
                                // Passage au Fragment_Reglages
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.content_frame, new Fragment_Reglages());
                                ft.commit();
                            }
                            webView.clearCache(true);
                            break;
                    }
                }
                return false;
            }
        });
        return v;
    }
}
