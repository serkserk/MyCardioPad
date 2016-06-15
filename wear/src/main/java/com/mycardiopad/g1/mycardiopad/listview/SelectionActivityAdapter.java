package com.mycardiopad.g1.mycardiopad.listview;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.view.ViewGroup;

import java.util.List;

/**
 * Réalisé par nicolassalleron le 14/02/16. <br/>
 * Permet la sélection d'une activitée  <br/>
 */
public class SelectionActivityAdapter extends WearableListView.Adapter{
    private Context context;
    private List<SelectionActivityItem> listViewItems;

    /**
     * Mise en place de la listView dans l'adaptateur
     * @param context   le context
     * @param listViewItems La listView contenant les items
     */
    public SelectionActivityAdapter(Context context, List<SelectionActivityItem> listViewItems){
        this.context = context;
        this.listViewItems = listViewItems;
    }

    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WearableListView.ViewHolder(new SelectionActivityRowView(context));
    }

    /**
     * Mise en place des images et du texte
     * @param holder    le holder
     * @param position  la position
     */
    @Override
    public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {

        SelectionActivityRowView listViewRowView = (SelectionActivityRowView) holder.itemView;

        final SelectionActivityItem listViewItem = listViewItems.get(position);
        listViewRowView.getImage().setImageResource(listViewItem.imageRes);
        listViewRowView.getText().setText(listViewItem.text);
        listViewRowView.getMinText().setText(listViewItem.minText);

    }

    @Override
    public int getItemCount() {
        return listViewItems.size();
    }   //Le nombre total
}
