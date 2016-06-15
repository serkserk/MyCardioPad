package com.mycardiopad.g1.mycardiopad.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.progresspageindicator.ProgressPageIndicator;
import com.mycardiopad.g1.mycardiopad.R;
import com.mycardiopad.g1.mycardiopad.adapter.Adapter_MonProgramme_Pager_Inside;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Compte;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Programme;
import com.mycardiopad.g1.mycardiopad.database._Compte;
import com.mycardiopad.g1.mycardiopad.database._Programme;
import com.mycardiopad.g1.mycardiopad.util.Detection_Internet;
import com.mycardiopad.g1.mycardiopad.util.OkHttpSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Réalisé par nicolassalleron le 21/02/16. <br/>
 * Mise en place de l'interface correspondant au programme  <br/>
 */
public class Fragment_MonProgramme_Host extends Fragment {


    ImageView img0,img1,img2,img3,img4,img5,img6;
    private ProgressDialog pd;
    private MyDBHandler_Programme db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mon_programme, container, false);

        //Récupération du compte du patient
        MyDBHandler_Compte dbHandler_compte = new MyDBHandler_Compte(getContext(), null, null, 1);
        _Compte compte = dbHandler_compte.lastRowCompte();

        db = new MyDBHandler_Programme(getActivity(), null, null, 1);

        //Vérification de la détection d'internet pour mettre à jour le programme
        if (new Detection_Internet(getContext()).isConnect()) {
            pd = new ProgressDialog(getContext());
            pd.setTitle("Chargement du programme en cours...");
            pd.setMessage("En attente du serveur");
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.show();
            updateProgramme(compte.get_email(), v);
        }

        if(db.numberLine() == 1){
            updateUI(v);    //Mise en place de l'interface
        }else{
            updateUISansProgramme(v);
        }

        return v;

    }

    private void updateUISansProgramme(View v) {
        ((TextView) v.findViewById(R.id.JoursSemaine)).setText("Vous n'avez pas de programme !");
        ((TextView) v.findViewById(R.id.Duree)).setText("merci de mettre à jour votre compte sur le site :¬)");
        ((TextView) v.findViewById(R.id.Duree)).setTextSize(14);
        v.findViewById(R.id.PratiquerDuSport).setVisibility(View.INVISIBLE);
        v.findViewById(R.id.layoutBleu).setVisibility(View.INVISIBLE);
        v.findViewById(R.id.layoutRouge).setVisibility(View.INVISIBLE);
    }

    /**
     * Mise en place des différentes informations du patient
     * Mise en place des images correspondant au jour
     * @param v la vue
     */
    private void updateUI(View v) {



        ImageView[] imageView = {img0,img1,img2,img3,img4,img5,img6};
        int [] resId = {R.id.img0,R.id.img1,R.id.img2,R.id.img3,R.id.img4,R.id.img5,R.id.img6};

        //Initialisation des imagesView
        for(int i = 0;i<7;i++){
            imageView[i] = (ImageView) v.findViewById(resId[i]);
        }

        TextView jourSemaine = (TextView) v.findViewById(R.id.JoursSemaine);
        TextView jourDuree = (TextView) v.findViewById(R.id.Duree);


        _Programme programme = db.lastRowProgramme();
        String exercice_Jours = programme.get_ExerciceJour();
        String[] jours = exercice_Jours.split("µ"); //Exercice concernant le jour
        String programmeJours = programme.get_ProgrammeJours(); //Récupération du programme des jours
        String[] joursDetail = programmeJours.split("/");     //Récupération du programme du Jour.

        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        int currentDay = cal.get(Calendar.DAY_OF_WEEK)-2; //Commence à 1; Récupération du jour courant
        if (currentDay < 0)
            currentDay = 6;
        Log.e("CurrentDay ",""+currentDay);

        int jourSem = 0;
        int duree =0;
        for (int i = 0;i <7;i++){           //0 = Lundi, 6 = Dimanche
            if((Integer.parseInt(jours[i])==1)){
                jourSem+=1;
            }else{
                imageView[i].setVisibility(View.INVISIBLE);
            }
            if((Integer.parseInt(jours[i])==1 && i == currentDay)){ //Dans le cas ou nous sommes
                String[] exerciceDuJour = joursDetail[i].split("µ");
                for (int j = 0;j <exerciceDuJour.length;j++){   //Affichage des fragments
                    ++j;
                    duree+=Integer.parseInt(exerciceDuJour[++j]);
                }
            }
        }

        jourSemaine.setText(jourSem+" jours/semaine");
        if(duree == 0){
            jourDuree.setVisibility(View.INVISIBLE);
        }else {
            jourDuree.setText(duree+"min aujourd'hui");
        }

        ProgressPageIndicator pagerIndicator = (ProgressPageIndicator) v.findViewById(R.id.pageIndicator);

        //Mise en place de l'adapter
        Adapter_MonProgramme_Pager_Inside myPagerAdapter = new Adapter_MonProgramme_Pager_Inside(this.getChildFragmentManager(), getContext());
        ViewPager myPager = (ViewPager) v.findViewById(R.id.MonProgrammePagerAdapter);
        myPager.setAdapter(myPagerAdapter);

        //Mise en place du PageIndicator
        pagerIndicator.setViewPager(myPager);
        pagerIndicator.setGravity(Gravity.CENTER_HORIZONTAL);
        pagerIndicator.setFillColor(getResources().getColor(R.color.MockupRedText));

        //Cas particulier, dans le cas ou juste avant l'utilisateur n'avait pas de compte
        v.findViewById(R.id.PratiquerDuSport).setVisibility(View.VISIBLE);
        v.findViewById(R.id.layoutBleu).setVisibility(View.VISIBLE);
        v.findViewById(R.id.layoutRouge).setVisibility(View.VISIBLE);
    }

    /**
     * Mise à jour du programme de l'utilisateur
     * @param email l'adresse email de l'utilisateur
     * @param v la view de l'utilisateur
     */
    private void updateProgramme(String email, final View v) {
        Request request = new Request.Builder()
                .url("http://journaldesilver.com/api/get_current_ordonnance/?email=" + email)
                .get()
                .build();

        OkHttpSingleton.getInstance().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                pd.dismiss();
                Log.e("errorInitProgramme", "Erreur lors de la récupération du programme");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                MyDBHandler_Programme db_programme = new MyDBHandler_Programme(getContext(), null, null, 1);

                try {
                    JSONObject json = new JSONObject(response.body().string());
                    String status = json.getString("status");

                    if (status.equals("ok")) {
                        JSONObject ordonnance = json.getJSONObject("ordonnance");

                        Date date_debut = new SimpleDateFormat("yyyy-MM-dd").parse(ordonnance.getString("dateDebut"));
                        if (db_programme.numberLine() > 0) {
                            _Programme programme = db_programme.lastRowProgramme();

                            if (programme.get_DateDebutPrescription() < date_debut.getTime()) {

                                programme.set_id(db_programme.numberLine());
                                programme.set_DateDebutPrescription(date_debut.getTime());
                                programme.set_maxFreq(ordonnance.getInt("maxFreq"));
                                programme.set_minFreq(ordonnance.getInt("minFreq"));
                                programme.set_ProgrammeJours(ordonnance.getString("exerciceJour"));
                                programme.set_ExerciceJour(ordonnance.getString("programmeJours"));

                                db_programme.updateProgramme(programme);
                            }
                        }
                        else {
                            _Programme programme = new _Programme();

                            programme.set_id(db_programme.numberLine());
                            programme.set_DateDebutPrescription(date_debut.getTime());
                            programme.set_maxFreq(ordonnance.getInt("maxFreq"));
                            programme.set_minFreq(ordonnance.getInt("minFreq"));
                            programme.set_ProgrammeJours(ordonnance.getString("exerciceJour"));
                            programme.set_ExerciceJour(ordonnance.getString("programmeJours"));

                            db_programme.addProgramme(programme);
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateUI(v);
                            }
                        });
                    }


                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }

                pd.dismiss();
            }
        });
    }
}

