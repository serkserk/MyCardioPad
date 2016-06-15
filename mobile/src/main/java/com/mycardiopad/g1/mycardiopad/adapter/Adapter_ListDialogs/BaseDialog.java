package com.mycardiopad.g1.mycardiopad.adapter.Adapter_ListDialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;

/**
 * Reprise de code
 * @author Created by Lewis on 30/08/2014.
 * @link https://github.com/lewisjdeane/L-Dialogs/blob/master/app/src/main/java/uk/me/lewisdeane/ldialogs/BaseDialog.java
 */
public abstract class BaseDialog extends AlertDialog {

    enum LightColours {
        TITLE("#474747"), ITEM("#999999");

        final String mColour;

        LightColours(String _colour) {
            this.mColour = _colour;
        }
    }

    enum DarkColours {
        TITLE("#CCCCCC"), ITEM("#999999");

        final String mColour;

        DarkColours(String _colour) {
            this.mColour = _colour;
        }
    }

    public enum Theme {
        LIGHT, DARK
    }

    public enum Alignment {
         CENTER,LEFT, RIGHT
    }

    BaseDialog(Context _context) {
        super(_context);
    }

    @SuppressLint("RtlHardcoded")
    static int getGravityFromAlignment(Alignment _alignment) {
        // Return corresponding gravity from our Alignment value.
        switch (_alignment) {
            case LEFT:
                return Gravity.LEFT;
            case CENTER:
                return Gravity.CENTER;
            case RIGHT:
                return Gravity.RIGHT;
            default:
                return Gravity.RIGHT;
        }
    }
}