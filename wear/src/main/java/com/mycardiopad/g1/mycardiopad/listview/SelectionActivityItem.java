package com.mycardiopad.g1.mycardiopad.listview;

/**
 * Réalisé par nicolassalleron le 14/02/16.  <br/>
 * Permet de mettre en place les différents items correpondant aux activités  <br/>
 */
public class SelectionActivityItem {
    public int imageRes;
    public String text;
    public String minText;

    /**
     * Permet de stockés les items nécessaires à la sélection d'activité
     * @param imageRes  La ressource de l'image
     * @param text      Le texte de l'image
     * @param minText   Les minutes de l'image
     */
    public SelectionActivityItem(int imageRes, String text, String minText) {
        this.imageRes = imageRes;
        this.text = text;
        this.minText = minText;
    }
}
