package com.mycardiopad.g1.mycardiopad.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Réalisé par kevin le 01/03/2016.  <br/>
 * Onglet des vidéos Youtube <br/>
 */
public class YouTubeContent {

    public static List<YouTubeVideo> ITEMS = new ArrayList<>();

    public static Map<String, YouTubeVideo> ITEM_MAP = new HashMap<>();


    //Plusieurs vidéos
    static {
        addItem(new YouTubeVideo("B82d229s1LU", "Bien s'étirer pour une bonne séance de sport !"));
        addItem(new YouTubeVideo("t0vjIOqE0yU", "Comment bien courir ?"));
        addItem(new YouTubeVideo("BsmTPTO8NnI", "Comment bien respirer pendant la course ?"));
        addItem(new YouTubeVideo("MmiEmSAEK0k", "Le vélo, tout un art !"));
    }
    //Permet d'ajouter une vidéo à la liste
    private static void addItem(final YouTubeVideo item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static class YouTubeVideo {
        public String id;
        public String title;

        public YouTubeVideo(String id, String content) {
            this.id = id;
            this.title = content;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}