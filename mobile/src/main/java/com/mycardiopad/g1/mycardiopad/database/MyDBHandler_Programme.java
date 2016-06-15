package com.mycardiopad.g1.mycardiopad.database;

/**
 * Réalisé par nicolassalleron le 25/05/15. <br/>
 * Base de données du programme. <br/>
 *
 * Exemple de mise en place des jours d'exercices <br/>
 *
 *         1 = Jour de Sport, 0 = Jour de repos <br/>
 *
 *  programme.set_ExerciceJour("0µ" + //Lundi   <br/>
 *  "1µ" +  //Mardi <br/>
 *  "1µ" +  //Mercredi  <br/>
 *  "0µ" +  //Jeudi <br/>
 *  "1µ" +  //Vendreid  <br/>
 *  "0µ" +  //Samedi    <br/>
 *  "1"); //    <br/>
 *
 *  programme.set_DateDebutPrescription(new Date().getTime());<br/>
 *  programme.set_id(db.numberLine()); <br/>
 *
 * Exemple de mise en place des jours d'activités   <br/>
 *      Séparateur des jours = /    <br/>
 *      Séparateur entre données µ  <br/>
 *          3 champs pour une activité  <br/>
 *            -  1er Token = ordre de l'activité (commence à 0) <br/>
 *            -  2eme Token = ID unique de l'activité   <br/>
 *            -  3eme Token = Temps pour cette activité.    <br/>
 *
 *
 *  /!\ Un jour de sport possède forcément une activitée <br/>
 *
 *  programme.set_ProgrammeJours("/" + //Lundi
 *      "0µCourseµ10µ1µMarcheµ20µ2µVéloµ30µ/" + //Mardi
 *      "0µVéloµ10µ1µMarcheµ30µ2µCourseµ10µ/" + //Mercredi
 *      "/" +  //Jeudi
 *      "0µCourseµ30µ1µMarcheµ10µ2µVéloµ20µ/" +  //Vendredi
 *      "/" +   //Samedi
 *      "0µCourseµ30µ1µMarcheµ10µ2µVéloµ20µ/"); //Dimanche
 *
 *  /!\ Le nombre d'activité par jours n'est pas limité.
 *
 *      programme.set_minFreq(70);
 *      programme.set_maxFreq(180);
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDBHandler_Programme extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "programmeDB.db";
    private static final String TABLE_PROGRAMME = "_Programme";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PROGRAMME = "_programme";
    public static final String COLUMN_EXERCICE_JOURS= "_exercice";
    public static final String COLUMN_DATE_DEBUT_PRESCRIPTION = "_date_debut_prescription";
    public static final String COLUMN_MIN_FREQ = "_min_freq";
    public static final String COLUMN_MAX_FREQ = "_max_freq";

    @SuppressWarnings("unused")
    public MyDBHandler_Programme(Context context, String name,
                                 SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " +
                TABLE_PROGRAMME + "("
                + COLUMN_ID + " LONG PRIMARY KEY,"
                + COLUMN_PROGRAMME + " TEXT,"
                + COLUMN_EXERCICE_JOURS + " TEXT,"
                + COLUMN_DATE_DEBUT_PRESCRIPTION + " LONG,"
                + COLUMN_MIN_FREQ + " INTEGER,"
                + COLUMN_MAX_FREQ + " INTEGER"+")";
        db.execSQL(CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROGRAMME);
        onCreate(db);
    }


    /**
     * Retourne le nombre de ligne de la base de données
     */
    public long numberLine() {
        SQLiteDatabase db = this.getWritableDatabase();
        //Log.e("Nombre de ligne : ", String.valueOf(numRows));
        return DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + TABLE_PROGRAMME, null);
    }

    /**
     * Ajoute un programme à la base de données
     * @param c un programme
     */

    public void addProgramme(_Programme c) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, c.get_id());
        values.put(COLUMN_PROGRAMME ,c.get_ProgrammeJours());
        values.put(COLUMN_EXERCICE_JOURS,c.get_ExerciceJour());
        values.put(COLUMN_DATE_DEBUT_PRESCRIPTION,c.get_DateDebutPrescription());
        values.put(COLUMN_MIN_FREQ,c.get_minFreq());
        values.put(COLUMN_MAX_FREQ,c.get_maxFreq());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_PROGRAMME, null, values);
        db.close();
        Log.e("addCapture", "Ajout de la capture : OK");
    }

    /**
     * Permet de mettre à jour programme
     * @param c le programme
     */
    public void updateProgramme(_Programme c) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, c.get_id());
        values.put(COLUMN_PROGRAMME ,c.get_ProgrammeJours());
        values.put(COLUMN_EXERCICE_JOURS,c.get_ExerciceJour());
        values.put(COLUMN_DATE_DEBUT_PRESCRIPTION,c.get_DateDebutPrescription());
        values.put(COLUMN_MIN_FREQ,c.get_minFreq());
        values.put(COLUMN_MAX_FREQ,c.get_maxFreq());
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_PROGRAMME, values, "1=1", null); // Il n'y aura qu'une ligne, on les met donc "toutes" à jour
        db.close();
        Log.e("addCapture", "Ajout de la capture : OK");
    }
    /**
     * Récupère la dernière ligne du programme
     */
    public _Programme lastRowProgramme() {

        String selectQuery= "SELECT * FROM " + TABLE_PROGRAMME +" ORDER BY "+COLUMN_ID+" DESC LIMIT 1";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        _Programme programme = new _Programme();
        if(cursor.moveToFirst()){
            programme.set_id(Long.parseLong(cursor.getString(0)));
            programme.set_ProgrammeJours(cursor.getString(1));
            programme.set_ExerciceJour(cursor.getString(2));
            programme.set_DateDebutPrescription(Long.parseLong(cursor.getString(3)));
            programme.set_minFreq(Integer.parseInt(cursor.getString(4)));
            programme.set_maxFreq(Integer.parseInt(cursor.getString(5)));
        }
        cursor.close();
        db.close();
        //Log.e("lastRowCapture", "last row = " + programme.toString());
        return programme;
    }

    /**
     * Permet de supprimer la base de données
     */

    public void deleteTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROGRAMME);
        onCreate(db);
    }



}