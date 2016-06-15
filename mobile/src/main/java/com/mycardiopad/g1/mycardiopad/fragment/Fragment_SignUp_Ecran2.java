package com.mycardiopad.g1.mycardiopad.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mycardiopad.g1.mycardiopad.R;
import com.mycardiopad.g1.mycardiopad.activity.Activity_Crop;

import java.io.File;

/**
 * Réalisé par nicolassalleron le 17/02/16.  <br/>
 * Deuxième écran d'inscription prenant en charge les images de l'utilisateur  <br/>
 */
public class Fragment_SignUp_Ecran2 extends Fragment {


    private int RESULT_CNCI = 2, RESULT_EE = 3;
    private ImageView imageViewCNCI;
    private ImageView imageViewEE;
    private FloatingActionButton btnDelCaptureCNCI;
    private FloatingActionButton btnDelCaptureEE;
    private LinearLayout layout ;
    File EE, CNCI;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_signup_ecran2, container, false);
        layout = (LinearLayout) v.findViewById(R.id.SignUpEcran2);
        FloatingActionButton btnCaptureCNCI = (FloatingActionButton) v.findViewById(R.id.btnPhotoCNCI);
        FloatingActionButton btnCaptureEE = (FloatingActionButton) v.findViewById(R.id.btnPhotoEE);
        btnDelCaptureCNCI = (FloatingActionButton) v.findViewById(R.id.btnDelPhotoCNCI);
        btnDelCaptureEE = (FloatingActionButton) v.findViewById(R.id.btnDelPhotoEE);
        imageViewCNCI = (ImageView) v.findViewById(R.id.AffichageImageCNCI);
        imageViewEE = (ImageView) v.findViewById(R.id.AffichageImageEE);

        btnCaptureCNCI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent captureIntent = new Intent(getContext(),Activity_Crop.class);
                captureIntent.putExtra("requestCode", RESULT_CNCI);
                startActivityForResult(captureIntent,RESULT_CNCI);
            }
        });
        btnCaptureEE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent captureIntent = new Intent(getContext(),Activity_Crop.class);
                captureIntent.putExtra("requestCode", RESULT_EE);
                startActivityForResult(captureIntent,RESULT_EE);
            }
        });
        btnDelCaptureCNCI.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onClick(View v) {
                btnDelCaptureCNCI.hide();
                imageViewCNCI.setImageResource(android.R.color.transparent);
                Snackbar.make(getActivity().getCurrentFocus(), "L'image a été supprimée", Snackbar.LENGTH_LONG)
                        .show();
            }
        });
        btnDelCaptureEE.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onClick(View v) {
                imageViewEE.setImageResource(android.R.color.transparent);
                btnDelCaptureEE.hide();
                Snackbar.make(getActivity().getCurrentFocus(), "L'image a été supprimée", Snackbar.LENGTH_LONG)
                        .show();
            }
        });
        btnDelCaptureCNCI.hide();
        btnDelCaptureEE.hide();

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Résultat de la capture des images
     * @param requestCode le code de requête
     * @param resultCode le code de retour
     * @param data les données
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == Activity.RESULT_OK){
            @SuppressWarnings("ConstantConditions") File file = new File(data.getExtras().getString("file"));
            //Le fichier
            if(requestCode == RESULT_CNCI){
               //Toast.makeText(getActivity(),"RESULT_CNCI : " + data.getExtras().getString("file"),Toast.LENGTH_SHORT).show();
               imageViewCNCI.setImageURI(Uri.fromFile(file));
                CNCI = file;
               btnDelCaptureCNCI.show();
            }
            if(requestCode == RESULT_EE){
               //Toast.makeText(getActivity(),"RESULT_EE : " + data.getExtras().getString("file"),Toast.LENGTH_SHORT).show();
               imageViewEE.setImageURI(Uri.fromFile(file));
                EE = file;
                btnDelCaptureEE.show();
            }
        }else if (resultCode == Activity.RESULT_CANCELED){
            Snackbar.make(layout, "La photo n'a pas été prise.", Snackbar.LENGTH_LONG)
                    .setDuration(Snackbar.LENGTH_LONG)
                    .show();

        }

    }

    public File getFileEE(){return EE;}
    public File getFileCNCI(){return CNCI;}

}
