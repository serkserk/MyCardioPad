package com.mycardiopad.g1.mycardiopad.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.mycardiopad.g1.mycardiopad.R;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Capture_Wear;
import com.mycardiopad.g1.mycardiopad.database._Capture_Wear;
import com.mycardiopad.g1.mycardiopad.util.CompteARebours;
import com.mycardiopad.g1.mycardiopad.util.MesureValue;
import com.mycardiopad.g1.mycardiopad.util.SendToDataLayerThread;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Réalisé par nicolassalleron   <br/>
 * Récupération de la fréquence cardiaque    <br/>
 * Communication avec la montre <br/>
 */
public class Activity_Wear_Main extends WearableActivity implements SensorEventListener,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{

    //Nos différentes éléments d'interface
    private TextView rateTv ;//,timeTv;
    private RelativeLayout relativeLayout;
    private ImageView imgMax, imgMin, imgRate;
    private ImageView imgStatus;

    //Les différents capteurs et l'api client pour communiquer avec le mobile
    private GoogleApiClient googleClient;
    private Sensor mHeartRateSensor, mStepCountSensor;
    private SensorManager mSensorManager;

    //Notification
    NotificationCompat.Builder notificationBuilder;
    NotificationManagerCompat notificationManager;
    int notificationId = 1;

    //MesureValue
    ArrayList<MesureValue> mesureValueArrayList = new ArrayList<>();
    //MesurePodomètre
    ArrayList<MesureValue> mesurePodoArrayList = new ArrayList<>();
    //Compte à Rebours
    CompteARebours compteaRebours;
    long timer = 0;

    //Maximum et Minimum pour le BPM;
    int maxFreq = 180, minFreq=60, currentFreq = 100;
    private int imgMaxWidth;
    private int imgMaxHeight;
    @SuppressWarnings("unused")
    private int imgMinWidth;
    @SuppressWarnings("unused")
    private int imgMinHeight;
    private Handler uiHandler;
    //Base de données
    MyDBHandler_Capture_Wear dbCapture;

    //Vibration
    Vibrator v;

    //Enregistrement
    private boolean enregistrement = false;
    private boolean premierefois = true;
    //Le status de l'activité (en cours = 0, en pause = 1 , stop = 1)
    int status;

    //Heure de début et de fin de l'activité
    long startRecord,endRecord;

    //ProgressBar
    CircularProgressBar circularProgressBar ;

    //Thread d'update de l'UI
    Thread UpdateUI= new Thread() {

        @Override
        public void run() {
            initThreadUI();
        }

        private void initThreadUI() {
            try {
                while (!isInterrupted()) {
                    Thread.sleep(1000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Mise à jour de l'interface
                            if(rateTv != null){
                                rateTv.setText(String.valueOf(currentFreq));
                                rateTv.startAnimation(AnimationUtils.loadAnimation(Activity_Wear_Main.this, R.anim.pulse));
                                imgRate.startAnimation(AnimationUtils.loadAnimation(Activity_Wear_Main.this, R.anim.pulse_low));
                            }
                            updateUI(currentFreq);
                        }
                    });
                    if(compteaRebours != null) {
                        if(currentFreq>maxFreq || currentFreq<minFreq){
                            // TODO: 20/03/16 Update les vibrations
                            if(enregistrement)
                                v.vibrate(100);
                        }
                        if (compteaRebours.isFinish()) { // Dans ce cas, enregistrement dans la base de données locale et envoi
                            if (premierefois) {
                                premierefois = false; //Pour ne pas avoir de doublon d'enregistrement si ce dernier est long.
                                enregistrement = false;
                                endRecord = new Date().getTime();
                                Calendar cal = Calendar.getInstance();
                                //Ajout de la capture dans la base de données
                                if (mesurePodoArrayList.size() == 0) {
                                    mesurePodoArrayList.add(new MesureValue(status, 0, new Date().getTime()));
                                }
                                dbCapture.addCapture(new _Capture_Wear(dbCapture.numberLine(),
                                        cal.get(Calendar.YEAR),
                                        cal.get(Calendar.MONTH),
                                        cal.get(Calendar.DAY_OF_MONTH),
                                        startRecord,
                                        mesureValueArrayList,
                                        mesurePodoArrayList,
                                        endRecord));

                                Thread td = new SendToDataLayerThread("/messageAction", dbCapture.lastRowCapture(), googleClient);

                                updateNotification("MyCardioPad", "C'est fini !");
                                td.start();
                                if (!td.isAlive()) //Permet de ne pas lancer deux fois l'enregistrement vers le mobile
                                    td.start();
                                premierefois = true;
                                compteaRebours = null;
                                Thread.sleep(10000);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        circularProgressBar.setProgress(0);
                                    }
                                });


                            }

                        }
                    }
                }
            } catch (InterruptedException ignored) {
            }
        }
    };

    //Animation
    private Animation animFadeOut;

    String TAG = "Activity_Wear_Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my);
        setAmbientEnabled();    //Mode ambient pour économie de batterie

        initAnimations();

        //Adaptation en fonction de l'écran de la montre
        WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                //Initialisation des différents éléments de l'interface
                initInterface(stub);

                relativeLayout = (RelativeLayout) stub.findViewById(R.id.relativeLayout);

                //Fonction de pause et d'enregistrement présent sur le TextView
                rateTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (enregistrement) {
                            //Toast.makeText(Activity_Wear_Main.this, "Pause !", Toast.LENGTH_SHORT).show();
                            imgStatusAnimation(R.drawable.ic_pause_circle_filled_black_48dp, animFadeOut);
                            timer = compteaRebours.pause();
                            enregistrement = false;
                            status = 1;

                        } else {
                            if(compteaRebours != null){
                                if (!compteaRebours.isFinish()) {
                                    enregistrement = true;
                                    (compteaRebours = new CompteARebours(timer, 1)).start();
                                    //Toast.makeText(Activity_Wear_Main.this, "Reprise !", Toast.LENGTH_SHORT).show();
                                    imgStatusAnimation(R.drawable.ic_play_circle_filled_black_48dp, animFadeOut);
                                    status = 0;
                                    circularProgressBar.setProgress(23);
                                }
                            }
                            rateTv.setVisibility(View.VISIBLE);

                        }
                    }
                });
                //Fonction stop quand appui long sur le TextView
                rateTv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(enregistrement){
                            Intent intent = new Intent(Activity_Wear_Main.this, Activity_Question.class);
                            intent.putExtra("result", "Voulez-vous arrêter?");
                            startActivityForResult(intent, 1);
                        }else {
                            Intent intent = new Intent(Activity_Wear_Main.this, Activity_Question.class);
                            intent.putExtra("result", "Voulez-vous quitter?");
                            startActivityForResult(intent, 2);
                        }

                        return true;
                    }
                });


                uiHandler = new Handler(); //Gestionnaire de mise à jour de l'interface
                calculValeursImagesViews();
                //Mise à jour de l'interface et de la valeur du Rate par défaut et du Rate Min
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imgRate.getLayoutParams();
                        params.width = (imgMaxWidth *currentFreq)/ maxFreq;
                        params.height = (imgMaxHeight *currentFreq)/ maxFreq;
                        imgRate.setLayoutParams(params);
                        //Log.e("imgRate","width :"+((imgMaxWidth *currentFreq)/ maxFreq)+" height : "+((imgMaxHeight *currentFreq)/ maxFreq));
                        params = (LinearLayout.LayoutParams) imgMin.getLayoutParams();
                        params.width = (imgMaxWidth *minFreq)/ maxFreq;
                        params.height = (imgMaxHeight *minFreq)/ maxFreq;
                        imgMin.setLayoutParams(params);
                        //Log.e("imgMin","width :"+((imgMaxWidth *minFreq)/ maxFreq)+" height : "+((imgMaxHeight *minFreq)/ maxFreq));
                    }
                });
            }
        });


        //Instantiation des différents sensors
        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mStepCountSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        //Permet de recevoir des messages de broadcast du processsus d'écoute
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

        // Implémentation de googleClient pour l'api Wearable
        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // Instanciation de la base de données pour enregistrement des résultats
        dbCapture = new MyDBHandler_Capture_Wear(getApplicationContext(), null);

        // Instanciation du vibreur
        v = (Vibrator) Activity_Wear_Main.this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);

        //Communication avec le service
        new Thread(new Runnable() {
            @Override
            public void run() {
                NodeApi.GetLocalNodeResult nodes = Wearable.NodeApi.getLocalNode(googleClient).await();

                Node node = nodes.getNode();
                Log.v(TAG, "Activity Node is : "+node.getId()+ " - " + node.getDisplayName());
                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleClient, node.getId(), "/messageAction", "ActivityLaunch".getBytes()).await();
                if (result.getStatus().isSuccess()) {
                    Log.v(TAG, "Activity Message: {" + "ActivityLaunch"+ "} sent to: " + node.getDisplayName());
                }
                else {
                    // Log an error
                    Log.v(TAG, "ERROR: failed to send Activity Message");
                }
            }
        }).start();


        //Update Thread
        UpdateUI.start();
    }

    /**
     * Mise en place des animations
     */
    private void initAnimations() {
        animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_out);
    }

    /**
     * Lancement Animation
     * @param img la ressource image
     * @param animFadeOut l'animation
     */
    private void imgStatusAnimation(int img, Animation animFadeOut) {
        imgStatus.setImageResource(img);
        imgStatus.setVisibility(View.VISIBLE);
        imgStatus.startAnimation(animFadeOut);
    }

    /**
     * Initialisation de l'interface
     * @param stub la view de la montre
     */
    private void initInterface(WatchViewStub stub) {
        rateTv = (TextView) stub.findViewById(R.id.rate);
        imgMax = (ImageView) stub.findViewById(R.id.img_cercle_red);
        imgMin = (ImageView) stub.findViewById(R.id.img_cercle_yellow);
        imgStatus = (ImageView) stub.findViewById(R.id.imgStatus);
        imgStatus.setVisibility(View.INVISIBLE);
        imgRate = (ImageView) stub.findViewById(R.id.img_cercle_bleu);
        circularProgressBar = (CircularProgressBar) stub.findViewById(R.id.circularProgressBar);

        imgMax.setVisibility(View.VISIBLE);
        imgMin.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(googleClient != null)
            googleClient.connect(); //Connexion au mobile
        mSensorManager.registerListener(this, this.mHeartRateSensor, SensorManager.SENSOR_DELAY_FASTEST); //Activation de la lecture du capteur
        mSensorManager.registerListener(this, this.mStepCountSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }
    @Override
    protected void onRestart() {    //Pas d'utilisation pour le moment
        super.onRestart();
    }
    @Override
    protected void onPause() {   //Pas d'utilisation pour le moment
        super.onPause();
    }

    /**
     * Permet de calculer la valeur de imagesView (valeur initial)
     */
    private void calculValeursImagesViews() {
        ViewTreeObserver observer = imgMax.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imgMax.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                imgMaxWidth = imgMax.getMeasuredWidth();
                imgMaxHeight = imgMax.getMeasuredHeight();
            }
        });
        observer = imgMin.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imgMin.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                imgMinWidth = imgMin.getMeasuredWidth();
                imgMinHeight = imgMin.getMeasuredHeight();
            }
        });
    }
    @Override
    protected void onStop() {   //Pas d'utilisation pour le moment
        super.onStop();

        // Deconnexion du mobile quand la montre entre en veille
        if (null != googleClient && googleClient.isConnected()) {
            googleClient.disconnect(); //Mise en veille
        }
        //Volontairement désactivé pour lecture pendant veille
        //mSensorManager.unregisterListener(this);
        if (compteaRebours == null) {
            updateNotification("MyCardioPad", "Pas d'activité en cours");
        }


    }

    /**
     * Mise à jour de l'interface des imagesView suivant la fréquence de l'utilisateur
     * @param current la fréquence de l'utilisateur
     */
    private void updateUI(final float current){
        if(current>0 && compteaRebours != null){

            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imgRate.getLayoutParams();
                    params.width = (int) ((imgMaxWidth * current) / (maxFreq));
                    params.height = (int) ((imgMaxHeight * current) / (maxFreq));
                    imgRate.setLayoutParams(params);
                }
            });

            updateNotification("MyCardioPad","Il vous reste " + compteaRebours.getTime());
            long time = compteaRebours.getTimeTotal();
            //Affichage du temps restant
            circularProgressBar.setProgress((25*(
                    time -
                            (new Date(compteaRebours.getLong()).getMinutes()*60+(new Date(compteaRebours.getLong()).getSeconds())))
                    /(float) compteaRebours.getTimeTotal()));

            //Modification des couleurs si il ne reste que deux "carrées"
            if(circularProgressBar.getProgress()>23) circularProgressBar.setColor(
                    getResources().getColor(R.color.MockupRedLightText));
            if(circularProgressBar.getProgress()==25) circularProgressBar.setColor(
                    getResources().getColor(R.color.MockupRedText)
            );

        }

    }

    /**
     * Méthodes pour les capteurs
     * Le capteur permet de faire environ 4-5 mesures par seconde, cependant il dépend de la précision.
     * @param sensorEvent Permet de récupérer l'evenement lier au sensor
     */
    @Override
    public void onSensorChanged(final SensorEvent sensorEvent) {

        //Dans le cas du capteur cardiaque
        if (sensorEvent.values[0] > 0 && sensorEvent.sensor.getType() == Sensor.TYPE_HEART_RATE){
            currentFreq = (int) sensorEvent.values[0];
            Thread td = new SendToDataLayerThread("/messageAction", "DIRECTµ"+String.valueOf(currentFreq), googleClient);
            td.start();
            if (!td.isAlive()) //Permet de ne pas lancer deux fois l'enregistrement vers le mobile
                td.start();
        }

        if(enregistrement) { //Quand l'enregistrement est en cours
            if(sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER){ //Enregistrement de la valeur du podomètre suivant l'heure
                mesurePodoArrayList.add(new MesureValue(status,sensorEvent.values[0],new Date().getTime()));
            }else{ //Dans le cas du capteur cardiaque
                if(!compteaRebours.isFinish()){
                    currentFreq = (int) sensorEvent.values[0];
                    if (sensorEvent.values[0] > 0) { //Enregistrement de la mesure avec la date et vibration si besoin
                        mesureValueArrayList.add(new MesureValue(status, sensorEvent.values[0], new Date().getTime()));
                    }else{
                        mesureValueArrayList.add(new MesureValue(status,(float)0, new Date().getTime()));
                    }
                }
            }
        }
    }

    /**
     * Dans le cas ou la précision change, sachant que 0 = Plus de captation
     * @param sensor permet de récupérer le type de sensor
     * @param i permet de connaitre la précision pour le sensor
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.e(sensor.getName(),String.valueOf(i));
    }

    /**
     * METHODES POUR LA COMMUNICATION
     */

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * Class permettant de recevoir les messages par Broadcast inter-processus
     */
    public class MessageReceiver extends BroadcastReceiver {

        /**
         * Quand un message est recu
         * @param context pas encore utilisé
         * @param intent permet d'avoir le contenu du message
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            String [] message = intent.getStringExtra("message").split("µ");
            Log.e("Message",intent.getStringExtra("message"));

            // Affiche le message sur l'UI
            if(!enregistrement){
                if(message[0].equals("G")){ //Lancement d'un enregistrement
                    //Récupération des valeurs min et max
                    maxFreq = Integer.valueOf(message[1]);
                    minFreq = Integer.valueOf(message[2]);
                    //Affichage des images
                    imgMax.setVisibility(View.VISIBLE);
                    imgMin.setVisibility(View.VISIBLE);
                    //Affichage du temps et du champs "Description" du message
                    Toast.makeText(Activity_Wear_Main.this,message[4], Toast.LENGTH_SHORT).show();
                    circularProgressBar.setVisibility(View.VISIBLE);
                    circularProgressBar.setColor(getResources().getColor(R.color.MockupBlueBack));
                    //Lancement de l'enregistrement
                    enregistrement = true;
                    startRecord = new Date().getTime();
                    (compteaRebours = new CompteARebours(Long.parseLong(message[3])*1000*60,1)).start();
                }
                if(message[0].equals("S")){ //Arrêt de l'enregistrement
                    enregistrement = false;
                    Toast.makeText(Activity_Wear_Main.this,message[3], Toast.LENGTH_SHORT).show();
                    imgStatusAnimation(R.drawable.ic_pause_circle_filled_black_48dp, animFadeOut);
                    timer = compteaRebours.pause();
                    enregistrement = false;
                    status = 1;
                    rateTv.setVisibility(View.GONE);

                }
                if(message[0].equals("R")){ //Arrêt de l'enregistrement
                    enregistrement = false;
                    Toast.makeText(Activity_Wear_Main.this,message[3], Toast.LENGTH_SHORT).show();
                    if(compteaRebours != null){
                        if (!compteaRebours.isFinish()) {
                            enregistrement = true;
                            (compteaRebours = new CompteARebours(timer, 1)).start();
                            Toast.makeText(Activity_Wear_Main.this, "Reprise !", Toast.LENGTH_SHORT).show();
                            imgStatusAnimation(R.drawable.ic_play_circle_filled_black_48dp, animFadeOut);
                            status = 0;
                        }
                    }
                    rateTv.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * Pas utilisé ici, car on force l'utilisation d'une unique instance + tâche de fond
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    /**
     * METHODE POUR LE MODE AMBIENT
     * @param ambientDetails object permet de récupérer des paramètre quand on rentre dans le mode ambiant
     */

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);

        //Modification quand l'utilisateur active l'application
        rateTv.getPaint().setAntiAlias(false);
        rateTv.setTextColor(Color.WHITE);
        relativeLayout.setBackgroundColor(Color.BLACK);
        circularProgressBar.setBackgroundColor(Color.BLACK);
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();

        //Modification quand la montre passe en mode Ambient
        rateTv.setTextColor(Color.BLACK);
        rateTv.getPaint().setAntiAlias(true);
        relativeLayout.setBackgroundColor(Color.WHITE);
        circularProgressBar.setBackgroundColor(Color.WHITE);
    }


    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
    }

    /**
     * Pour le moment pas d'utilité
     * @param intent permet de récuperer les paramètres de l'appel
     */
    @Override
    public void onNewIntent(Intent intent){
        super.onNewIntent(intent);
    }

    /**
     * Mise à jour des notifications pour la montre
     * @param title le titre
     * @param contentText le contenu
     */
    public void updateNotification(String title, String contentText){

        //Préparation d'un intent pour revenir vers cette écran via la notification
        Intent intent = new Intent();
        intent.setClass(this, Activity_Wear_Main.class);

        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if(notificationBuilder == null){
            notificationBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(title)
                            .setContentText(contentText)
                            .setContentIntent(viewPendingIntent);
        }else{
            notificationBuilder.setContentText(contentText).setContentIntent(viewPendingIntent).setContentTitle(title);
        }



        notificationManager  = NotificationManagerCompat.from(this);

        //Lancement de la notification
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    /**
     * Lancement d'une activité pour arrêter l'enregistrement.
     * @param requestCode code de requête
     * @param resultCode code de resultat pour la requête
     * @param data données supplémentaires
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((resultCode == Activity.RESULT_OK) && (resultCode == 1)) {
            if(compteaRebours!=null){
                compteaRebours.cancel();
                status = 1;
            }
            Toast.makeText(getApplicationContext(),"Exercice fini !",Toast.LENGTH_SHORT).show();
            updateNotification("MyCardioPad", "Pas d'exercice en cours");
        }else{
            mSensorManager.unregisterListener(this); //Désactivation du capteur
            finish();
        }
    }
}