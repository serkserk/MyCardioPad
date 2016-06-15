package com.mycardiopad.g1.mycardiopad.fragment;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.mycardiopad.g1.mycardiopad.R;

/**
 * Réalisé par kevin le 01/03/2016. <br/>
 * Affichage des vidéos youtube suivant le click de l'utilisateur.  <br/>
 */

public class Fragment_YouTube extends YouTubePlayerSupportFragment implements YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_DIALOG_REQUEST = 1;

    private static final String KEY_VIDEO_ID = "KEY_VIDEO_ID";

    private String mVideoId;

    public Fragment_YouTube() {
    }

    /**
     * Retourne une nouvelle instance de Fragment_YouTube
     *
     * @param videoId l'ID de la vidéo à charger
     */
    public static Fragment_YouTube newInstance(final String videoId) {
        final Fragment_YouTube youTubeFragmentYouTube = new Fragment_YouTube();
        final Bundle bundle = new Bundle();
        bundle.putString(KEY_VIDEO_ID, videoId);
        youTubeFragmentYouTube.setArguments(bundle);
        return youTubeFragmentYouTube;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        final Bundle arguments = getArguments();

        if (bundle != null && bundle.containsKey(KEY_VIDEO_ID)) {
            mVideoId = bundle.getString(KEY_VIDEO_ID);
        } else if (arguments != null && arguments.containsKey(KEY_VIDEO_ID)) {
            mVideoId = arguments.getString(KEY_VIDEO_ID);
        }

        initialize(getString(R.string.DEVELOPER_KEY), this);
    }

    /**
     * Initialise le lecteur vidéo
     * @param videoId l'ID de la vidéo à charger
     */
    public void setVideoId(final String videoId) {
        mVideoId = videoId;
        initialize(getString(R.string.DEVELOPER_KEY), this);
    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean restored) {

        if (mVideoId != null) {
            if (restored) {
                youTubePlayer.play();
            } else {
                youTubePlayer.loadVideo(mVideoId);
            }
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(getActivity(), RECOVERY_DIALOG_REQUEST).show();
        } else {
            Toast.makeText(getActivity(), "Echec", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString(KEY_VIDEO_ID, mVideoId);
    }
}
