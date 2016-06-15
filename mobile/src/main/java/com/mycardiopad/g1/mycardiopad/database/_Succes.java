package com.mycardiopad.g1.mycardiopad.database;

import java.util.Date;

/**
 * Réalisé par kevin le 19/03/2016.
 * Objet permettant la récupération ou l'inscription d'information du compte depuis la base de données.  <br/>
 */
public class _Succes {

    private long _id;
    private String _nom, _description;
    private Date _date_obtention;
    private int _obtenu;

    public _Succes() {

    }


    //Get
    public long get_id() {
        return _id;
    }
    public String get_nom() {
        return _nom;
    }
    public String get_description() {
        return _description;
    }
    public Date get_date_obtention() {
        return _date_obtention;
    }
    public int get_obtenu() {
        return _obtenu;
    }

    //Set
    public void set_id(long _id) {
        this._id = _id;
    }
    public void set_nom(String _nom) {
        this._nom = _nom;
    }
    public void set_description(String _description) {
        this._description = _description;
    }
    public void set_date_obtention(Date _date_obtention) {
        this._date_obtention = _date_obtention;
    }
    public void set_obtenu(int _obtenu) {
        this._obtenu = _obtenu;
    }
}