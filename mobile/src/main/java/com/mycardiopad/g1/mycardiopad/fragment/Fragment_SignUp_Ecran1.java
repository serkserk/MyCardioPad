package com.mycardiopad.g1.mycardiopad.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.mycardiopad.g1.mycardiopad.R;
import com.mycardiopad.g1.mycardiopad.database._Compte;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Character.isDigit;
import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;


/**
 * Réalisé par nicolassalleron le 17/02/16. <br/>
 * Premier écran de l'inscription  <br/>
 */
public class Fragment_SignUp_Ecran1 extends Fragment implements SurfaceHolder.Callback{

    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    ImageView imageView;
    FloatingActionButton fab;
    Handler handler;
    Camera.PictureCallback rawCallback;
    Camera.ShutterCallback shutterCallback;
    Camera.PictureCallback jpegCallback;

    Switch stch;
    Button btnBirth;

    private EditText mNameView, mFNameView, mEmailView, mPasswordView,mPoidsView,mTailleView;
    private CheckBox chkHomme, chkFemme;
    private int width;
    private int height;

    private File photo;
    private int mYear_birth;
    private int mMonth_birth;
    private int mDay_birth;
    private TextView mAge;
    private FloatingActionButton btnSuivant;
    @SuppressWarnings("unused")
    private List<Camera.Size> mSupportedPreviewSizes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_signup_ecran1, container, false);
        surfaceView = (SurfaceView) view.findViewById(R.id.surface_camera);
        imageView = (ImageView) view.findViewById(R.id.imageViewCamera);
        fab = (FloatingActionButton) view.findViewById(R.id.btnPhoto);
        mAge = (TextView) view.findViewById(R.id.mTextDate);
        stch = (Switch) view.findViewById(R.id.switchCamera);
        //Mise en place de la date de naissance
        btnBirth = (Button) view.findViewById(R.id.btnBirth);
        btnBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                mYear_birth = c.get(Calendar.YEAR);
                mMonth_birth = c.get(Calendar.MONTH);
                mDay_birth = c.get(Calendar.DAY_OF_MONTH);
                System.out.println("the selected " + mDay_birth);
                DatePickerDialog dialog = new DatePickerDialog(getActivity(),AlertDialog.THEME_HOLO_LIGHT
                        ,new mDateSetListener(), mYear_birth, mMonth_birth, mDay_birth);
                dialog.setMessage("Veuillez choisir votre date de naissance");
                dialog.setTitle("Date de naissance");
                dialog.show();
            }
        });


        // Set up the checkErrors form.
        mNameView = (EditText)  view.findViewById(R.id.editNom);
        mFNameView = (EditText)  view.findViewById(R.id.editPrenom);
        mEmailView = (EditText)  view.findViewById(R.id.editEmail);
        mPasswordView = (EditText)  view.findViewById(R.id.editPassword);
        mPoidsView = (EditText) view.findViewById(R.id.editPoids);
        mTailleView = (EditText) view.findViewById(R.id.editTaille);
        chkHomme =  (CheckBox) view.findViewById(R.id.chkHomme);
        chkFemme = (CheckBox) view.findViewById(R.id.chkFemme);

        FocusEditTextListener fo = new FocusEditTextListener();
        //Pour l'interface
        mNameView.setOnFocusChangeListener(fo);
        mFNameView.setOnFocusChangeListener(fo);
        mEmailView.setOnFocusChangeListener(fo);
        mPasswordView.setOnFocusChangeListener(fo);
        mPoidsView.setOnFocusChangeListener(fo);
        mTailleView.setOnFocusChangeListener(fo);


        //On ne peut pas avoir les deux sexes
        chkFemme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    chkHomme.setChecked(false);
            }
        });
        chkHomme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    chkFemme.setChecked(false);
            }
        });



        //Allumage ou extinction de la caméra suivant le switch
        stch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stch.isChecked()) {
                    surfaceView.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.INVISIBLE);
                    fab.setVisibility(View.VISIBLE);
                    fab.animate().alpha(1.0f);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            start_camera();
                            mSupportedPreviewSizes = camera.getParameters().getSupportedPreviewSizes();
                            setCameraDisplayOrientation(1, camera);
                        }
                    }).start();



                } else {
                    surfaceView.setVisibility(View.INVISIBLE);
                    imageView.setVisibility(View.VISIBLE);

                    fab.animate().alpha(0.0f);
                    fab.setVisibility(View.GONE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            stop_camera();
                        }
                    }).start();
                }
            }
        });

        surfaceView.setVisibility(View.INVISIBLE);
        fab.setVisibility(View.GONE);
        handler = new Handler();

        //Photo
        photo = new File(getString(R.string.default_save_emplacement_user));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        captureImage();
                    }
                }).start();

            }
        });

        surfaceView = (SurfaceView) view.findViewById(R.id.surface_camera);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        rawCallback = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {
            }
        };
        /** Gestionnaire de data pour le jpeg */
        shutterCallback = new Camera.ShutterCallback() {
            public void onShutter() {

            }
        };
        jpegCallback = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {
                FileOutputStream outStream;
                try {

                    //Sauvegarde de la premiere bitmap
                    outStream = new FileOutputStream(photo);
                    outStream.write(data);
                    outStream.close();


                    //Rotation de 90 degres
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeFile(photo.getAbsolutePath(),bmOptions);
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(),true);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), matrix, true);

                    //Enregistrement de la nouvelle Bitmap
                    FileOutputStream out = null;
                    try {
                        out = new FileOutputStream(photo);
                        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally { //Fermeture du fichier
                        try {
                            if (out != null) {
                                out.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        return view;
    }

    /**
     * Prise de l'image
     */
    private void captureImage() {
        // TODO Auto-generated method stub
        try {
            camera.takePicture(shutterCallback,rawCallback,jpegCallback);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Permet de définir orientation de la caméra
     * @param cameraId l'identifiant de la caméra
     * @param camera la caméra hardware
     */
    public void setCameraDisplayOrientation( int cameraId, android.hardware.Camera camera) {

        android.hardware.Camera.CameraInfo info =   //Récupération des information
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);

        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();  //récupération de l'orientation
        int degrees = 0;

        switch (rotation) { //définit la rotation à adopter
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) { //Si la caméra est de face
            result = (info.orientation + degrees) % 360;    //On oriente la caméra
            result = (360 - result) % 360;
        } else {  // On oriente la caméra
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result); //Mise en place de l'orientation
    }


    /**
     * Définit la meilleur résolution de la caméra
     * @param width largeur
     * @param height hauteur
     * @param parameters objet parametre de la caméra
     * @return Objet Camera.Size contenant la meilleur résolution à adopter
     */
    private Camera.Size getBestPreviewSize(int width, int height,
                                           Camera.Parameters parameters) {
        Camera.Size result=null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width<=width && size.height<=height) {
                if (result==null) {
                    result=size;
                }
                else {
                    int resultArea=result.width*result.height;
                    int newArea=size.width*size.height;

                    if (newArea>resultArea) {
                        result=size;
                    }
                }
            }
        }

        return(result);
    }

    private void start_camera()
    {
        try{
            camera = Camera.open(1);
        }catch(RuntimeException ignored){}

        Camera.Parameters param;
        param = camera.getParameters();

        //Modification des paramètres de l'image renvoyé par la caméra
        Camera.Size size=getBestPreviewSize(width, height,
                param);
        if (size!=null) {
            param.setPreviewSize(size.width, size.height);
        }

        //Mise en place du nombre d'image de la préview
        param.setPreviewFrameRate(24);
        camera.setParameters(param);


        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();

        } catch (Exception ignored) {

        }
    }

    /**
     * permet l'arrêt de la caméra
     */
    private void stop_camera()
    {
        if(camera != null){
            camera.stopPreview();
            //mSurfaceView.getHolder().removeCallback(this);
            camera.release();
        }

    }


    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

        width = arg2;   //Pour connaitre les dimensions d'affichage de la caméra
        height = arg3;

    }

    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub




    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    /**
     * On désactive la caméra par défaut
     */
    @Override
    public void onResume() {
        super.onResume();
        stch.setChecked(false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    /**
     * Vérification de l'état de l'inscription
     * @return vrai ou faux si l'inscription est complète ou non
     */
    public boolean getEtatInscription(){
        return checkErrors();
    }

    /**
     * Récupération du nouveau compte de l'utilisateur
     * @return l'objet compte contenant les informations de l'utilisateur
     */
    public _Compte getCompte(){
        _Compte compte = new _Compte();
        compte.set_id(1);
        compte.set_nom(mNameView.getText().toString());
        compte.set_prenom(mFNameView.getText().toString());
        compte.set_email(mEmailView.getText().toString());
        compte.set_mot_de_passe(md5(mPasswordView.getText().toString()));
        compte.set_poids(Integer.parseInt(mPoidsView.getText().toString()));
        compte.set_taille(Integer.parseInt(mTailleView.getText().toString()));

        //Sexe
        if(chkHomme.isChecked())
            compte.set_sexe(0);
        else
            compte.set_sexe(1);
        compte.set_date_de_naissance(new GregorianCalendar(mYear_birth,mMonth_birth,mDay_birth).getTime());
        compte.set_path_photo(photo.getAbsolutePath());

        return compte;
    }

    /**
     * Converti un mot de passe en clair en son équivalent en md5
     * @param s le mot de passe
     * @return le mot de passe en md5
     */
    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Vérification des différents champs
     * @return vrai ou faux si il n'y a pas d'érreur de saisie d'information
     */
    private boolean checkErrors() {

        ArrayList<View> listView = new ArrayList<>();
        // Remise à 0 des érreurs
        mNameView.setError(null);
        mFNameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mPoidsView.setError(null);
        mTailleView.setError(null);

        // Enregistrement des valeur
        String name = mNameView.getText().toString();
        String fname = mFNameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String poids = mPoidsView.getText().toString();
        String taille = mTailleView.getText().toString();

        boolean cancel = false;

        boolean majuscule = false;
        boolean minuscule = false;
        boolean chiffre = false;

        //Vérification si le mot de passe contient un majuscule, minuscule ou chiffre
        for (int i = 0; i < password.length() && !(majuscule && minuscule && chiffre); i++)
        {
            char caractere = password.charAt(i);
            majuscule = majuscule || isUpperCase(caractere);
            minuscule = minuscule || isLowerCase(caractere);
            chiffre = chiffre || isDigit(caractere);
        }


        if (TextUtils.isEmpty((name))){  // Vérification pour un nom valide
            mNameView.setError(getString(R.string.error_field_required));
            listView.add(mNameView);
            cancel = true;
        }

        if (TextUtils.isEmpty((fname))){    // Vérification pour un prénom valide
            mFNameView.setError(getString(R.string.error_field_required));
            listView.add(mFNameView);
            cancel = true;
        }

        // Vérification pour un mot de passe valide, si l'utilisateur en a entré un.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            if (password.length() < 8) {
                mPasswordView.setError(getString(R.string.error_password_length));
                listView.add(mPasswordView);
                cancel = true;
            }
            else if(!majuscule){
                mPasswordView.setError(getString(R.string.error_password_upper));
                listView.add(mPasswordView);
                cancel = true;
            }
            else if(!minuscule){
                mPasswordView.setError(getString(R.string.error_password_lower));
                listView.add(mPasswordView);
                cancel = true;
            }
            else if(!chiffre){
                mPasswordView.setError(getString(R.string.error_password_digit));
                listView.add(mPasswordView);
                cancel = true;
            }
        }

        // Vérification pour l'adresse email
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            listView.add(mEmailView);
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            listView.add(mEmailView);
            cancel = true;
        }

        // Vérification pour le poids
        if (TextUtils.isEmpty((poids))){
            mPoidsView.setError(getString(R.string.error_field_required));
            listView.add(mPoidsView);
            cancel = true;
        }

        // Vérification pour la taille
        if (TextUtils.isEmpty((taille))){
            mTailleView.setError(getString(R.string.error_field_required));
            listView.add(mTailleView);
            cancel = true;
        }
        if (cancel) {
            listView.get(0).requestFocus();
        }
        return !cancel;
    }

    /**
     * Vérification si l'email est valide
     * @param email le string
     * @return vrai ou faux
     */
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    /**
     * Pattern du password
     * http://www.mkyong.com/regular-expressions/how-to-validate-password-with-regular-expression/
     */
    private static final String PASSWORD_PATTERN =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,20})";


    /**
     * Vérification si le password est valide
     * @param password le password de l'utilisateur
     * @return vrai ou faux si le password correspond au pattern
     */
    private boolean isPasswordValid(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }


    /**
     * Selection de la date et récupération des données
     */
    class mDateSetListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            mYear_birth = year;
            mMonth_birth = monthOfYear;
            mDay_birth = dayOfMonth;
            mAge.setText(new StringBuilder()
                    .append(mDay_birth).append("-").append(mMonth_birth + 1).append("-")
                    .append(mYear_birth).append(""));
            Log.e("Year",mYear_birth+"");
            Log.e("Month",mMonth_birth+"");
            Log.e("Day",mDay_birth+"");
            btnSuivant.show();
        }
    }

    /**
     * Mise en place du bouton suivant
     * @param btnSuivant le bouton
     */
    public void  setBtn(FloatingActionButton btnSuivant){
        this.btnSuivant = btnSuivant;
    }

    /**
     * Changement de focus
     */
    class FocusEditTextListener implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
                    btnSuivant.hide();
        }
    }

    /**
     * Suppression de la view
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        btnSuivant.show();
    }
}

