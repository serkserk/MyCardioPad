package com.mycardiopad.g1.mycardiopad.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import com.mycardiopad.g1.mycardiopad.activity.Activity_YouTube;
import com.mycardiopad.g1.mycardiopad.adapter.Adapter_VideoList;
import com.mycardiopad.g1.mycardiopad.content.YouTubeContent;

/**
 * Réalisé par Kévin <br/>
 * Affichage des différentes vidéos Youtube
 */
public class Fragment_VideoList extends ListFragment {

    public Fragment_VideoList() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new Adapter_VideoList(getActivity()));   //Mise en place de l'adapteur
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * Lors qu'un item est cliqué, lance l'activité youtube
     * @param l la listview
     * @param v la view
     * @param position la position
     * @param id l'id de la vidéo
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        final Context context = getActivity();
        final YouTubeContent.YouTubeVideo video = YouTubeContent.ITEMS.get(position);
                final Intent fragIntent = new Intent(context, Activity_YouTube.class);
                fragIntent.putExtra(Activity_YouTube.KEY_VIDEO_ID, video.id);
                startActivity(fragIntent);
        }
    }