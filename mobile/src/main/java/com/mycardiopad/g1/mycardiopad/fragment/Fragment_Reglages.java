package com.mycardiopad.g1.mycardiopad.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mycardiopad.g1.mycardiopad.R;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Capture;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Compte;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Programme;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Succes;

import java.io.File;

/**
 * Réalisé par kevin le 02/04/2016.  <br/>
 * Permet de prendre en compte les préférences de l'applications  <br/>
 */

public class Fragment_Reglages extends Fragment {

    MenuItem fav;   //Menu contenant nos items

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reglages,container,false);
        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        fav = menu.add("A propos");
        fav = menu.add("Déconnexion");
        //Mise en place du A propos
        menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, new Fragment_WebLicence());
                ft.commit();
                return false;
            }
        });
        //Mise en place de la fonction déconnexion
        menu.getItem(1).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Construction du 1er Alertdialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setMessage("Etes-vous sûr de vouloir vous déconnecter ?")
                        .setCancelable(false)
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int bouton) {

                                // Construction du 2ème Alertdialog
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                builder.setMessage("Toutes vos données vont être supprimées ! ")
                                        .setCancelable(false)
                                        .setPositiveButton("J\'accepte", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int bouton) {

                                                SharedPreferences alarmTime;
                                                alarmTime = getContext().getSharedPreferences("userDetails", 0);
                                                SharedPreferences.Editor alarmTimeEdit = alarmTime.edit();
                                                alarmTimeEdit.clear();
                                                alarmTimeEdit.apply();

                                                deleteAll();
                                            }
                                        })
                                        .setNegativeButton("Je refuse", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int bouton) {
                                                // Retour au Fragment_Reglages
                                                dialog.cancel();
                                            }
                                        });

                                // Affiche le 2ème alertdialog
                                AlertDialog dialogue = builder.create();
                                // Définie le titre du 2ème alertdialog
                                dialogue.setTitle("Déconnexion : 2ème demande");
                                dialogue.show();
                            }
                        })
                        .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int bouton) {
                                // Retour au Fragment_Reglages
                                dialog.cancel();
                            }
                        });

                // Affiche le 1er Alertdialog
                AlertDialog dialog = builder.create();
                // Définie le titre du 1er Alertdialog
                dialog.setTitle("Déconnexion : 1ère demande");
                dialog.show();
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Suppression de l'ensemble des bases de données ainsi que la photo de l'utilisateur
     */
    private void deleteAll() {
        // Efface les BDD
        new MyDBHandler_Compte(getActivity(), null, null, 1).deleteTable();
        new MyDBHandler_Succes(getActivity(), null, null, 1).deleteTable();
        new MyDBHandler_Capture(getActivity(), null, null, 1).deleteTable();
        new MyDBHandler_Programme(getActivity(), null, null, 1).deleteTable();
        File photo = new File(getContext().getExternalFilesDir(null), "user.jpg");
        if (photo.exists()) {
            if(!photo.delete())
                Log.e("Suppression image : ", "impossible");
        }
        getActivity().finish();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.reglages_alt, new Fragment_Reglages_Compat())
                .commit();
    }

    static public class Fragment_Reglages_Compat extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        public Fragment_Reglages_Compat() {}

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences);
            SharedPreferences sp = getPreferenceScreen().getSharedPreferences();

            ListPreference langue = (ListPreference) findPreference("pref_key_langue");
            langue.setSummary(sp.getString("pref_key_langue", "Français"));

            ListPreference taille = (ListPreference) findPreference("pref_key_taille");
            taille.setSummary(sp.getString("pref_key_taille", "centimètres"));

            ListPreference poids = (ListPreference) findPreference("pref_key_poids");
            poids.setSummary(sp.getString("pref_key_poids", "kilogrammes"));

            ListPreference distance = (ListPreference) findPreference("pref_key_distance");
            distance.setSummary(sp.getString("pref_key_distance", "kilomètres"));


        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Preference pref = findPreference(key);

            if (pref instanceof ListPreference) {
                ListPreference listPref = (ListPreference) pref;
                pref.setSummary(listPref.getEntry());
            }
        }
    }
    @Override
    public String toString(){
        return "Reglages";
    }
}