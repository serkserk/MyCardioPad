package com.mycardiopad.g1.mycardiopad.util;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.widget.TextView;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Réalisé par nicolassalleron le 22/01/16. <br/>
 * Permet de mettre en place un compte à rebours pour l'activité <br/>
 */
public class CompteARebours extends CountDownTimer {

    TextView tv;
    boolean fin, alive = false;
    long millisrestant;
    long timetotal;


    public CompteARebours(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        timetotal = millisInFuture;
        alive = true;
    }


    @SuppressLint("DefaultLocale")
    @Override
    /**
     * Capture des millisecondes restante
     */
    public void onTick(final long millisUntilFinished) {
        millisrestant = millisUntilFinished;
        if(!(tv ==null)){
            tv.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
            ));
        }
    }

    /**
     * Récupère le temps total en seconde
     * @return le temps en seconde
     */
    public long getTimeTotal() {
        return new Date(timetotal).getMinutes()*60;
    }

    /**
     * Renvoi le temps en %d min, %d sec
     * @return une chaine
     */
    @SuppressLint("DefaultLocale")
    public String getTime(){
        return String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(millisrestant),
                TimeUnit.MILLISECONDS.toSeconds(millisrestant) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisrestant)));
    }

    /**
     * Retourne le temps (millisecondes)
     * @return un long
     */
    @SuppressWarnings("unused")
    public long getLong(){
        return millisrestant;
    }

    /**
     * Quand le compte à rebours est fini
     */
    @Override
    public void onFinish() {
        fin= true;
    }

    /**
     * Vérification si le compte à rebours est fini
     * @return un boolean
     */
    public boolean isFinish(){
        return fin;
    }

    /**
     * Mise en place de la pause
     * @return le temps restant en milliseconde;
     */
    public long pause(){
        super.cancel();
        return millisrestant;
    }

}


