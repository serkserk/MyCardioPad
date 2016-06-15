package com.mycardiopad.g1.mycardiopad.database;

/**
 * Réalisé par nicolassalleron le 12/02/16. <br/>
 * Base de données  <br/>
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDBHandler_Compte_Wear extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "compteDB2.db";
    private static final String TABLE_COMPTE = "_Compte_Wear";

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
    public MyDBHandler_Compte_Wear(Context context,
                                   SQLiteDatabase.CursorFactory factory) {
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
                + COLUMN_TOTAL_PAS + " INTEGER)";
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
        long numRows = DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + TABLE_COMPTE, null);
        Log.e("Nombre de ligne : ", String.valueOf(numRows));
        return numRows;
    }

    /**
     * Ajoute un compte à la base de donnée
     * @param c un comte à ajouter
     */
    public void addCompte(_Compte_Wear c) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, c.get_id());
        values.put(COLUMN_EMAIL,c.get_email());
        values.put(COLUMN_MOT_DE_PASSE, c.get_mot_de_passe());
        values.put(COLUMN_NOM, c.get_nom());
        values.put(COLUMN_PRENOM, c.get_prenom());
        values.put(COLUMN_SEXE,c.get_sexe());
        values.put(COLUMN_TAILLE, c.get_taille());
        values.put(COLUMN_POIDS, c.get_poids());
        values.put(COLUMN_COMPTE_LIMITE,c.get_compte_limite());
        values.put(COLUMN_TOTAL_SESSION, c.get_total_session());
        values.put(COLUMN_TOTAL_PAS, c.get_total_pas());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_COMPTE, null, values);
        db.close();
        Log.e("addCapture", "Ajout du compte : OK");
    }


    /**
     * Récupère le compte précisé en paramètre
     * @param ligne La ligne à récupérer dans la base de données
     */
    public _Compte_Wear getCompte(int ligne) {

        String query = "Select * FROM " + TABLE_COMPTE ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        _Compte_Wear compte = new _Compte_Wear();
        if(cursor.moveToPosition(ligne)) {
            compte.set_id(Long.parseLong(cursor.getString(0)));
            compte.set_email(cursor.getString(1));
            compte.set_mot_de_passe(cursor.getString(2));
            compte.set_nom(cursor.getString(3));
            compte.set_prenom(cursor.getString(4));
            compte.set_sexe(Integer.parseInt(cursor.getString(5)));
            compte.set_taille(Integer.parseInt(cursor.getString(7)));
            compte.set_poids(Integer.parseInt(cursor.getString(8)));
            compte.set_compte_limite(Integer.parseInt(cursor.getString(10)));
            compte.set_total_session(Integer.parseInt(cursor.getString(11)));
            compte.set_total_pas(Integer.parseInt(cursor.getString(12)));
        }
        cursor.close();
        db.close();
        return compte;
    }

}