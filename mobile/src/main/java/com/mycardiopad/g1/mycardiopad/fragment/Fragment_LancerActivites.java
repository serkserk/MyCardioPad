package com.mycardiopad.g1.mycardiopad.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mycardiopad.g1.mycardiopad.R;
import com.mycardiopad.g1.mycardiopad.adapter.Adapter_ActivitesListView;
import com.mycardiopad.g1.mycardiopad.adapter.Adapter_ListDialogs.Adapter_ListDialogs;
import com.mycardiopad.g1.mycardiopad.adapter.Adapter_ListDialogs.BaseDialog.Alignment;
import com.mycardiopad.g1.mycardiopad.adapter.Adapter_ListDialogs.CustomListDialog;
import com.mycardiopad.g1.mycardiopad.listview.List_ActivitesItem;

import java.util.ArrayList;
import java.util.List;


/**
 * Réalisé par nicolassalleron le 14/02/16. <br/>
 * Mise en place de l'interface correspondant aux activités <br/>
 */
public class Fragment_LancerActivites extends Fragment{

    TextView textView;
    String activité, environnement, mode;
    private CustomListDialog.Builder builder;
    private CustomListDialog.Builder builderDialog;
    private ArrayList<Adapter_ListDialogs> md;
    private String title;
    private CustomListDialog customListDialog;
    private String time;
    private AlertDialog dialog;
    public Fragment_SessionEnregistrement_Host fragment;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_selection_activite_enregistrement,container,false);
        textView = (TextView) v.findViewById(R.id.choixActivite);
        final ListView listView = (ListView) v.findViewById(R.id.listView1);
        initListView(listView);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                activité = ((Adapter_ActivitesListView) listView.getAdapter()).getItem(position).getName();

                //Mise en place des différents éléments de notre CustomView
                title = "Environnement";
                String[] items = {"Intérieur", "Extérieur"};
                md = new ArrayList<>();
                TypedArray images = getResources().obtainTypedArray(R.array.environnement);
                for (int i = 0; i < items.length; i++) {    //Mise en place
                    md.add(new Adapter_ListDialogs(items[i], images.getDrawable(i)));
                }
                // Création de la listDialog avec les différents paramètres, comme le context, le titre, et le tableau d'item pour remplir la liste
                builder = createBuilder(title, items, md, images);

                //Construction du dialog et affichage
                customListDialog = builder.build();
                customListDialog.show();

                //Ecoute
                customListDialog.setListClickListener(new CustomListDialog.ListClickListener() {
                    @Override
                    public void onListItemSelected(int i, String[] strings, String s) {

                        //Vérification si l'utilisateur à une montre connectée.
                        if (!PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("pref_key_montre_connectee", false)) {
                            showDialogLancerSession(s);
                        } else {

                            //Enregistrement de l'environnement
                            environnement = s;
                            //Mise en place des différents éléments de notre CustomView
                            title = "Fréquence cardiaque ?";
                            String[] items = {"Avec FC", "Sans FC"};
                            md = new ArrayList<>();
                            TypedArray images = getResources().obtainTypedArray(R.array.mode);
                            for (int j = 0; j < items.length; j++) {    //Mise en place
                                md.add(new Adapter_ListDialogs(items[j], images.getDrawable(j)));
                            }

                            // Création de la listDialog avec les différents paramètres, comme le context, le titre, et le tableau d'item pour remplir la liste
                            builder = createBuilder(title, items, md, images);


                            // Construction du dialog et affichage
                            customListDialog = builder.build();
                            customListDialog.show();


                            //Ecoute du dialog
                            customListDialog.setListClickListener(new CustomListDialog.ListClickListener() {
                                @Override
                                public void onListItemSelected(int j, String[] strings, String s) {
                                    showDialogLancerSession(s);
                                }
                            });
                        }

                    }
                });


                StateListDrawable selector = new StateListDrawable();
                //Ajoute une nouvelle image avec un identifiant
                selector.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(Color.WHITE));

                // Mise en place du selecteur
                customListDialog.getListView().setSelector(selector);


            }
        });

        return v;   //Retour de la view

    }

    /**
     * Initialisation de la ListView avec les différentes activités
     * @param listView la listView à peupler
     */
    private void initListView(ListView listView) {
        String[] names = new String[]{"Marche", "Vélo",
                "Course", "Natation", "Ski de fond", "Danse", "Tapis roulant", "Ergomètre (vélo classique)", "Autres appareils"};
        final int[] images = {R.drawable.ic_marche, R.drawable.ic_velo,
                R.drawable.ic_jogging, R.drawable.ic_natation, R.drawable.ic_ski, R.drawable.ic_danse, R.drawable.ic_tapis, R.drawable.ic_ergometre, R.drawable.ic_exercise};
        ArrayList<List_ActivitesItem> myList = new ArrayList<>();

        //Mise en place dans l'ArrayList des données
        for (int i = 0; i < names.length; i++) {
            myList.add(new List_ActivitesItem(names[i], images[i]));
        }

        //Mise en place de l'adapter dans la list
        listView.setAdapter(new Adapter_ActivitesListView(getActivity(), myList));
    }

    /**
     * Lancement de la session d'enregistrement
     * Passage d'un fragment à un autre et mise en place des différentes informations nécessaire au futur fragment
     */
    private void lancementSessionEnregistrement() {

        //Préparation du fragment
        fragment = new Fragment_SessionEnregistrement_Host();
        Bundle args = new Bundle();

        //Mise en place des données
        args.putString("activité", activité);
        args.putString("environnement", environnement);
        args.putString("mode", mode);
        args.putString("time", time);

        //Placement des arguments
        fragment.setArguments(args);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();    //Changement de fragment

    }

    private CustomListDialog.Builder createBuilder(String title, String[] items, List<Adapter_ListDialogs> md, TypedArray images) {

        //Création d'une list avec les paramètres requis comme le titre ou les tableaux d'items et d'image
        CustomListDialog.Builder builderDialog = new CustomListDialog.Builder(getContext(), title, items, md);

        // Customisation du dialog
        builderDialog.darkTheme(false);
        builderDialog.typeface(Typeface.DEFAULT);
        builderDialog.titleAlignment(Alignment.LEFT);
        builderDialog.itemAlignment(Alignment.CENTER);
        builderDialog.titleColor("RED");
        builderDialog.itemColor(0);
        builderDialog.titleTextSize(22);
        builderDialog.itemTextSize(18);
        builderDialog.rightToLeft(false);
        return builderDialog;
    }


    /**
     * Dialog
     * @param s la string à afficher
     */
    public void showDialogLancerSession(String s){

        //Nouveau fragment
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
        newFragment.setCancelable(false);

        //Configuration du fragment
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        mode = s;
        builder.setMessage("Vous reconnaissez ne pas avoir de douleur anormale sur " +
                "les derniers jours, ne pas vous sentir essoufflé ni avoir quelconque " +
                "événement qui pourrait nécessiter une visite médicale")
                .setCancelable(false)
                .setIcon(R.drawable.ic_error_outline_black_48dp)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int bouton) {
                        lancementSessionEnregistrement();
                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int bouton) {
                        dialog.cancel();
                        Toast.makeText(getContext(), "Non", Toast.LENGTH_SHORT).show();

                    }
                });

        //Affichage
        dialog = builder.create();

    }

    /**
     * Annulation de la session
     */
    public void cancelSession() {
        fragment.onDestroy();
        fragment = null;
    }


    /**
     * Selection du temps à travers ce fragment
     */
    @SuppressLint("ValidFragment")
    public class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            //Configuration du fragment
            TimePickerDialog  tpd = new TimePickerDialog(getActivity(),AlertDialog.THEME_HOLO_LIGHT
                    ,this, 0, 0, DateFormat.is24HourFormat(getActivity()));
            tpd.setTitle("Durée de votre exercice ? (non obligatoire)");
            tpd.setButton(DialogInterface.BUTTON_POSITIVE, "Valider", tpd);
            tpd.setButton(DialogInterface.BUTTON_NEGATIVE,"",tpd);

            return tpd;
        }

        //Lorsque la date est sélectionné
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            if(hourOfDay != 0) {
                time = "" + (60 * hourOfDay + minute);
            }else
              time = ""+minute;

        }

        // Affichage du dialog final quand le fragment courant est détruit
        @Override
        public void onDestroyView() {
            super.onDestroyView();
            dialog.setTitle("Attention");
            dialog.show();
        }
    }
}