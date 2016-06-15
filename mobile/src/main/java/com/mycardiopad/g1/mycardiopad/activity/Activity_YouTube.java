package com.mycardiopad.g1.mycardiopad.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebView;

import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.mycardiopad.g1.mycardiopad.R;
import com.mycardiopad.g1.mycardiopad.fragment.Fragment_YouTube;

/**
 * Réalisé par kevin le 01/03/2016.  <br/>
 * C'est ici que sont mise en place les vidéos Youtube <br/>
 */

public class Activity_YouTube extends ActionBarActivity {
    public static final String KEY_VIDEO_ID = "KEY_VIDEO_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_youtube);

        String htmlText = " %s ";
        String myData = "<html><body style=\"text-align:justify\"> "+
                "TESTLorem ipsum dolor sit amet, consectetur adipiscing elit." +
                " Cras eget mauris eu nunc dignissim accumsan et at eros." +
                " Maecenas eu ligula rhoncus, egestas lacus condimentum, auctor sem." +
                " Pellentesque interdum ornare mauris, in accumsan diam molestie in. " +
                "Ut ultrices, massa quis dapibus fermentum, risus odio egestas dui," +
                " a convallis nibh elit id est. Mauris nec odio eu felis pharetra molestie hendrerit" +
                " id sapien. Fusce a mollis nisl. Mauris eu mollis lorem, a dapibus sapien." +
                " Aenean sollicitudin, est vel tristique aliquet, massa tellus egestas nisi," +
                " quis ornare leo erat et justo. In hac habitasse platea dictumst." +
                " Donec nec sagittis est, egestas pellentesque metus." +
                " Suspendisse hendrerit et purus id aliquet. Nullam finibus enim tempus," +
                " placerat ipsum ut, commodo odio. Vivamus molestie sapien eu ante aliquet rhoncus." +
                "Proin nec posuere mi. Nunc aliquam lobortis enim, at efficitur justo porttitor vitae." +
                " Ut sodales interdum luctus. Proin sit amet efficitur arcu, et pellentesque eros. Mauris id."+
                "</body></Html>";
        WebView webView = (WebView) findViewById(R.id.webView);
        assert webView != null;
        webView.loadData(String.format(htmlText, myData), "text/html", "utf-8");

        final Bundle bundle = getIntent().getExtras();

        if (bundle != null && bundle.containsKey(KEY_VIDEO_ID)) {
            final String videoId = bundle.getString(KEY_VIDEO_ID);
            final Fragment_YouTube fragment = (Fragment_YouTube) getSupportFragmentManager().findFragmentById(R.id.fragment_youtube);
            fragment.setVideoId(videoId);
        }

        //Affiche une erreur si jamais YouTube n'est pas encore installé ou si la version est trop vieille
        final YouTubeInitializationResult result = YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(this);
        if (result != YouTubeInitializationResult.SUCCESS) {
            result.getErrorDialog(this,0).show();
        }

    }
}
