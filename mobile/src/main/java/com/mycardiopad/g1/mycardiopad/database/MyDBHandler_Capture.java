package com.mycardiopad.g1.mycardiopad.database;

/**
 * Réalisé par nicolassalleron le 25/05/15. <br/>
 * Base de données de Capture <br/>
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDBHandler_Capture extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "captureDataBase.db";
    private static final String TABLE_CAPTURE = "_CaptureDataBase";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_YEAR = "_year";
    public static final String COLUMN_MONTH = "_month";
    public static final String COLUMN_DAY = "_day";
    public static final String COLUMN_STARTDATE = "_startDate";
    public static final String COLUMN_CAPTURE_FREQ = "_CaptureString";
    public static final String COLUMN_CAPTURE_PODO = "_CapturePodoString";
    public static final String COLUMN_ENDDATE = "_endDate";

    @SuppressWarnings("unused")
    public MyDBHandler_Capture(Context context, String name,
                               SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " +
                TABLE_CAPTURE + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_YEAR + " INTEGER,"
                + COLUMN_MONTH + " INTEGER,"
                + COLUMN_DAY + " INTEGER,"
                + COLUMN_STARTDATE + " INTEGER,"
                + COLUMN_CAPTURE_FREQ + " TEXT,"
                + COLUMN_CAPTURE_PODO + " TEXT,"
                + COLUMN_ENDDATE + " INTEGER"+")";
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
        //Log.e("Nombre de ligne : ", String.valueOf(numRows));
        return DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + TABLE_CAPTURE, null);
    }


    /**
     * Ajoute une capture à la base de donnée
     * @param c une capture d'un enregistrement
     */
    public void addCapture(_Capture c) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, c.get_id());
        values.put(COLUMN_YEAR,c.getYear());
        values.put(COLUMN_MONTH,c.getMonth());
        values.put(COLUMN_DAY,c.getDay());
        values.put(COLUMN_STARTDATE,c.getStartRecord());
        values.put(COLUMN_CAPTURE_FREQ, c.get_cap());
        values.put(COLUMN_CAPTURE_PODO,c.get_podo());
        values.put(COLUMN_ENDDATE,c.getEndRecord());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_CAPTURE, null, values);
        db.close();
        Log.e("addCapture", "Ajout de la capture : OK");
    }

    /**
     * Récupère la dernière ligne de l'enregistrement
     */
    public _Capture lastRowCapture() {

        String selectQuery= "SELECT * FROM " + TABLE_CAPTURE +" ORDER BY "+COLUMN_ID+" DESC LIMIT 1";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        _Capture capture = new _Capture();
        if(cursor.moveToFirst()){
            capture.set_id(Long.parseLong(cursor.getString(0)));
            capture.setYear(Integer.parseInt(cursor.getString(1)));
            capture.setMonth(Integer.parseInt(cursor.getString(2)));
            capture.setDay(Integer.parseInt(cursor.getString(3)));
            capture.setStartRecord(Long.parseLong(cursor.getString(4)));
            capture.set_cap(cursor.getString(5));
            capture.set_podo(cursor.getString(6));
            capture.setEndRecord(Long.parseLong(cursor.getString(7)));
        }
        cursor.close();
        db.close();
        //Log.e("lastRowCapture", "last row = " + capture.toString());
        return capture;
    }


    /**
     * Récupère la mesure précisée en paramètre
     * @param ligne La ligne à récupérer dans la base de données
     */
    public _Capture getMesure(int ligne) {


        String query = "Select * FROM " + TABLE_CAPTURE ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        _Capture capture = new _Capture();
        if(cursor.moveToPosition(ligne)){
            //Log.e("String Cursur ID", cursor.getString(0));
            capture.set_id(Long.parseLong(cursor.getString(0)));
            capture.setYear(Integer.parseInt(cursor.getString(1)));
            capture.setMonth(Integer.parseInt(cursor.getString(2)));
            capture.setDay(Integer.parseInt(cursor.getString(3)));
            capture.setStartRecord(Long.parseLong(cursor.getString(4)));
            capture.set_cap(cursor.getString(5));
            capture.set_podo(cursor.getString(6));
            capture.setEndRecord(Long.parseLong(cursor.getString(7)));
        }
        cursor.close();
        db.close();
        return capture;
    }

    /**
     * Vérifie si la ligne donner en paramètre est existante dans la base de données
     * @param ligne la ligne dans la base de données
     */
    public boolean isExist(int ligne){
        //Log.e("LIGNE ", ""+ligne);
        String query = "Select * FROM " + TABLE_CAPTURE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        boolean retour = true;
        if(cursor.moveToPosition(ligne)){
            _Capture capture = new _Capture();
            capture.set_id(Long.parseLong(cursor.getString(0)));
            capture.setYear(Integer.parseInt(cursor.getString(1)));
            capture.setMonth(Integer.parseInt(cursor.getString(2)));
            capture.setDay(Integer.parseInt(cursor.getString(3)));
            capture.setStartRecord(Long.parseLong(cursor.getString(4)));
            capture.set_cap(cursor.getString(5));
            capture.set_podo(cursor.getString(6));
            capture.setEndRecord(Long.parseLong(cursor.getString(7)));

        }else
            retour = false;
        cursor.close();
        db.close();
        return retour;
    }

    /**
     * Permet de supprimer la base de données
     */
    public void deleteTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAPTURE);
        onCreate(db);
    }

}