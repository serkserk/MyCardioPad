package com.mycardiopad.g1.mycardiopad.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycardiopad.g1.mycardiopad.R;

/**
 * Réalisé par nicolassalleron le 21/02/16.  <br/>
 * Fragment correspondant à l'exercice du jour <br/>
 * Affiche également l'image de l'exercice <br/>
 */
public class Fragment_MonProgramme_Ecran1 extends Fragment {

    String exercice, duree;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mon_programme_fragment_1,container,false);

        Bundle bundle = getArguments();
        this.exercice = (String) bundle.get("exercice");
        this.duree = (String) bundle.get("duree");

        ImageView imgActivite = (ImageView) v.findViewById(R.id.imageActivite);
        TextView txtActivité = (TextView) v.findViewById(R.id.TextActivite);
        TextView txtActivitéConseillé = (TextView) v.findViewById(R.id.TextActiviteConseille);

        if(!(duree.isEmpty())){    //Affichage de l'exercice

            imgActivite.setImageResource(getResIdFromActivityString(exercice));
            txtActivité.setText("En salle pendant " + duree +" minutes");
            txtActivitéConseillé.setText("Votre activité conseillée : "+exercice);

        }else if(duree.equals("")){             //Affichage par défault

            imgActivite.setImageResource(R.mipmap.ic_launcher);
            txtActivitéConseillé.setText(exercice);
            txtActivité.setText("");
        }
        return v;
    }

    /**
     * Récupération de l'image suivant l'exercice
     * @param exercice l'exercice
     * @return l'identifiant de la ressource
     */
    private int getResIdFromActivityString(String exercice) {

        String[] names = new String[]{"Marche", "Vélo",
                "Course", "Natation", "Ski de fond", "Danse", "Tapis roulant", "Ergomètre (vélo classique)", "Autres appareils"};
        final int[] images = {R.drawable.ic_marche, R.drawable.ic_velo,
                R.drawable.ic_jogging, R.drawable.ic_natation, R.drawable.ic_ski, R.drawable.ic_danse, R.drawable.ic_tapis, R.drawable.ic_ergometre, R.drawable.ic_exercise};
        for(int i = 0;i<names.length;i++){
            if(names[i].contains(exercice)){
                return images[i];
            }
        }
        return Integer.parseInt(null);
    }
}
