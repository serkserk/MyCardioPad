package com.mycardiopad.g1.mycardiopad.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.johnpersano.supertoasts.SuperToast;
import com.mycardiopad.g1.mycardiopad.R;
import com.mycardiopad.g1.mycardiopad.util.Detection_Internet;
import com.mycardiopad.g1.mycardiopad.util.Notification;
import com.mycardiopad.g1.mycardiopad.util.OkHttpSingleton;
import com.mycardiopad.g1.mycardiopad.util.ServeurURL;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Réalisé par Vishnupriya le 24/03/16. <br/>
 * Permet à l'utilisateur de récupérer son mot de passe via un email  <br/>
 */
public class Activity_RetrievePassword  extends AppCompatActivity {

    private ProgressDialog pDialog;
    private EditText editText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Affiche la view correspondant à l'activité
        setContentView(R.layout.activity_retrieve_password);

        //Cercle de progression
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Initialisation du String "email" par le String retourné par la fonction RetrieveMail
        final String email = RetrieveMail();

        Button button = (Button) findViewById(R.id.retrieve_password);

        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Si le champ editText est visible
                    if(editText.isShown()){
                        // Appel à la fonction envoieRequete avec comme paramètre la valeur saisie par l'utilisateur dans le champ editText
                        envoieRequete(editText.getText().toString());
                    }else{
                        // Appel à la fonction envoieRequete avec le paramètre "email"
                        envoieRequete(email);
                    }
                }
            });
        }

    }

    /**
     * Récupération de l'email de l'intent et mise en place des champs
     * @return l'email sélectionner dans l'activité précédante
     */
    private String RetrieveMail(){
        //Initialise l'adresse mail par le String récupéré de Activity_Login grâce à la clé "mail"
        String email = getIntent().getExtras().getString("mail");


        TextView textView = (TextView) findViewById(R.id.email_retrieve);
        TextView question = (TextView) findViewById(R.id.question);
        editText = (EditText) findViewById(R.id.email);

        if(email==null){
            // Le champ textView est invisible dans la view
            if (textView != null) {
                textView.setVisibility(View.GONE);
            }
        }else{
            // Les champs question et editText sont invisible dans la view et on met "email"
            // (qui est différent de null) dans le champ textView
            if (question != null) {
                question.setVisibility(View.GONE);
            }
            if (editText != null) {
                editText.setVisibility(View.GONE);
            }
            if (textView != null) {
                textView.setText(email);
            }
            Log.e("mail2 = ", "" + email);


        }

        // Récupération de "email"
        return email;

    }


    /**
     * Demande au serveur une requête de mot de passe pour l'email
     * @param mail l'email
     */
    private void envoieRequete(String mail){

        pDialog.setMessage("Demande au serveur...");
        showDialog();

        // Requête REST
        final Request request = new Request.Builder()
                .url( ServeurURL.FORGOT_PASSWORD + mail)
                .get()
                .build();

        // Vérification si présence d'internet
        Detection_Internet detection_internet = new Detection_Internet(getApplicationContext());
        if (detection_internet.isConnect()) {
            OkHttpSingleton.getInstance().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    hideDialog();
                    new Notification(getApplicationContext()
                            , "Erreur lors de la connexion au serveur, veuillez réessayer ultérieurement"
                            , SuperToast.Icon.Dark.EXIT);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    hideDialog();
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        String status = json.getString("status");

                        // Envoie un mail à l'utilisateur
                        if (status.equals("ok")) {
                            // Le resultCode retourné à l'Activity_Login est 2
                            setResult(2);
                            finish();

                        }
                        else if (status.equals("error")) {
                            // Le resultCode retourné à l'Activity_Login est 3
                            setResult(3);
                            finish();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            hideDialog();
            // Affiche le message "Erreur de connexion au serveur" dans la view
            View v = findViewById(android.R.id.content);
            if(v!= null){
                Snackbar snackbar = Snackbar
                        .make(v , "Erreur de connexion au serveur", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }
    }

    /**
     * Affichage ou non du Dialog
     */
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
