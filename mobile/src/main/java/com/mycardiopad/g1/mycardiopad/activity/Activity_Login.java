package com.mycardiopad.g1.mycardiopad.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.SuperToast;
import com.mycardiopad.g1.mycardiopad.R;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Compte;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Programme;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Succes;
import com.mycardiopad.g1.mycardiopad.database._Compte;
import com.mycardiopad.g1.mycardiopad.database._Programme;
import com.mycardiopad.g1.mycardiopad.database._Succes;
import com.mycardiopad.g1.mycardiopad.util.Detection_Internet;
import com.mycardiopad.g1.mycardiopad.util.Notification;
import com.mycardiopad.g1.mycardiopad.util.OkHttpSingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import static java.lang.Character.isDigit;
import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;

/**
 * Réalisé par Admin le 20/01/16 <br/>
 * Login de l'utilisateur <br/>
 * Si l'utilisateur n'a pas de mot de passe permet de lui envoyer un nouvel email  <br/>
 * Récupère l'image de l'utilisateur ainsi que les différentes informations de son compte dans le cas d'un login classique  <br/>
 */
public class Activity_Login extends AppCompatActivity {


    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private ProgressDialog pDialog;
    MyDBHandler_Compte db;
    public final boolean[] valeurEnvoi = new boolean[1];

    private static final String PASSWORD_PATTERN =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,20})";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);


        getSupportActionBar().setCustomView(R.layout.actionbar);

        //Mise en forme du formulaire de login
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        TextView tv = (TextView) findViewById(R.id.name);
        mPasswordView = (EditText) findViewById(R.id.password);
        ImageView imageView = (ImageView) findViewById(R.id.image_password_miss);
        Button mEmailSignInButton = (Button) findViewById(R.id.sign_in_button);
        Button mRegisterButton = (Button) findViewById(R.id.register_in_button);

        //Cercle de progression
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        //On initialise le handler permettant de gérer la base de données du compte local
        db = new MyDBHandler_Compte(getBaseContext(), null, null, 1);

        //On teste si l'utilisateur est déjà connecté ou non
        if (db.numberLine() == 1) {
            //Si l'utilisateur est déjà connecté, alors on lance Activity_Main
            Intent intent = new Intent(Activity_Login.this, Activity_Main.class);
            startActivity(intent);
            finish();
        }

        assert imageView != null;
        imageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String email = mEmailView.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    email = null;
                }

                Intent i = new Intent(getApplicationContext(), Activity_RetrievePassword.class);
                i.putExtra("mail", email);
                startActivityForResult(i, 1);
            }
        });

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        assert tv != null;
        tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Activity_Main.class);
                startActivityForResult(intent, 0);
            }
        });

        assert mEmailSignInButton != null;
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmailView.getText().toString().trim();
                String password = mPasswordView.getText().toString().trim();

                // Vérification du contenu des champs
                if (!email.isEmpty() && !password.isEmpty()) {
                    login(email, password);

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Merci de remplir tous les champs !", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        assert mRegisterButton != null;
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Activity_SignUp.class);
                startActivityForResult(intent, 0);
            }
        });

    }

    /**
     * Contrôle d'erreur sur le login
     */
    private void attemptLogin() {

        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        boolean majuscule = false;
        boolean minuscule = false;
        boolean chiffre = false;

        for (int i = 0; i < password.length() && !(majuscule && minuscule && chiffre); i++) {
            char caractere = password.charAt(i);
            majuscule = majuscule || isUpperCase(caractere);
            minuscule = minuscule || isLowerCase(caractere);
            chiffre = chiffre || isDigit(caractere);
        }

        //Vérifie si le mot de passe est valide
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            if (password.length() < 8) {
                mPasswordView.setError(getString(R.string.error_password_length));
                focusView = mPasswordView;
                cancel = true;
            } else if (!majuscule) {
                mPasswordView.setError(getString(R.string.error_password_upper));
                focusView = mPasswordView;
                cancel = true;
            } else if (!minuscule) {
                mPasswordView.setError(getString(R.string.error_password_lower));
                focusView = mPasswordView;
                cancel = true;
            } else if (!chiffre) {
                mPasswordView.setError(getString(R.string.error_password_digit));
                focusView = mPasswordView;
                cancel = true;
            }
        }

        //Vérifie si l'adresse mail est correcte
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            //S'il y a une erreur, un focus aura lieu sur le premier champs incorrect
            focusView.requestFocus();
        }
    }

    /**
     * Vérification si l'email contient un @
     * @param email l'adresse de l'utilisateur
     * @return vrai ou faux
     */
    public boolean isEmailValid(String email) {
        return email.contains("@");
    }



    /**
     * Vérification si un password correspond bien au pattern
     * @param password le mot de passe de l'utilisateur
     * @return vrai ou faux si pwd valid
     */
    public boolean isPasswordValid(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    /**
     * Vérification des codes d'erreur pour retrouver le mot de passe
     * @param requestCode Le code de lancement de l'activité
     * @param resultCode Le code de résultat du lancement de l'activité
     * @param data Les data recu de l'activité fermé
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        View v = findViewById(android.R.id.content);
        if (requestCode == 1) {
            if (resultCode == 2) {
                String message = getString(R.string.email_envoyé);
                if(v!=null) {
                    Snackbar snackbar = Snackbar
                            .make(v, message, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            } else if (resultCode == 3) {
                String message = getString(R.string.email_problem);
                if(v!=null) {
                    Snackbar snackbar = Snackbar
                            .make(v, message, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        }

    }

    /**
     * Connexion au serveur
     */
    public void login(final String email, final String password) {


        pDialog.setMessage(getString(R.string.connexion));
        showDialog();

        // Requête REST
        final Request[] request = {new Request.Builder()
                .url("http://journaldesilver.com/api/login/?email=" + email + "&mot_de_passe=" + md5(password))
                .get()
                .build()};

        //Vérification de la présence d'internet
        Detection_Internet detection_internet = new Detection_Internet(getApplicationContext());
        if (detection_internet.isConnect()) {
            okHttpCall(request);
        } else {
                hideDialog();
            new Notification(getApplicationContext()
                    , "Veuillez vous connecter à Internet", SuperToast.Icon.Dark.EXIT);
        }
    }

    public void okHttpCall(Request[] request) {
        OkHttpSingleton.getInstance().newCall(request[0]).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                hideDialog();
                new Notification(getApplicationContext()
                        , "Erreur lors de la connexion au serveur, veuillez réessayer ultérieurement"
                        , SuperToast.Icon.Dark.EXIT);
                valeurEnvoi[0] = false;

            }
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(pDialog != null)
                      hideDialog();
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    String status = json.getString("status");

                    if (status.equals("ok")) {
                        final JSONObject user = json.getJSONObject("user");
                        _Compte c = new _Compte();

                        c.set_email(user.getString("email"));
                        c.set_nom(user.getString("nom"));
                        c.set_prenom(user.getString("prenom"));
                        c.set_sexe(Integer.parseInt(user.getString("sexe")));
                        c.set_taille(Integer.parseInt(user.getString("taille")));
                        c.set_poids(Integer.parseInt(user.getString("poids")));
                        c.set_compte_limite(Integer.parseInt(user.getString("compte_limite")));
                        c.set_total_session(Integer.parseInt(user.getString("total_sessions")));
                        c.set_total_pas(Integer.parseInt(user.getString("total_pas")));
                        try {
                            Date date_de_naissance = new SimpleDateFormat("yyyy-MM-dd").parse(user.getString("date_de_naissance"));
                            Date date_inscription = new SimpleDateFormat("yyyy-MM-dd").parse(user.getString("date_inscription"));
                            c.set_date_de_naissance(date_de_naissance);
                            c.set_date_inscription(date_inscription);
                        } catch (ParseException e) {
                            Log.e("parseexp", e.getMessage());
                        }

                        MyDBHandler_Compte handlerCompte = new MyDBHandler_Compte(getApplicationContext(), null, null, 1);
                        handlerCompte.addCompte(c);

                        recupPhoto(user.getLong("id"));
                        initSuccess(c.get_email());
                        initProgramme(c.get_email());


                        valeurEnvoi[0] = true;
                        // Passage à la vue principale une fois connecté
                        Intent intent = new Intent(Activity_Login.this, Activity_Main.class);
                        startActivity(intent);
                        finish();

                    } else {
                        try{
                            new Notification(getApplicationContext(), "Le serveur renvoie une erreur", SuperToast.Icon.Dark.EXIT);
                        }catch (Exception ignored){}
                        valeurEnvoi[0] = false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Initialisation des succes de l'utilisateur
     * @param email l'email de l'utilisateur
     */
    private void initSuccess(String email) {
        Request request = new Request.Builder()
                .url("http://journaldesilver.com/api/get_user_success/?email=" + email)
                .get()
                .build();

        OkHttpSingleton.getInstance().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("errorInitSuccess", "Erreur lors de la récupération des succès");
            }

            @Override
            @SuppressLint("SimpleDateFormat")
            public void onResponse(Call call, Response response) throws IOException {
                MyDBHandler_Succes db_succes = new MyDBHandler_Succes(getApplicationContext(), null, null, 1);
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    String status = json.getString("status");

                    if (status.equals("ok")) {

                        JSONArray liste_success = json.getJSONArray("success");
                        for (int i = 0; i < liste_success.length(); i++) {
                            JSONObject succes = liste_success.getJSONObject(i);
                            _Succes s = new _Succes();
                            s.set_id(Long.parseLong(succes.getString("id")));
                            s.set_nom(succes.getString("nom"));
                            s.set_description(succes.getString("description"));
                            s.set_obtenu(Integer.parseInt(succes.getString("obtenu")));
                            if (s.get_obtenu() == 1) {
                                s.set_date_obtention(new SimpleDateFormat("yyyy-MM-dd").parse(succes.getString("date")));
                            }

                            if (db_succes.isIdExist(s.get_id())) {
                                db_succes.updateSucces(s);
                            } else {
                                db_succes.addSucces(s);
                            }
                        }
                        db_succes.close();
                    }
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Récupération du programme et mise en place local suivant l'email de l'utilisateur
     * @param email email associé au programme de l'utilisateur
     */
    private void initProgramme(String email) {
        Request request = new Request.Builder()
                .url("http://journaldesilver.com/api/get_current_ordonnance/?email=" + email)
                .get()
                .build();

        OkHttpSingleton.getInstance().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("errorInitProgramme", "Erreur lors de la récupération du programme");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                MyDBHandler_Programme db_programme = new MyDBHandler_Programme(getApplicationContext(), null, null, 1);
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    String status = json.getString("status");

                    if (status.equals("ok")) {
                        JSONObject ordonnance = json.getJSONObject("ordonnance");

                        Date date_debut = new SimpleDateFormat("yyyy-MM-dd").parse(ordonnance.getString("dateDebut"));
                        _Programme programme = db_programme.lastRowProgramme();

                        programme.set_id(db_programme.numberLine());
                        programme.set_DateDebutPrescription(date_debut.getTime());
                        programme.set_maxFreq(ordonnance.getInt("maxFreq"));
                        programme.set_minFreq(ordonnance.getInt("minFreq"));
                        programme.set_ProgrammeJours(ordonnance.getString("exerciceJour"));
                        programme.set_ExerciceJour(ordonnance.getString("programmeJours"));

                        db_programme.addProgramme(programme);
                    }


                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Récupération de la photo suivant l'identifiant de l'utilisateur
     * @param id l'id de l'utilsateur
     */
    private void recupPhoto(long id) {
        try {
            Bitmap photo = Picasso.with(getApplicationContext()).load("http://journaldesilver.com/uploaded_images/" +
                   id + "/pp/pp_" + id + ".jpeg").get();
            FileOutputStream out = null;
            try {
                File file = new File(getString(R.string.default_save_emplacement_user));
                out = new FileOutputStream(file);
                photo.compress(Bitmap.CompressFormat.JPEG, 70, out);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Affiche ou non le dialog
     */
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    /**
     * Permet de hasher une chaîne de caractère en MD5
     *
     * @param in la chaîne à haser
     * @return la chaîne hashée
     */
    public String md5(String in) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(in.getBytes());
            byte[] a = digest.digest();
            int len = a.length;
            StringBuilder sb = new StringBuilder(len << 1);
            for (byte anA : a) {
                sb.append(Character.forDigit((anA & 0xf0) >> 4, 16));
                sb.append(Character.forDigit(anA & 0x0f, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}