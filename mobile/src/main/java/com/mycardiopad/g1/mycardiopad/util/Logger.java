package com.mycardiopad.g1.mycardiopad.util;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

/**
 * Réalisé par nicolassalleron le 21/03/16. <br/>
 * Permet d'écrire dans le fichier de Log. <br/>
 */
public class Logger {

    /**
     * Permet d'écrire directement sur un fichier de log
     * @param TAG Le tag de l'activité
     * @param log La string du log
     */
    public static void write(String TAG, String log){
        Calendar cal = Calendar.getInstance();
        String logger = (cal.get(Calendar.MONTH)+1)+"-"
                + cal.get(Calendar.DAY_OF_MONTH)
                + "\t" + cal.get(Calendar.HOUR_OF_DAY)+":"
                +cal.get(Calendar.MINUTE)+":"
                +cal.get(Calendar.SECOND)+":"
                +cal.get(Calendar.MILLISECOND)+"\t"
                +"com.mycardiopad.g1.mycardiopad:"+TAG+" : "+log;
        appendLog(logger);
    }

    /**
     * Ajoute en fin de ligne la nouvelle String
     * @param text la string qui sera ajouté
     */
    private static void appendLog(String text)
    {
        File logFile = new File("sdcard/MyCardioPad/log.file");
        if (!logFile.exists())
        {
            try
            {
                if(logFile.createNewFile())
                    Log.e("logFile : ", " création OK");
                else
                    Log.e("logFile : ", " problème de création");
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
