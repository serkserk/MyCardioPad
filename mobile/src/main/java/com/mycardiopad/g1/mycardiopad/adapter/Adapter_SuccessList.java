package com.mycardiopad.g1.mycardiopad.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycardiopad.g1.mycardiopad.R;
import com.mycardiopad.g1.mycardiopad.util.Succes;

import java.util.ArrayList;

/**
 * Réalisé par kevin le 14/03/2016. <br/>
 * Permet de mettre en place les différents succès.  <br/>
 */
public class Adapter_SuccessList extends RecyclerView.Adapter<Adapter_SuccessList.ViewHolder> {

    private ArrayList<Succes> listeSucces;
    public Adapter_SuccessList(ArrayList listeSucces) {
        this.listeSucces = listeSucces;
    }


    //Permet de créer la RecyclerView
    @Override
    public Adapter_SuccessList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);

        return new ViewHolder(itemLayoutView);
    }

    //Permet de mettre à jour la vue affichée dans le RecyclerView par rapport à la position de l'objet
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        viewHolder.titre.setText(listeSucces.get(position).getTitre());
        viewHolder.sousTitre.setText(listeSucces.get(position).getSousTitre());
        viewHolder.image.setImageResource(listeSucces.get(position).getImage());

    }

    /**
     * Cette classe permet d'instancier les éléments d'interface correspondant à l'itemLayoutView
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView titre;
        TextView sousTitre;
        public ImageView image;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            titre = (TextView) itemLayoutView.findViewById(R.id.item_titre);
            sousTitre = (TextView) itemLayoutView.findViewById(R.id.item_sousTitre);
            image = (ImageView) itemLayoutView.findViewById(R.id.item_image);
        }
    }

    @Override
    public int getItemCount() {
        return listeSucces.size();
    }


}