package com.mycardiopad.g1.mycardiopad.database;

import android.test.AndroidTestCase;

import org.junit.Assert;

import java.util.Date;

/**
 * Réalisé par nicolassalleron le 10/04/16.
 * Vérification dans la database d'ajout sans erreur de capture
 * Vérification dans la database d'ajout avec erreur de capture
 * Vérification du nombre de ligne après suppression de la db
 */
public class MyDBHandler_CaptureTest extends AndroidTestCase {


    MyDBHandler_Capture dbcap = new MyDBHandler_Capture(getContext(),null,null,1);

    public void testNumberLine() throws Exception {
        dbcap = new MyDBHandler_Capture(getContext(),null,null,1);
        dbcap.deleteTable();
        Assert.assertEquals( 0,dbcap.numberLine());
    }

    public void testAddCapture() throws Exception {
        dbcap = new MyDBHandler_Capture(getContext(),null,null,1);
        _Capture cap = new _Capture();
        cap.set_id(dbcap.numberLine());
        cap.set_cap("0µ0µ0");
        cap.set_podo("0µ0µ0");
        cap.setDay(10);
        cap.setMonth(5);
        cap.setYear(1900);
        cap.setStartRecord(new Date().getTime());
        cap.setStartRecord(new Date().getTime());
        int nbLigne = (int) dbcap.numberLine();
        dbcap.addCapture(cap);
        Assert.assertEquals(dbcap.numberLine(),nbLigne + 1);
    }

    public void testBadIDCapture() throws Exception {
        dbcap = new MyDBHandler_Capture(getContext(),null,null,1);
        _Capture cap = new _Capture();
        cap.set_id(0);
        cap.set_cap("0µ0µ0");
        cap.set_podo("0µ0µ0");
        cap.setDay(10);
        cap.setMonth(5);
        cap.setYear(1900);
        cap.setStartRecord(new Date().getTime());
        cap.setStartRecord(new Date().getTime());
        int nbLigne = (int) dbcap.numberLine();
        dbcap.addCapture(cap);
        Assert.assertEquals(dbcap.numberLine(), nbLigne );   //Pas d'ajout dans ce cas
    }
}