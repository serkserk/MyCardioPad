package com.mycardiopad.g1.mycardiopad.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycardiopad.g1.mycardiopad.R;
import com.mycardiopad.g1.mycardiopad.listview.List_ActivitesItem;

import java.util.ArrayList;

/**
 * Réalisé par nicolassalleron le 14/02/16. <br/>
 * Permet l'affichage de la liste des activités  <br/>
 */
public class Adapter_ActivitesListView extends BaseAdapter {

    ArrayList<List_ActivitesItem> myList = new ArrayList<List_ActivitesItem>();
    Context context;
    public int position;
    // on passe le context afin d'obtenir un LayoutInflater pour utiliser notre
    // row_layout.xml
    // on passe les valeurs de notre tableau à l'adapter

    /**
     * Adapter pour la selection d'activitée
     * @param context le contexte
     * @param myList la liste des activités
     */
    public Adapter_ActivitesListView(Context context, ArrayList<List_ActivitesItem> myList) {
        this.myList = myList;
        this.context = context;
    }

    // retourne le nombre d'objet présent dans notre liste
    @Override
    public int getCount() {
        return myList.size();
    }

    // retourne un élément de notre liste en fonction de sa position
    @Override
    public List_ActivitesItem getItem(int position) {
        return myList.get(position);
    }

    // retourne l'id d'un élément de notre liste en fonction de sa position
    @Override
    public long getItemId(int position) {
        return myList.indexOf(getItem(position));
    }

    // retourne la vue d'un élément de la liste
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder = null;

        // au premier appel ConvertView est null, on inflate notre layout
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = mInflater.inflate(R.layout.activites_list_row, parent, false);

            // nous plaçons dans notre MyViewHolder les vues de notre layout
            mViewHolder = new MyViewHolder();
            mViewHolder.textViewName = (TextView) convertView
                    .findViewById(R.id.textViewName);
            mViewHolder.imageView = (ImageView) convertView
                    .findViewById(R.id.imageView);

            // nous attribuons comme tag notre MyViewHolder à convertView
            convertView.setTag(mViewHolder);
        } else {
            // convertView n'est pas null, nous récupérons notre objet MyViewHolder
            // et évitons ainsi de devoir retrouver les vues à chaque appel de getView
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        // nous récupérons l'item de la liste demandé par getView
        List_ActivitesItem listItem = (List_ActivitesItem) getItem(position);
        // nous pouvons attribuer à nos vues les valeurs de l'élément de la liste
        mViewHolder.textViewName.setText(listItem.getName());
        mViewHolder.imageView.setImageResource(listItem.getImageId());
        // nous retournons la vue de l'item demandé
        return convertView;
    }

    // MyViewHolder va nous permettre de ne pas devoir rechercher
    // les vues à chaque appel de getView, nous gagnons ainsi en performance
    private class MyViewHolder {
        TextView textViewName, textViewAge;
        ImageView imageView;
    }

}