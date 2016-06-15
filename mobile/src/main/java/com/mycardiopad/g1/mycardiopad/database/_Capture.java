package com.mycardiopad.g1.mycardiopad.database;

import com.mycardiopad.g1.mycardiopad.util.MesureValue;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Réalisé par nicolassalleron le 25/05/15.  <br/>
 * Objet permettant la récupération ou l'inscription d'information du compte depuis la base de données.  <br/>
 */
@SuppressWarnings("unused")
public class _Capture {
    private long _id;
    private String _cap, _podo;
    int year,month,day;
    long startRecord, endRecord;

    public _Capture() {

    }

    /**
     * Constructeur de l'objet
     * @param id identifant de la capture
     */

    public _Capture(long id) {
        this._id = id;

    }

    public _Capture(long id, int YEAR, int MONTH, int DAY, long startRecord, ArrayList<MesureValue> CaptureFreq, ArrayList<MesureValue> CapturePodo, long endRecord) {
        this._id = id;
        this.year = YEAR;
        this.month = MONTH;
        this.day = DAY;
        this.startRecord = startRecord;
        this._cap = getString(CaptureFreq);
        this._podo = getString(CapturePodo);
        this.endRecord = endRecord;
    }

    @Override
    public String toString(){
        return String.valueOf(this._id) +
                "µ" +
                this.year +
                "µ" +
                this.month +
                "µ" +
                this.day +
                "µ" +
                this.startRecord +
                "$" +
                this._cap +
                "$" +
                this._podo +
                "$" +
                this.endRecord +
                "µ";
    }

    public String getString(ArrayList<MesureValue> rec){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);

        //Conversion des éléments en String
        for (MesureValue element : rec) {
            try {
                out.write(String.valueOf(element.getStatus()).getBytes());
                out.write("µ".getBytes());
                out.write(String.valueOf(element.getValue()).getBytes());
                out.write("µ".getBytes());
                out.write(String.valueOf(element.getDate()).getBytes());
                out.write("µ".getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String result = baos.toString();

        //Fermeture
        try {
            baos.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    //Set
    public void set_id(long id){
        this._id = id;
    }
    public void setYear(int year) {
        this.year = year;
    }
    public void setMonth(int month) {
        this.month = month;
    }
    public void setDay(int day) {
        this.day = day;
    }
    public void setStartRecord(long startRecord) {
        this.startRecord = startRecord;
    }
    public void set_cap(String capture){
        this._cap = capture;
    }
    public void set_podo(String _podo) {
        this._podo = _podo;
    }
    public void setEndRecord(long endRecord) {
        this.endRecord = endRecord;
    }

    //Get
    public long get_id(){
        return this._id;
    }
    public int getYear() {
        return this.year;
    }
    public int getMonth() {
        return this.month;
    }
    public int getDay() {
        return this.day;
    }
    public long getStartRecord() {
        return this.startRecord;
    }
    public String get_cap(){
        return this._cap;
    }
    public String get_podo() {
        return _podo;
    }
    public long getEndRecord() {
        return this.endRecord;
    }


}

