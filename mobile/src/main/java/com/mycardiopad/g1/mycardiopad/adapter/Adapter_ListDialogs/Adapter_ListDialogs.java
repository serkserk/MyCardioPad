package com.mycardiopad.g1.mycardiopad.adapter.Adapter_ListDialogs;

import android.graphics.drawable.Drawable;

/**
 * Réalisé par Vishnupriya le 17/02/16.
 */
public class Adapter_ListDialogs {

    private String item;
    private Drawable image;

    /**
     * Constructeur de la list des activité
     * @param item le nom de l'item
     * @param image l'image de l'item
     */
    public Adapter_ListDialogs(String item, Drawable image){
        this.item = item;
        this.image = image;
    }

    // Récupère le String atribué à item
    public String getItem() {
        return item;
    }

    // Définie la valeur de item
    public void setItem(String item) {
        this.item = item;
    }

    // Récupère l'Image atribué à image
    public Drawable getImage() {
        return image;
    }

    @Override
    public String toString() {
        return this.item;
    }

}