package com.mycardiopad.g1.mycardiopad.database;

/**
 * Réalisé par Vishnupriya le 25/02/16.  <br/>
 * Mise en place du programme de l'utilsiateur sur la montre <br/>
 */
public class _Programme_Wear {


    private String _ProgrammeJours,_ExerciceJour;
    private long _DateDebutPrescription, _id;
    private int _maxFreq, _minFreq;

    @SuppressWarnings("unused")
    public _Programme_Wear(long id, String _ProgrammeJours, String _ExerciceJour, long dateDebutPrescription, int maxFreq, int minFreq) {
        this._id = id;
        this._ProgrammeJours = _ProgrammeJours;
        this._ExerciceJour = _ExerciceJour;
        this._DateDebutPrescription = dateDebutPrescription;
        this._maxFreq = maxFreq;
        this._minFreq = minFreq;
    }
    public _Programme_Wear() {

    }


    //Set
    public void set_maxFreq(int _maxFreq) {
        this._maxFreq = _maxFreq;
    }
    public void set_minFreq(int _minFreq) {
        this._minFreq = _minFreq;
    }
    public void set_ProgrammeJours(String _ProgrammeJours) {
        this._ProgrammeJours = _ProgrammeJours;
    }
    public void set_ExerciceJour(String _ExerciceJour) {
        this._ExerciceJour = _ExerciceJour;
    }
    public void set_DateDebutPrescription(long _DateDebutPrescription) {
        this._DateDebutPrescription = _DateDebutPrescription;
    }
    public void set_id(long _id) {
        this._id = _id;
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
    public long get_DateDebutPrescription() {
        return _DateDebutPrescription;
    }
    public long get_id() {
        return _id;
    }
}




