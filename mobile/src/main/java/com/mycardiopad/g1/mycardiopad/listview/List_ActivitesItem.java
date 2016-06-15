package com.mycardiopad.g1.mycardiopad.listview;

/**
 * Réalisé par nicolassalleron le 14/02/16.
 * Permet de sauvegarder les différentes activités pour leur affichage
 */
public class List_ActivitesItem {

    private String name;
    private int imageId;

    /**
     * Permet de sauvegarde le nom et l'id dand un objet
     * @param name le nom de l'image
     * @param imageId l'id d'une image
     */
    public List_ActivitesItem(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;
    }

    //Get
    public String getName() {
        return name;
    }
    public int getImageId() {
        return imageId;
    }

    //Set
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
