package com.mycardiopad.g1.mycardiopad.database;

import java.util.Date;

/**
 * Réalisé par nicolassalleron le 12/02/16. <br/>
 * Mise en place du compte <br/>
 */
@SuppressWarnings("unused")
public class _Compte_Wear {

    private long _id;
    private String _email, _mot_de_passe,_nom, _prenom, _path_photo;
    private int _sexe, _taille, _poids, _compte_limite, _total_session, _total_pas;
    private Date _date_de_naissance, _date_inscription;

    public _Compte_Wear() {

    }

    /**
     * Constructeur de l'objet
     * @param id identifant de la capture
     */

    public _Compte_Wear(long id) {
        this._id = id;

    }


    public _Compte_Wear(long _id, String _email, String _mot_de_passe, String _nom, String _prenom,
                        int _sexe, Date _date_de_naissance, int _taille, int _poids,
                        Date _date_inscription, int _compte_limite, int _total_session,
                        int _total_pas, String _path_photo ) {
        this._id = _id;
        this._email = _email;
        this._mot_de_passe = _mot_de_passe;
        this._nom = _nom;
        this._prenom = _prenom;
        this._sexe = _sexe;
        this._date_de_naissance = _date_de_naissance;
        this._taille = _taille;
        this._poids = _poids;
        this._date_inscription = _date_inscription;
        this._compte_limite = _compte_limite;
        this._total_session = _total_session;
        this._total_pas = _total_pas;
        this._path_photo = _path_photo;
    }


    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String get_email() {
        return _email;
    }

    public void set_email(String _email) {
        this._email = _email;
    }

    public String get_mot_de_passe() {
        return _mot_de_passe;
    }

    public void set_mot_de_passe(String _mot_de_passe) {
        this._mot_de_passe = _mot_de_passe;
    }

    public String get_nom() {
        return _nom;
    }

    public void set_nom(String _nom) {
        this._nom = _nom;
    }

    public String get_prenom() {
        return _prenom;
    }

    public void set_prenom(String _prenom) {
        this._prenom = _prenom;
    }

    public int get_sexe() {
        return _sexe;
    }

    public void set_sexe(int _sexe) {
        this._sexe = _sexe;
    }

    public Date get_date_de_naissance() {
        return _date_de_naissance;
    }

    public void set_date_de_naissance(Date _date_de_naissance) {
        this._date_de_naissance = _date_de_naissance;

    }

    public int get_taille() {
        return _taille;
    }

    public void set_taille(int _taille) {
        this._taille = _taille;
    }

    public int get_poids() {
        return _poids;
    }

    public void set_poids(int _poids) {
        this._poids = _poids;
    }

    public Date get_date_inscription() {
        return _date_inscription;
    }

    public void set_date_inscription(Date _date_inscription) {
        this._date_inscription = _date_inscription;
    }

    public int get_compte_limite() {
        return _compte_limite;
    }

    public void set_compte_limite(int _compte_limite) {
        this._compte_limite = _compte_limite;
    }

    public int get_total_session() {
        return _total_session;
    }

    public void set_total_session(int _total_session) {
        this._total_session = _total_session;
    }

    public int get_total_pas() {
        return _total_pas;
    }

    public void set_total_pas(int _total_pas) {
        this._total_pas = _total_pas;
    }

    public String get_path_photo() {
        return _path_photo;
    }

    public void set_path_photo(String _path_photo) {
        this._path_photo = _path_photo;
    }
}