package com.mycardiopad.g1.mycardiopad.util;

/**
 * Réalisé par kevin le 14/03/2016.  <br/>
 * Informations concernant les succès  <br/>
 */
public class Succes {

    private String titre;
    private String sousTitre;
    private int image;

    /**
     * Adapteur pour la liste des succes
     * @param titre le titre du succès
     * @param sousTitre la description du succès
     * @param image la ressource de l'image du succès
     */
    public Succes(String titre, String sousTitre,int image){

        this.titre = titre;
        this.sousTitre = sousTitre;
        this.image = image;
    }

    //Set
    public void setImage(int image) {
        this.image = image;
    }

    //Get
    public String getTitre() {
        return titre;
    }
    public String getSousTitre() {
        return sousTitre;
    }
    public int getImage() {
        return image;
    }



}
