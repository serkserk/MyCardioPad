package com.mycardiopad.g1.mycardiopad.database;

/**
 * Réalisé par nicolassalleron le 25/05/15. <br/>
 * Base de données  <br/>
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDBHandler_Programme_Wear extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "programmeDB.db";
    private static final String TABLE_CAPTURE = "_Programme_Wear";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PROGRAMME = "_programme";
    public static final String COLUMN_EXERCICE_JOURS= "_exercice";
    public static final String COLUMN_DATE_DEBUT_PRESCRIPTION = "_date_debut_prescription";
    public static final String COLUMN_MIN_FREQ = "_min_freq";
    public static final String COLUMN_MAX_FREQ = "_max_freq";
    public MyDBHandler_Programme_Wear(Context context,
                                      SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " +
                TABLE_CAPTURE + "("
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

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAPTURE);
        onCreate(db);
    }
    /**
     * Retourne le nombre de ligne de la base de données
     */
    public long numberLine() {
        SQLiteDatabase db = this.getWritableDatabase();
        return DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + TABLE_CAPTURE, null);
    }
    /**
     * Ajoute un programme à la base de données
     * @param c un programme
     */
    public void addProgramme(_Programme_Wear c) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, c.get_id());
        values.put(COLUMN_PROGRAMME ,c.get_ProgrammeJours());
        values.put(COLUMN_EXERCICE_JOURS,c.get_ExerciceJour());
        values.put(COLUMN_DATE_DEBUT_PRESCRIPTION,c.get_DateDebutPrescription());
        values.put(COLUMN_MIN_FREQ,c.get_minFreq());
        values.put(COLUMN_MAX_FREQ,c.get_maxFreq());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_CAPTURE, null, values);
        db.close();
        Log.e("addCapture", "Ajout de la capture : OK");
    }
    /**
     * Récupère la dernière ligne du programme
     */
    public _Programme_Wear lastRowProgramme() {

        String selectQuery= "SELECT * FROM " + TABLE_CAPTURE +" ORDER BY "+COLUMN_ID+" DESC LIMIT 1";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        _Programme_Wear programme = new _Programme_Wear();
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
        return programme;
    }

}