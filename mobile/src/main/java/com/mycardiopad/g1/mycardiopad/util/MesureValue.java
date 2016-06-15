package com.mycardiopad.g1.mycardiopad.util;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Réalisé par nicolassalleron le 20/01/16. <br/>
 * Mesure Value du capture    <br/>
 *      status le status de l'activité     <br/>
 *                0 = En cours   <br/>
 *                1 = En pause / Arrêt   <br/>
 *      value la fréquence cardiaque de l'activité     <br/>
 *      Date la date de l'activité     <br/>
 */
public class MesureValue implements Parcelable, Serializable {
    float value;
    long date;
    int status;


    @SuppressWarnings("unused")
    public MesureValue(int status, float value, long Date){
        this.status = status;
        this.value = value;
        this.date = Date;
    }

    //Méthodes crée automatiquement, permettant de générer une Parcelle (description dans Javadoc).
    public static final Creator<MesureValue> CREATOR = new Creator<MesureValue>() {
        @Override
        public MesureValue createFromParcel(Parcel in) {
            return new MesureValue(in);
        }

        @Override
        public MesureValue[] newArray(int size) {
            return new MesureValue[size];
        }
    };

    protected MesureValue(Parcel in) {
        status = in.readInt();
        value = in.readFloat();
        date = in.readLong();
    }

    //Get
    public int getStatus(){return status;}
    public float getValue() {
        return value;
    }
    public long getDate() {
        return date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(status);
        dest.writeFloat(value);
        dest.writeLong(date);
    }

    @Override
    public String toString() {
        return status+"µ"+ value +"µ"+date+"µ";
    }
}
