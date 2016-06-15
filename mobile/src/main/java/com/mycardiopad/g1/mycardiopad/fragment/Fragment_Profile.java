package com.mycardiopad.g1.mycardiopad.fragment;

/**
 * Réalisé par nicolassalleron le 17/01/16. <br/>
 * Affiche les différentes informations de l'utilisateur  <br/>
 */

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycardiopad.g1.mycardiopad.R;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Capture;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Compte;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Programme;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Succes;
import com.mycardiopad.g1.mycardiopad.database._Capture;
import com.mycardiopad.g1.mycardiopad.database._Compte;
import com.mycardiopad.g1.mycardiopad.database._Programme;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Fragment_Profile extends Fragment {

    private int maxFreq = 170;
    private int minFreq = 50;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profil, container, false);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Profil");

        //Initialisation interface
        ImageView imgUtilisateur = (ImageView) rootView.findViewById(R.id.backdrop);
        TextView txtUtilisateur = (TextView) rootView.findViewById(R.id.txtUtilisateur);
        TextView txtInfo = (TextView) rootView.findViewById(R.id.txtInfo);
        TextView txtMesure = (TextView) rootView.findViewById(R.id.mesureInfo);
        TextView txtPodo = (TextView) rootView.findViewById(R.id.pasInfo);
        TextView txtFreqMax = (TextView) rootView.findViewById(R.id.freqMaxInfo);
        TextView txtFreqMin = (TextView) rootView.findViewById(R.id.freqMinInfo);
        TextView txtInfoDateInscrition = (TextView) rootView.findViewById(R.id.infoDateInscription);
        TextView txtBadge = (TextView) rootView.findViewById(R.id.txtBadge);
        TextView txtDesc = (TextView) rootView.findViewById(R.id.txtDescBadge);
        ImageView imgBadge = (ImageView) rootView.findViewById(R.id.badge);

        imgBadge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new Fragment_Success();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame,fragment);
                ft.commit();
            }
        });

        //Mise en place des bases de données
        MyDBHandler_Compte dbHandler_compte = new MyDBHandler_Compte(getActivity(), null, null, 1);
        MyDBHandler_Succes myDBHandler_succes = new MyDBHandler_Succes(getActivity(),null,null,1);

        //Mise en place des données dans le profil
        if (dbHandler_compte.numberLine() == 1) {
            _Compte compte = dbHandler_compte.lastRowCompte();

            // Informations générales
            txtUtilisateur.setText(compte.get_prenom() + " " + compte.get_nom());

            Calendar naissance = Calendar.getInstance();
            naissance.setTime(compte.get_date_de_naissance());

            Calendar now = (Calendar.getInstance());

            int age = now.get(Calendar.YEAR) - naissance.get(Calendar.YEAR);

            if (naissance.get(Calendar.MONTH) > now.get(Calendar.MONTH) || naissance.get(Calendar.MONTH) == now.get(Calendar.MONTH) && naissance.get(Calendar.DATE) > now.get(Calendar.DATE)) {
                age--;
            }

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());

            String tailleUnite = sp.getString("pref_key_taille", "centimètres");
            String poidsUnite = sp.getString("pref_key_poids", "kilogrammes");
            String taille;
            String poids;

            if (tailleUnite.equals("pouces")) {
                taille = conversionTaille(compte.get_taille()) + " pouces";
            }
            else {
                taille = compte.get_taille() + " cm";
            }

            if (poidsUnite.equals("livres")) {
                poids = conversionPoids(compte.get_poids()) + " lb";
            }
            else {
                poids = compte.get_poids() + " kg";
            }

            txtInfo.setText(taille + " | " + poids + " | " + age + " ans");

            // Photo de profil
            File photo = new File(getString(R.string.default_save_emplacement_user));
            if (photo.exists()) {
                Bitmap photo_utilisateur = BitmapFactory.decodeFile(photo.getAbsolutePath());
                imgUtilisateur.setImageBitmap(photo_utilisateur);
            }

            //Dernier succès obtenu
            if(myDBHandler_succes.lastObtainedSucces() != null) {
                txtBadge.setText(myDBHandler_succes.lastObtainedSucces().get_nom());
                txtDesc.setText(myDBHandler_succes.lastObtainedSucces().get_description());
                imgBadge.setImageResource(imageSucces(myDBHandler_succes.lastObtainedSucces().get_id()));
            }
            else {
                txtBadge.setText("Vous n'avez pas encore de badge");
                txtDesc.setText("Veuillez continuer à utiliser l'application");
            }

            // Date d'inscription
            txtInfoDateInscrition.setText(new SimpleDateFormat("yyyy-MM-dd").format(compte.get_date_inscription()));

            // Nombres de pas et sessions
            txtMesure.setText(new MyDBHandler_Capture(getActivity(),null,null,1).numberLine() + " sessions");
            //txtPodo.setText(compte.get_total_pas() + " pas");

            MyDBHandler_Programme dbHandler_programme = new MyDBHandler_Programme(getContext(), null, null, 1);
            if(dbHandler_programme.numberLine() >0){
                maxFreq = dbHandler_programme.lastRowProgramme().get_maxFreq();
                minFreq = dbHandler_programme.lastRowProgramme().get_minFreq();
            }
            // Infos provenant du programme
            if (dbHandler_programme.numberLine() > 0) {
                _Programme programme = dbHandler_programme.lastRowProgramme();
                txtFreqMax.setText(programme.get_maxFreq() + " bpm");
                txtFreqMin.setText(programme.get_minFreq() + " bpm");
            }

            MyDBHandler_Capture dbCapture = new MyDBHandler_Capture(getActivity(), null, null, 1);

            int totalpas = 0;

                //Calcul du nombre total de pas
                for (int i = 0; i < dbCapture.numberLine(); i++) { //Tout les enregistrements
                    _Capture cap = dbCapture.getMesure(i);
                    String podo = cap.get_podo();
                    String[] splitpodo = podo.split("µ");

                    for (int j = 0; j < splitpodo.length; j++) {
                        totalpas += Float.parseFloat(splitpodo[++j]); //Deuxième champs
                        j++;
                    }
                }
            txtPodo.setText(totalpas + " pas");
        }
        return rootView;
    }

    /**
     * Récupère l'image du succès
     * @param succes_position la position du succes
     * @return Retourne l'entier correspondant à l'image du succes
     */
    public int imageSucces(long succes_position) {
        int ressource;
        switch ((int)succes_position) {
            case 1:
                ressource = R.drawable.ic_premiere_connexion;
                break;
            case 2:
                ressource = R.drawable.ic_premiere_session;
                break;
            case 3:
                ressource = R.drawable.ic_dix_connexions;
                break;
            case 4:
                ressource = R.drawable.ic_indice_75;
                break;
            case 5:
                ressource = R.drawable.ic_semaine_parfaite;
                break;
            case 6:
                ressource = R.drawable.ic_marcheur;
                break;
            case 7:
                ressource = R.drawable.ic_multisport;
                break;
            case 8:
                ressource = R.drawable.ic_sportif_extreme;
                break;
            case 9:
                ressource = R.drawable.ic_platine;
                break;
            default:
                ressource = R.drawable.ic_bloque;
        }
        return ressource;
    }

    /**
     * Connversion des kg en livres
     * @param poids en kg
     * @return poids en livre
     */
    public int conversionPoids (int poids) {
        return (int) (2.20462F * poids);
    }

    /**
     * Conversion des cm en pouce
     * @param taille en cm
     * @return taille en pouce
     */
    public int conversionTaille(int taille) {
        return (int) (0.393701F * taille);
    }
}