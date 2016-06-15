package com.mycardiopad.g1.mycardiopad.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.isseiaoki.simplecropview.CropImageView;
import com.mycardiopad.g1.mycardiopad.R;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Réalisé par nicolassalleron le 17/02/16. <br/>
 * Permet de faire une rotation d'image, de rogner l'image utilisateur et réalise un seuillage d'Otsu  <br/>
 *
 */
public class Activity_Crop extends Activity {

    private boolean _taken = true;
    private CropImageView cropImageView;
    private FloatingActionButton cropButton;
    private File photoFile;
    private static final String PHOTO_TAKEN = "photo_taken";
    private TextView textView;
    private FloatingActionButton btnRotateRight;
    private FloatingActionButton btnRotateLeft;
    private ProgressDialog barProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        Intent i = getIntent();

        //Mise en route d'OpenCV pour le traitement de l'image
        if (!OpenCVLoader.initDebug()) {
            //Gestion de l'érreur d'ouverture
            setResult(-1);
            Log.e("Erreur","OpenCV");
            finish();
        }

        initViews();
        initProgressBar();

        btnRotateLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barProgressDialog.show();
                //Rotation de 90 degres gauche
                Matrix matrix = new Matrix();
                matrix.postRotate(-90);
                Bitmap bitmap = cropImageView.getImageBitmap();
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(),true);
                cropImageView.setImageBitmap(Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), matrix, true));
                //seuillageOtsu(bitmap);
                barProgressDialog.dismiss();
            }
        });

        btnRotateRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                barProgressDialog.show();
                //Rotation de 90 degres droit
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap bitmap = cropImageView.getImageBitmap();
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(),true);
                cropImageView.setImageBitmap(Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), matrix, true));
                barProgressDialog.dismiss();
            }
        });

        cropImageView.setHandleColor(getResources().getColor(R.color.MockupBlueBack));
        cropImageView.setGuideColor(getResources().getColor(R.color.MockupBlueBack));


        //Mise en place du texte suivant le type d'image
        int resquest = i.getExtras().getInt("requestCode");
        textView = (TextView) findViewById(R.id.textViewAction);
        if (resquest == 2) {
            textView.setText("Certificat médical de non contre-indication");
        } else if (resquest == 3) {
            textView.setText("Epreuve d'Effort");
        }


        cropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Récupère l'image rogner et l'affiche
                try {
                    FileOutputStream fos = new FileOutputStream(photoFile.getAbsolutePath());
                    Bitmap bitmap = cropImageView.getCroppedBitmap();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fos);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                //Mise en place du résultat
                setResult(RESULT_OK, new Intent().putExtra("file", photoFile.getAbsolutePath()));
                finish();
            }
        });

        requestCapture();

    }

    /**
     * Initialisation des vues
     */
    private void initViews() {
        cropImageView = (CropImageView) findViewById(R.id.cropImageView);
        textView = (TextView) findViewById(R.id.textViewAction);
        cropImageView.setCropMode(CropImageView.CropMode.RATIO_FREE);
        cropButton = (FloatingActionButton) findViewById(R.id.cropButton);
        btnRotateRight = (FloatingActionButton) findViewById(R.id.rotateRight);
        btnRotateLeft = (FloatingActionButton) findViewById(R.id.rotateLeft);
    }

    /**
     * Initialisation de la progressBar
     */
    private void initProgressBar() {
        barProgressDialog = new ProgressDialog(this);
        barProgressDialog.setTitle("Rotation ...");
        barProgressDialog.setMessage("Rotation de l'image ...");
        barProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(photoFile.length() == 0){
            setResult(Activity.RESULT_CANCELED);
            finish();
        }else {
            seuillageOtsu(BitmapFactory.decodeFile(photoFile.getAbsolutePath()));
        }
    }

    /**
     * Réalise un seuillage d'Otsu d'adaptatif avec OpenCV
     * @param bitmap le bitmap de la photo
     */
    private void seuillageOtsu(final Bitmap bitmap) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Mat imageMat = new Mat();
                Utils.bitmapToMat(bitmap, imageMat);
                Imgproc.cvtColor(imageMat, imageMat, Imgproc.COLOR_BGR2GRAY);
                Imgproc.medianBlur(imageMat, imageMat, 3);
                Imgproc.adaptiveThreshold(imageMat, imageMat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 5, 4);
                Imgproc.threshold(imageMat, imageMat, 0, 255, Imgproc.THRESH_OTSU);
                Utils.matToBitmap(imageMat,bitmap);

                //Affichage du bitmap
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cropImageView.setImageBitmap(bitmap);
                    }
                });
            }
        }).start();
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.getBoolean(Activity_Crop.PHOTO_TAKEN)) {
            _taken = true;
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(Activity_Crop.PHOTO_TAKEN, _taken);
    }




    /**
     * Retourne un fichier avec un nom formaté <br/>
     * L'image est en enregistrer sur le stockage externe dans le dossier MyCardioPad
     * @return Un fichier contenant l'image
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";


        //Répertoire des images de l'inscription
        File storageDir = new File(Environment.getExternalStorageDirectory(),"MyCardioPad");
        File image = null;
        if(storageDir.exists())
            image = new File(storageDir, imageFileName + ".jpg");
        else
            if(storageDir.mkdirs()){
                image = new File(storageDir, imageFileName + ".jpg");
            }
        return image;
    }

    static final int REQUEST_TAKE_PHOTO = 1;


    /**
     * Lancement de la requête de capture de la photo via l'appareil photo.
     */
    private void requestCapture() {
        //Préparation de l'activité
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Vérification que la caméra est bien présente sur l'appareil
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Initialisation du fichier
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) { //Dans le cas d'un échec
                ex.printStackTrace();
            }
            // On continu seulement si le fichier est correctement crée
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));

                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO); //Démarrage de l'activité
            }
        }else{  //Dans le cas ou il n'y a pas de caméra
            setResult(-1);
            Log.e("Erreur ","Pas d'activité de caméra");
            finish();
        }
    }
}
