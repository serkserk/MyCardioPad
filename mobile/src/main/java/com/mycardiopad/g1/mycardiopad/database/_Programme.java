package com.mycardiopad.g1.mycardiopad.database;

/**
 * Réalisé par Vishnupriya le 25/02/16.
 * Objet permettant la récupération ou l'inscription d'information du compte depuis la base de données.  <br/>
 */
public class _Programme {


    private String _ProgrammeJours,_ExerciceJour;
    private long _DateDebutPrescription, _id;
    private int _maxFreq, _minFreq;

    public _Programme() {

    }

    //Get
    public int get_maxFreq() {
        return _maxFreq;
    }
    public int get_minFreq() {
        return _minFreq;
    }
    public String get_ProgrammeJours() {
        return _ProgrammeJours;
    }
    public String get_ExerciceJour() {

        return _ExerciceJour;
    }
    public long get_id() {
        return _id;
    }
    public long get_DateDebutPrescription() {
        return _DateDebutPrescription;
    }
    public void set_ProgrammeJours(String _ProgrammeJours) {
        this._ProgrammeJours = _ProgrammeJours;
    }

    //Set
    public void set_id(long _id) {
        this._id = _id;
    }
    public void set_ExerciceJour(String _ExerciceJour) {
        this._ExerciceJour = _ExerciceJour;
    }
    public void set_DateDebutPrescription(long _DateDebutPrescription) {
        this._DateDebutPrescription = _DateDebutPrescription;
    }
    public void set_maxFreq(int _maxFreq) {
        this._maxFreq = _maxFreq;
    }
    public void set_minFreq(int _minFreq) {
        this._minFreq = _minFreq;
    }

}




