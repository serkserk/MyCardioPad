package com.mycardiopad.g1.mycardiopad.adapter.Adapter_ListDialogs;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycardiopad.g1.mycardiopad.R;

import java.util.List;

/**
 * Réalisé par Vishnupriya le 17/02/16.
 *
 */
public class CustomListViewAdapter extends ArrayAdapter<Adapter_ListDialogs> {
    private  List<Adapter_ListDialogs> list;
    private final Context context;

    static class ViewHolder {
        protected TextView item;
        protected ImageView image;
    }

    public CustomListViewAdapter(Context context, List<Adapter_ListDialogs> list) {
        super(context, R.layout.dialog_list_custom, list);
        this.context = context;
        this.list = list;
    }

    // retourne le nombre d'objet présent dans notre liste
    public int getCount() {
        return list.size();
    }

    // retourne un élément de notre liste en fonction de sa position
    public Adapter_ListDialogs getItem(int position) {
        return list.get(position);
    }

    // retourne l'id d'un élément de notre liste en fonction de sa position
    public long getItemId(int position) {
        return list.indexOf(getItem(position));
    }

    // retourne la vue d'un élément de la liste
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;

        // au premier appel ConvertView est null, on inflate notre layout
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = mInflater.inflate(R.layout.listview_item_dialog_list_in_adapter, parent, false);

            // nous plaçons dans notre MyViewHolder les vues de notre layout
            mViewHolder = new ViewHolder();
            mViewHolder.item = (TextView) convertView
                    .findViewById(R.id.item_dialog_list_item);
            mViewHolder.image = (ImageView) convertView
                    .findViewById(R.id.item_dialog_list_image);

            // nous attribuons comme tag notre MyViewHolder à convertView
            convertView.setTag(mViewHolder);
        } else {
            // convertView n'est pas null, nous récupérons notre objet MyViewHolder
            // et évitons ainsi de devoir retrouver les vues à chaque appel de getView
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        // nous récupérons l'item de la liste demandé par getView
        Adapter_ListDialogs listItem = getItem(position);

        // nous pouvons attribuer à nos vues les valeurs de l'élément de la liste
        mViewHolder.item.setText(listItem.getItem());
        mViewHolder.image.setImageDrawable(listItem.getImage());

        // nous retournons la vue de l'item demandé
        return convertView;

    }
}

