package com.mycardiopad.g1.mycardiopad.database;

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

/**
 * Réalisé par kevin le 19/03/2016. <br/>
 * Base de données des succès. <br/>
 */

public class MyDBHandler_Succes extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Succes.db";
    private static final String TABLE_SUCCES = "_Succes";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NOM = "_nom";
    public static final String COLUMN_DESCRIPTION = "_description";
    public static final String COLUMN_DATE_OBTENTION = "_date_obtention";
    public static final String COLUMN_OBTENU = "_obtenu";

    @SuppressWarnings("unused")
    public MyDBHandler_Succes(Context context, String name,
                              SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " +
                TABLE_SUCCES + "("
                + COLUMN_ID + " LONG PRIMARY KEY,"
                + COLUMN_NOM + " STRING,"
                + COLUMN_DESCRIPTION + " STRING,"
                + COLUMN_DATE_OBTENTION + " DATE,"
                + COLUMN_OBTENU + " INTEGER"
                + ")";
        db.execSQL(CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUCCES);
        onCreate(db);
    }
    /**
     * Retourne le nombre de ligne de la base de données
     */
    public long numberLine() {
        SQLiteDatabase db = this.getWritableDatabase();
        return DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + TABLE_SUCCES, null);
    }
    /**
     * Met à jour un succès
     */
    public void updateSucces(_Succes succes) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, succes.get_id());
        values.put(COLUMN_NOM, succes.get_nom());
        values.put(COLUMN_DESCRIPTION, succes.get_description());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (succes.get_date_obtention() != null) {
            try {
                values.put(COLUMN_DATE_OBTENTION, sdf.format(succes.get_date_obtention()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        values.put(COLUMN_OBTENU, succes.get_obtenu());

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_SUCCES, values, COLUMN_ID + " = " + succes.get_id(), null);
        db.close();
    }

    /**
     * Ajoute un succès à la base de données
     * @param succes le succès qui sera ajouter
     */
    public void addSucces(_Succes succes) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, succes.get_id());
        values.put(COLUMN_NOM, succes.get_nom());
        values.put(COLUMN_DESCRIPTION, succes.get_description());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (succes.get_date_obtention() != null) {
            try {
                values.put(COLUMN_DATE_OBTENTION, sdf.format(succes.get_date_obtention()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        values.put(COLUMN_OBTENU, succes.get_obtenu());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_SUCCES, null, values);
        db.close();
        Log.e("addSucces", "Ajout de la bdd : OK");
    }


    /**
     * Permet d'obtenir le dernier succès de la base de données
     */
    public _Succes lastObtainedSucces() {
        String query = "SELECT * FROM " + TABLE_SUCCES + " WHERE " + COLUMN_OBTENU +
                " = 1 ORDER BY " + COLUMN_DATE_OBTENTION + " DESC LIMIT 0,1";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToNext()) {
            _Succes succes = new _Succes();
            succes.set_id(cursor.getInt(0));
            succes.set_nom(cursor.getString(1));
            succes.set_description(cursor.getString(2));
            succes.set_obtenu(cursor.getInt(4));
            succes.set_date_obtention(new Date());

            cursor.close();
            db.close();
            return succes;
        }

        cursor.close();
        db.close();
        return null;
    }

    /**
     * Retourne le succès suivant la ligne
     * @param id la ligne du succès
     */
    public _Succes getSucces(int id) {

        String query = "Select * FROM " + TABLE_SUCCES + " WHERE " + COLUMN_ID + " = " + id;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        _Succes succes = new _Succes();
        if (cursor.moveToFirst()) {
            succes.set_id(Long.parseLong(cursor.getString(0)));
            succes.set_nom(cursor.getString(1));
            succes.set_description(cursor.getString(2));
            succes.set_obtenu(Integer.parseInt(cursor.getString(4)));

            if (succes.get_obtenu() == 1) {
                try {
                    Date simpleDate = new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(3));
                    succes.set_date_obtention(simpleDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        cursor.close();
        db.close();
        return succes;
    }

    /**
     * Vérification si l'identifiant existe
     * @param id le numéro de la ligne
     */
    public boolean isIdExist(long id) {
        String query = "Select * FROM " + TABLE_SUCCES + " WHERE " + COLUMN_ID + " = " + id;
        SQLiteDatabase db = this.getWritableDatabase();
        try (Cursor cursor = db.rawQuery(query, null)) {

            return cursor.moveToNext();
        }
    }

    /**
     * Permet de supprimer la base de donnée
     */
    public void deleteTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUCCES);
        onCreate(db);
    }
}