package com.mycardiopad.g1.mycardiopad.listview;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.mycardiopad.g1.mycardiopad.R;

/**
 * Réalisé par nicolassalleron le 14/02/16. <br/>
 * Permet de sélectionner la ligne  <br/>
 */
public class SelectionActivityRowView extends FrameLayout implements WearableListView.OnCenterProximityListener {
    private CircularImageView image;
    private TextView text, minText;

    /**
     * Permet de réaliser une liste avec
     * L'image correspondant à l'activité
     * Le titre de l'activité
     * Le nombre en minutes correspondant à l'activité
     * @param context le contexte
     */
    public SelectionActivityRowView(Context context) {
        super(context);
        View.inflate(context, R.layout.selection_activity_image_text_item, this);
        image = (CircularImageView) findViewById(R.id.image);
        text = (TextView) findViewById(R.id.text);
        minText = (TextView) findViewById(R.id.minText);
    }

    @Override
    public void onCenterPosition(boolean b) {
        image.animate().scaleX(1f).scaleY(1f).alpha(1).setDuration(200);
        text.animate().scaleX(1f).scaleY(1f).alpha(1).setDuration(200);
        minText.animate().scaleX(1f).scaleY(1f).alpha(1).setDuration(200);
    }

    @Override
    public void onNonCenterPosition(boolean b) {
        image.animate().scaleX(0.9f).scaleY(0.9f).alpha(0.6f).setDuration(200);
        text.animate().scaleX(0.9f).scaleY(0.9f).alpha(0.6f).setDuration(200);
        minText.animate().scaleX(0.9f).scaleY(0.9f).alpha(0.6f).setDuration(200);
    }

    public CircularImageView getImage() {   //Retourne l'image
        return image;
    }

    public TextView getText() { //retourne le texte
        return text;
    }
    public TextView getMinText() {  //retourne les minutes
        return minText;
    }
}
