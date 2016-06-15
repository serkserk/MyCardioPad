package com.mycardiopad.g1.mycardiopad.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.SuperToast;
import com.mycardiopad.g1.mycardiopad.R;
import com.mycardiopad.g1.mycardiopad.adapter.Adapter_SignUp;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Compte;
import com.mycardiopad.g1.mycardiopad.database._Compte;
import com.mycardiopad.g1.mycardiopad.fragment.Fragment_SignUp_Ecran1;
import com.mycardiopad.g1.mycardiopad.fragment.Fragment_SignUp_Ecran2;
import com.mycardiopad.g1.mycardiopad.fragment.Fragment_SignUp_Ecran3;
import com.mycardiopad.g1.mycardiopad.util.Detection_Internet;
import com.mycardiopad.g1.mycardiopad.util.Notification;
import com.mycardiopad.g1.mycardiopad.util.OkHttpSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Réalisé par nicolassalleron le 17/02/16.  <br/>
 * C'est l'écran d'inscription de l'utilisateur  <br/>
 * C'est ici que sont instanciés les différentes étapes du processus d'inscription  <br/>
 */
public class Activity_SignUp extends AppCompatActivity {

    public FloatingActionButton btnSuivant;
    private MyDBHandler_Compte db;
    private _Compte compte;
    private File EE, CNCI;
    private FloatingActionButton btnPrecedant;
    // Création de la liste de Fragments que fera défiler le Adapter_SignUp
    public final List<Fragment> fragments = new Vector<>();
    public ViewPager pager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_signup_contener);



        btnSuivant = (FloatingActionButton) findViewById(R.id.btnSuivant);
        btnPrecedant = (FloatingActionButton) findViewById(R.id.btnPrecedant);

        Fragment sign1 = new Fragment_SignUp_Ecran1();
        Fragment sign2 = new Fragment_SignUp_Ecran2();
        Fragment sign3 = new Fragment_SignUp_Ecran3();

        ((Fragment_SignUp_Ecran1) sign1).setBtn(btnSuivant);
        ((Fragment_SignUp_Ecran3) sign3).setBtn(btnSuivant);
        // Ajout des Fragments dans la liste
        fragments.add(sign1);
        fragments.add(sign2);
        fragments.add(sign3);

        // Création de l'adapter qui s'occupera de l'affichage de la liste de
        // Fragments
        PagerAdapter mPagerAdapter = new Adapter_SignUp(super.getSupportFragmentManager(), fragments) {
        };

        pager = (ViewPager) findViewById(R.id.viewpager);
        // Affectation de l'adapter au ViewPager
        if(pager != null)
            pager.setAdapter(mPagerAdapter);
        final LinearLayout layout = (LinearLayout) findViewById(R.id.signup_contener);

        db = new MyDBHandler_Compte(getBaseContext(), null, null, 1);

        btnPrecedant.hide();    //Pas de bouton précédant pour la première vue


        btnSuivant.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {

                if(pager != null && layout != null) {

                    //Affichage fragment photo profil + infos
                    if (pager.getCurrentItem() == 0) {

                        if (((Fragment_SignUp_Ecran1) fragments.get(0)).getEtatInscription()) {


                            compte = ((Fragment_SignUp_Ecran1) fragments.get(0)).getCompte();
                            pager.setCurrentItem(1);
                            Toast.makeText(Activity_SignUp.this,
                                    "Une photo du CNCI et du EE n'est pas obligatoire. " +
                                            "Seulement si vous n'en avez pas, vous serez en compte limité.",
                                    Toast.LENGTH_LONG).show();
                            btnSuivant.show();

                            /**
                             * MERCI DE NE PLUS SUPPRIMER LE .show() DE CE BOUTON.
                             */
                            btnPrecedant.show();


                        } else {

                                Snackbar.make(layout, "Il manque quelque chose. ", Snackbar.LENGTH_SHORT).show();
                        }


                        return;

                    }

                    //Affichage du fragment de demande de photo CNCI et EE
                    if (pager.getCurrentItem() == 1) {

                        //Récupération des fichiers images;
                        if (((Fragment_SignUp_Ecran2) fragments.get(1)).getFileEE() != null) {
                            EE = ((Fragment_SignUp_Ecran2) fragments.get(1)).getFileEE();
                            Log.e("EE", "Le fichier est différent de null");
                        }
                        if (((Fragment_SignUp_Ecran2) fragments.get(1)).getFileCNCI() != null) {
                            CNCI = ((Fragment_SignUp_Ecran2) fragments.get(1)).getFileCNCI();
                            Log.e("CNCI", "Le fichier est différent de null");
                        }
                        if (EE == null || CNCI == null) {
                            compte.set_compte_limite(1); //Dans ce cas on est en limité
                        } else {
                            compte.set_compte_limite(0);
                        }

                        pager.setCurrentItem(2);

                        btnSuivant.setImageResource(R.drawable.ic_action_accept);
                        btnSuivant.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGreen)));
                        btnSuivant.show();
                        return;
                    }


                    //Fragment final d'acceptation de l'inscription
                    if (pager.getCurrentItem() == 2) {


                        if (((Fragment_SignUp_Ecran3) fragments.get(2)).getEtatInscription()) {
                            compte.set_date_inscription(new Date());
                            compte.set_total_session(0);
                            compte.set_total_pas(0);
                            compte.set_id(db.lastRowCompte().get_id() + 1);
                            btnSuivant.show();
                            

                            //Requête REST
                            Detection_Internet detection_internet = new Detection_Internet(getApplicationContext());
                            if (detection_internet.isConnect()) {
                                String stringDate = new SimpleDateFormat("yyyy-MM-dd").format(compte.get_date_de_naissance());

                                MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                                        .setType(MultipartBody.FORM)
                                        .addFormDataPart("email", compte.get_email())
                                        .addFormDataPart("mot_de_passe", compte.get_mot_de_passe())
                                        .addFormDataPart("nom", compte.get_nom())
                                        .addFormDataPart("prenom", compte.get_prenom())
                                        .addFormDataPart("date_de_naissance", stringDate)
                                        .addFormDataPart("taille", "" + compte.get_taille())
                                        .addFormDataPart("poids", "" + compte.get_poids())
                                        .addFormDataPart("sexe", "" + compte.get_sexe());

                                if (((Fragment_SignUp_Ecran2) fragments.get(1)).getFileEE() != null) {
                                    multipartBuilder = multipartBuilder.addFormDataPart
                                            ("ee", "ee.png", RequestBody.create(MediaType.parse("image/png"), EE));
                                }
                                if (((Fragment_SignUp_Ecran2) fragments.get(1)).getFileCNCI() != null) {
                                    multipartBuilder = multipartBuilder.addFormDataPart
                                            ("cnci", "cnci.png", RequestBody.create(MediaType.parse("image/png"), CNCI));
                                }

                                File file = new File(getString(R.string.default_save_emplacement_user));

                                if (file.exists()) {
                                    multipartBuilder = multipartBuilder.addFormDataPart
                                            ("photo_profil", "photo_profil.jpeg", RequestBody.create(MediaType.parse("image/jpeg"), file));
                                }

                                RequestBody requestBody = multipartBuilder.build();

                                final Request request = new Request.Builder()
                                        .url("http://journaldesilver.com/api/signup/")
                                        .post(requestBody)
                                        .build();


                                OkHttpSingleton.getInstance().newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                       new Notification(getApplicationContext()
                                                , "Le serveur ne renvoie rien", SuperToast.Icon.Dark.EXIT);
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        try {
                                            String json_string = response.body().string();
                                            JSONObject json = new JSONObject(json_string);
                                            System.out.println(json_string);
                                            String status = json.getString("status");

                                            if (status.equals("ok")) {
                                                db.addCompte(compte);
                                                Intent intent = new Intent(Activity_SignUp.this, Activity_Splash.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }
                    } else {
                        new Notification(getApplicationContext()
                                , "Veuillez vous connecter à Internet", SuperToast.Icon.Dark.EXIT);
                    }
                }
            }
        });


        btnPrecedant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pager != null && pager.getCurrentItem() == 1) {
                    pager.setCurrentItem(0);
                    btnPrecedant.hide();
                }

                if (pager != null && pager.getCurrentItem() == 2) {
                    pager.setCurrentItem(1);
                    btnSuivant.setImageResource(R.drawable.ic_keyboard_arrow_right_white_24dp);
                    btnSuivant.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.MockupBlueBack)));
                }
            }
        });
    }
}
