package com.mycardiopad.g1.mycardiopad.database;

/**
 * Réalisé par nicolassalleron le 12/02/16. <br/>
 * Base de données Compte <br/>
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyDBHandler_Compte extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "compteDB2.db";
    private static final String TABLE_COMPTE = "_Compte";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_EMAIL = "_email";
    public static final String COLUMN_MOT_DE_PASSE = "_mot_de_passe";
    public static final String COLUMN_NOM = "_nom";
    public static final String COLUMN_PRENOM = "_prenom";
    public static final String COLUMN_SEXE = "_sexe";
    public static final String COLUMN_DATE_DE_NAISSANCE = "_date_de_naissance";
    public static final String COLUMN_TAILLE = "_taille";
    public static final String COLUMN_POIDS = "_poids";
    public static final String COLUMN_DATE_INSCRIPTION = "_date_inscription";
    public static final String COLUMN_COMPTE_LIMITE = "_compte_limite";
    public static final String COLUMN_TOTAL_SESSION = "_total_session";
    public static final String COLUMN_TOTAL_PAS = "_total_pas";
    public static final String COLUMN_PATH = "_path_photo";


    @SuppressWarnings("unused")
    public MyDBHandler_Compte(Context context, String name,
                              SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " +
                TABLE_COMPTE + "("
                + COLUMN_ID + " LONG PRIMARY KEY,"
                + COLUMN_EMAIL + " STRING,"
                + COLUMN_MOT_DE_PASSE + " STRING,"
                + COLUMN_NOM + " STRING,"
                + COLUMN_PRENOM + " STRING,"
                + COLUMN_SEXE + " INTEGER,"
                + COLUMN_DATE_DE_NAISSANCE + " DATE,"
                + COLUMN_TAILLE + " INTEGER,"
                + COLUMN_POIDS + " INTEGER,"
                + COLUMN_DATE_INSCRIPTION + " DATE,"
                + COLUMN_COMPTE_LIMITE + " INTEGER,"
                + COLUMN_TOTAL_SESSION + " INTEGER,"
                + COLUMN_TOTAL_PAS + " INTEGER,"
                + COLUMN_PATH + " STRING)";
        db.execSQL(CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPTE);
        onCreate(db);
    }

    /**
     *
     * Retourne le nombre de ligne de la base de données
     */
    public long numberLine() {
        SQLiteDatabase db = this.getWritableDatabase();
        //Log.e("Nombre de ligne : ", String.valueOf(numRows));
        return DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + TABLE_COMPTE, null);
    }

    /**
     * Ajoute un compte à la base de donnée
     * @param c un comte à ajouter
     */
    public void addCompte(_Compte c) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, c.get_id());
        values.put(COLUMN_EMAIL,c.get_email());
        values.put(COLUMN_MOT_DE_PASSE, c.get_mot_de_passe());
        values.put(COLUMN_NOM, c.get_nom());
        values.put(COLUMN_PRENOM, c.get_prenom());
        values.put(COLUMN_SEXE,c.get_sexe());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            values.put(COLUMN_DATE_DE_NAISSANCE, sdf.format(c.get_date_de_naissance()));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        values.put(COLUMN_TAILLE, c.get_taille());
        values.put(COLUMN_POIDS, c.get_poids());
        try {
            values.put(COLUMN_DATE_INSCRIPTION, sdf.format(c.get_date_inscription()));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        values.put(COLUMN_COMPTE_LIMITE,c.get_compte_limite());
        values.put(COLUMN_TOTAL_SESSION, c.get_total_session());
        values.put(COLUMN_TOTAL_PAS, c.get_total_pas());
        values.put(COLUMN_PATH,c.get_path_photo());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_COMPTE, null, values);
        db.close();
        Log.e("addCapture", "Ajout du compte : OK");
    }

    /**
     * Récupère le dernier compte
     */
    public _Compte lastRowCompte() {

        String selectQuery= "SELECT * FROM " + TABLE_COMPTE +" ORDER BY "+COLUMN_ID+" DESC LIMIT 1";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        _Compte compte = new _Compte();
        if(cursor.moveToFirst()){
            compte.set_id(Long.parseLong(cursor.getString(0)));
            compte.set_email(cursor.getString(1));
            compte.set_mot_de_passe(cursor.getString(2));
            compte.set_nom(cursor.getString(3));
            compte.set_prenom(cursor.getString(4));
            compte.set_sexe(Integer.parseInt(cursor.getString(5)));
            try {
                Date simpleDate = new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(6));
                compte.set_date_de_naissance(simpleDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            compte.set_taille(Integer.parseInt(cursor.getString(7)));
            compte.set_poids(Integer.parseInt(cursor.getString(8)));
            try {
                Date simpleDate = new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(9));
                compte.set_date_inscription(simpleDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            compte.set_compte_limite(Integer.parseInt(cursor.getString(10)));
            compte.set_total_session(Integer.parseInt(cursor.getString(11)));
            compte.set_total_pas(Integer.parseInt(cursor.getString(12)));
            compte.set_path_photo(cursor.getString(13));
        }
        cursor.close();
        db.close();
        Log.e("lastRowCompte", "last row = " + compte.toString());
        return compte;
    }


    /**
     * Récupère le compte précisé en paramètre
     * @param ligne La ligne à récupérer dans la base de données
     */
    public _Compte getCompte(int ligne) {

        String query = "Select * FROM " + TABLE_COMPTE ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        _Compte compte = new _Compte();
        if(cursor.moveToPosition(ligne)) {
            compte.set_id(Long.parseLong(cursor.getString(0)));
            compte.set_email(cursor.getString(1));
            compte.set_mot_de_passe(cursor.getString(2));
            compte.set_nom(cursor.getString(3));
            compte.set_prenom(cursor.getString(4));
            compte.set_sexe(Integer.parseInt(cursor.getString(5)));
            try {
                Date simpleDate = new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(6));
                compte.set_date_de_naissance(simpleDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            compte.set_taille(Integer.parseInt(cursor.getString(7)));
            compte.set_poids(Integer.parseInt(cursor.getString(8)));
            try {
                Date simpleDate = new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(9));
                compte.set_date_inscription(simpleDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            compte.set_compte_limite(Integer.parseInt(cursor.getString(10)));
            compte.set_total_session(Integer.parseInt(cursor.getString(11)));
            compte.set_total_pas(Integer.parseInt(cursor.getString(12)));
            compte.set_path_photo(cursor.getString(13));
        }
        cursor.close();
        db.close();
        return compte;
    }

    /**
     * Permet de supprimer la base de données
     */
    public void deleteTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPTE);
        onCreate(db);
    }

}