package com.mycardiopad.g1.mycardiopad.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.mycardiopad.g1.mycardiopad.R;

/**
 * Réalisé par nicolassalleron le 17/02/16. <br/>
 * Ecran final de l'inscription de l'utilisateur. C'est ici que l'inscription est envoyé au backOffice <br/>
 */
public class Fragment_SignUp_Ecran3 extends Fragment {

    private CheckBox chkBox;
    private FloatingActionButton btnSuivant;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_signup_ecran3, container, false);

        String htmlText = " %s ";
        String myData = "<html><body style=\"text-align:justify\"> "+
                " Lorem ipsum dolor sit amet, consectetur adipiscing elit." +
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
        WebView webView = (WebView) v.findViewById(R.id.webViewInscription);
        webView.loadData(String.format(htmlText, myData), "text/html", "utf-8");


        chkBox = (CheckBox) v.findViewById(R.id.chkPolitique);      //Mise en place de la checkbox pour lancer l'inscription ou non.
        chkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    btnSuivant.show();
                else
                    btnSuivant.hide();
            }
        });


        return v;
    }

    public boolean getEtatInscription() {

        if(chkBox.isChecked()){
            return true;
        }
        return false;
    }

    public void  setBtn(FloatingActionButton btnSuivant){
        this.btnSuivant = btnSuivant;
    }
}
