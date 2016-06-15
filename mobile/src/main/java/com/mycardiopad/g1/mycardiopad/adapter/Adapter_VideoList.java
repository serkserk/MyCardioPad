package com.mycardiopad.g1.mycardiopad.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.mycardiopad.g1.mycardiopad.R;
import com.mycardiopad.g1.mycardiopad.content.YouTubeContent;

import java.util.HashMap;
import java.util.Map;

/**
 * Permet d'afficher les informations concernant les vidéos dans l'onglet vidéo de MonProgramme <br/>
 *
 */
public class Adapter_VideoList extends BaseAdapter implements YouTubeThumbnailView.OnInitializedListener {

    private Context mContext;
    private Map<View, YouTubeThumbnailLoader> mLoaders;

    public Adapter_VideoList(final Context context) {
        mContext = context;
        mLoaders = new HashMap<>();
    }

    @Override
    public int getCount() {
        return YouTubeContent.ITEMS.size();
    }

    @Override
    public Object getItem(int position) {
        return YouTubeContent.ITEMS.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        VideoHolder holder;

        final YouTubeContent.YouTubeVideo item = YouTubeContent.ITEMS.get(position);

        if (convertView == null) {
            final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_layout, parent, false);

            //Création
            holder = new VideoHolder();

            //Ajout du titre
            holder.title = (TextView) convertView.findViewById(R.id.textView_title);
            holder.title.setText(item.title);

            //Initialisation de la miniature
            holder.thumb = (YouTubeThumbnailView) convertView.findViewById(R.id.imageView_thumbnail);
            holder.thumb.setTag(item.id);
            holder.thumb.initialize(mContext.getString(R.string.DEVELOPER_KEY), this);

            convertView.setTag(holder);
        } else {
            holder = (VideoHolder) convertView.getTag();
            final YouTubeThumbnailLoader loader = mLoaders.get(holder.thumb);

            if (item != null) {
                holder.title.setText(item.title);
                holder.thumb.setImageBitmap(null);

                if (loader == null) {
                    //Initialisation du chargement
                    holder.thumb.setTag(item.id);
                } else {
                    // Le chargement est déjà initialisé
                    try {
                        loader.setVideo(item.id);
                    } catch (IllegalStateException exception) {
                        //Si le chargement à provoqué une exception, on le supprime et on le réinitialise
                        mLoaders.remove(holder.thumb);
                        holder.thumb.initialize(mContext.getString(R.string.DEVELOPER_KEY), this);
                    }
                }
            }
        }
        return convertView;
    }


    @Override
    public void onInitializationSuccess(YouTubeThumbnailView view, YouTubeThumbnailLoader loader) {
        mLoaders.put(view, loader);
        loader.setVideo((String) view.getTag());
    }

    @Override
    public void onInitializationFailure(YouTubeThumbnailView thumbnailView, YouTubeInitializationResult errorReason) {
        final String errorMessage = errorReason.toString();
        Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
    }


    static class VideoHolder {
        YouTubeThumbnailView thumb;
        TextView title;
    }
}
