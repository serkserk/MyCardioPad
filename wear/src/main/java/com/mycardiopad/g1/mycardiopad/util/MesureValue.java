package com.mycardiopad.g1.mycardiopad.util;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Réalisé par nicolassalleron le 20/01/16. <br/>
 *
 */
public class MesureValue implements Parcelable, Serializable {
    float value;
    long date;
    int status;
    /**
     * MesureValue du capture
     * @param status le status de l'activité
     *                0 = En cours
     *                1 = En pause / Arrêt
     * @param value la fréquence cardiaque ou step de l'activité
     * @param Date la date de l'activité
     */
    public MesureValue(int status, float value, long Date){
        this.status = status;
        this.value = value;
        this.date = Date;
    }

    protected MesureValue(Parcel in) {
        status = in.readInt();
        value = in.readFloat();
        date = in.readLong();
    }


    //Méthodes crée automatique, permettant de générer une Parcelle.
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
