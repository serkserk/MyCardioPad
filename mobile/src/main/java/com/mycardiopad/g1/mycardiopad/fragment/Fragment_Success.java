package com.mycardiopad.g1.mycardiopad.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mycardiopad.g1.mycardiopad.R;
import com.mycardiopad.g1.mycardiopad.adapter.Adapter_SuccessList;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Compte;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Succes;
import com.mycardiopad.g1.mycardiopad.database._Succes;
import com.mycardiopad.g1.mycardiopad.util.Notification;
import com.mycardiopad.g1.mycardiopad.util.OkHttpSingleton;
import com.mycardiopad.g1.mycardiopad.util.Succes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Réalisé par kevin le 14/03/2016. <br/>
 * Permet à l'utilisateur de retrouver ses succès.  <br/>
 */
public class Fragment_Success extends Fragment {

    MyDBHandler_Compte dbc;
    MyDBHandler_Succes dbs;
    Adapter_SuccessList adapter;
    ArrayList<Succes> listeSucces;
    ProgressDialog pd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_succes, container, false);

        final RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        dbc = new MyDBHandler_Compte(getContext(), null, null, 1);
        listeSucces = new ArrayList<>();

        final Request request = new Request.Builder()
                .url("http://journaldesilver.com/api/get_user_success/?email=" + dbc.getCompte(0).get_email())
                .get()
                .build();

        pd = new ProgressDialog(getContext());
        pd.setTitle("Chargement des succès en cours...");
        pd.setMessage("En attente du serveur");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.show();

        //Récupération des succès sur le serveur
        OkHttpSingleton.getInstance().newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        pd.dismiss();
                        new Notification(getContext(), "Echec du chargement",
                                R.drawable.ic_action_cancel);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        try {
                            JSONObject json = new JSONObject(response.body().string()); //Récupération de la réponse
                            String status = json.getString("status");

                            if (status.equals("ok")) {   //Dans le cas ou la requête est accepté

                                int count = json.getInt("count");
                                JSONArray liste = json.getJSONArray("success");

                                for (int i = 0; i < count; i++) {   //Récupération des Succès

                                    JSONObject succes = liste.getJSONObject(i);
                                    _Succes s = new _Succes();

                                    s.set_id(Long.parseLong(succes.getString("id")));
                                    s.set_nom(succes.getString("nom"));
                                    s.set_description(succes.getString("description"));
                                    s.set_obtenu(Integer.parseInt(succes.getString("obtenu")));

                                    if (s.get_obtenu() == 1) {
                                        try {
                                            Date sdf = new SimpleDateFormat("yyyy-MM-dd")
                                                    .parse(succes.getString("date"));
                                            s.set_date_obtention(sdf);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    dbs = new MyDBHandler_Succes(getContext(), null, null, 1);

                                    if (dbs.isIdExist(s.get_id())) { //Mise à jour des succès
                                        dbs.updateSucces(s);
                                    } else {
                                        dbs.addSucces(s);       //Mise en place des succès
                                    }

                                    Succes success = new Succes(s.get_nom(), s.get_description(),
                                            (s.get_obtenu() == 0) ? R.drawable.ic_bloque :
                                                    imageSucces(succes.getString("image_asset")));
                                    listeSucces.add(success);   //Ajout de la liste des succès
                                }
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() { //Mise en place de l'adapter
                                        adapter = new Adapter_SuccessList(listeSucces);
                                        recyclerView.setHasFixedSize(true);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                        recyclerView.setAdapter(adapter);
                                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                                    }
                                });

                                if (dbs.getSucces(1).get_obtenu() == 0) {

                                    // Création du premier succès
                                    MyDBHandler_Compte myDBHandler_compte = new MyDBHandler_Compte(getContext(), null, null, 1);
                                    RequestBody formBody = new FormBody.Builder()
                                            .add("email", myDBHandler_compte.getCompte(0).get_email())
                                            .add("success_id", "1")
                                            .build();

                                    Request request_cadeau = new Request.Builder()
                                            .url("http://journaldesilver.com/api/update_success/")
                                            .post(formBody)
                                            .build();

                                    OkHttpSingleton.getInstance().newCall(request_cadeau).enqueue(new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            try {
                                                String status = new JSONObject(response.body().string()).getString("status");
                                                if (status.equals("ok")) {
                                                    _Succes cadeau_bienvenue = dbs.getSucces(1);    //Premier succès
                                                    cadeau_bienvenue.set_obtenu(1);
                                                    cadeau_bienvenue.set_date_obtention(new Date());
                                                    dbs.updateSucces(cadeau_bienvenue);
                                                    Succes s = listeSucces.get(0);
                                                    s.setImage(R.drawable.ic_premiere_connexion);
                                                    listeSucces.set(0, s);
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            recyclerView.getAdapter().notifyDataSetChanged();
                                                        }
                                                    });

                                                    // Notification pour le 1er succès
                                                    new Notification(getContext(), "Vous venez de débloquer un succès !"
                                                            , R.drawable.ic_premiere_connexion);
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            }
                        } catch (JSONException j) {
                            j.printStackTrace();
                        }
                        pd.dismiss();
                    }
                });
        return v;
    }

    /**
     * Retourne l'id ressource de l'image selon la string qui l'identifie
     * @param asset la string qui permet de l'identifier
     * @return la ressource id
     */
    public int imageSucces(String asset) {
        int ressource;
        switch (asset) {
            case "premiere_connexion":
                ressource = R.drawable.ic_premiere_connexion;
                break;
            case "premiere_session":
                ressource = R.drawable.ic_premiere_session;
                break;
            case "dix_connexions":
                ressource = R.drawable.ic_dix_connexions;
                break;
            case "indice_75":
                ressource = R.drawable.ic_indice_75;
                break;
            case "semaine_parfaite":
                ressource = R.drawable.ic_semaine_parfaite;
                break;
            case "10000_pas":
                ressource = R.drawable.ic_marcheur;
                break;
            case "multisport":
                ressource = R.drawable.ic_multisport;
                break;
            case "sportif_extreme":
                ressource = R.drawable.ic_sportif_extreme;
                break;
            case "platine":
                ressource = R.drawable.ic_platine;
                break;
            default:
                ressource = R.drawable.ic_bloque;
        }
        return ressource;
    }
}