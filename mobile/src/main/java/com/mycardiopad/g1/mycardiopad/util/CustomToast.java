package com.mycardiopad.g1.mycardiopad.util;

import android.content.Context;

import com.github.johnpersano.supertoasts.SuperToast;

/**
 * Réalisé par kevin le 11/03/2016. <br/>
 * Affichage d'une notification
 */
public class CustomToast {
    /**
     *  Affiche une notification
     * @param context le contexte de l'application
     * @param titre le message qui sera affiché dans la notification
     * @param resID la ressource utilisée pour afficher l'icône présent dans la notification
     */
    public CustomToast(Context context, String titre, int resID) {

        SuperToast superToast = new SuperToast(context);
        superToast.setDuration(SuperToast.Duration.MEDIUM);
        superToast.setText(titre);
        superToast.setBackground(SuperToast.Background.BLUE);
        superToast.setAnimations(SuperToast.Animations.POPUP);
        superToast.setIcon(resID, SuperToast.IconPosition.LEFT);
        superToast.show();
    }

    /**
     * Affiche une notification
     * @param context le contexte de l'application
     * @param titre le message qui sera affiché dans la notification
     */
    public CustomToast(Context context, String titre) {

        SuperToast superToast = new SuperToast(context);
        superToast.setDuration(SuperToast.Duration.MEDIUM);
        superToast.setText(titre);
        superToast.setBackground(SuperToast.Background.BLUE);
        superToast.setAnimations(SuperToast.Animations.POPUP);
        superToast.show();
    }
}
