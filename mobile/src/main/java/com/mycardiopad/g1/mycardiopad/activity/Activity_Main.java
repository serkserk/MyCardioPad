package com.mycardiopad.g1.mycardiopad.activity;


import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mycardiopad.g1.mycardiopad.R;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Compte;
import com.mycardiopad.g1.mycardiopad.database._Compte;
import com.mycardiopad.g1.mycardiopad.fragment.Fragment_Historique_Host;
import com.mycardiopad.g1.mycardiopad.fragment.Fragment_LancerActivites;
import com.mycardiopad.g1.mycardiopad.fragment.Fragment_MonProgrammeEtVideo_Host;
import com.mycardiopad.g1.mycardiopad.fragment.Fragment_Profile;
import com.mycardiopad.g1.mycardiopad.fragment.Fragment_Reglages;
import com.mycardiopad.g1.mycardiopad.fragment.Fragment_Success;
import com.mycardiopad.g1.mycardiopad.util.CustomToast;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Ecran utilisateur principal  <br/>
 * C'est dans cet écran que sont instancié les différents sous-menus  <br/>
 */
@SuppressWarnings("deprecation")
public class Activity_Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    public Fragment fragment = null;
    String title;
    public boolean isSessionEnregistrement;
    private boolean cancel;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Affiche la view correspondant à l'activité
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); //Mise en place de cette ActionBar


        //Mise en place de l'interface Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer != null){
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();
        }

        //Représente le menu, il est implémenter par un fichier xml
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if(navigationView != null){
            navigationView.setNavigationItemSelectedListener(this);
            //Récupération du Header du menu
            View headerLayout =
                    navigationView.getHeaderView(0);

            TextView utilisateur = (TextView) headerLayout.findViewById(R.id.utilisateurDrawer);
            TextView email = (TextView) headerLayout.findViewById(R.id.emailDrawer);
            CircleImageView imgUtilisateur = (CircleImageView) headerLayout.findViewById(R.id.imageViewFixeDrawer);


            MyDBHandler_Compte dbHandler_compte = new MyDBHandler_Compte(this,"",null,1);

            //Création et affichage de la notification de bienvenue
            new CustomToast(getApplicationContext(),"Bienvenue " +
                    dbHandler_compte.getCompte(0).get_prenom() + " !");


            //Mise en place des données dans le header
            if(dbHandler_compte.numberLine() == 1){
                _Compte compte = dbHandler_compte.lastRowCompte();
                // Log.e("FileImage",compte.get_path_photo());
                utilisateur.setText(compte.get_nom() + " " + compte.get_prenom());
                email.setText(compte.get_email());
                File photo = new File(getApplicationContext().getExternalFilesDir(null), "user.jpg");
                if(photo.exists()) {
                    Bitmap photo_image = BitmapFactory.decodeFile(photo.getAbsolutePath());
                    imgUtilisateur.setImageBitmap(photo_image);
                }
                else
                    imgUtilisateur.setBackgroundResource(R.mipmap.ic_launcher);
            }else
            {
                utilisateur.setText(R.string.user_default);
                email.setText(R.string.user_email_deault);
                imgUtilisateur.setBackgroundResource(R.mipmap.ic_launcher);
            }

        }

        //Affichage de la vue 1
        if (savedInstanceState != null) {
            //Restauration de l'instance du fragment
            displayView(fragment = getSupportFragmentManager().getFragment(
                    savedInstanceState, "mContent"));
        }else{
            displayView(R.id.nav_session_enregistrement);
        }
    }

    /**
     * Quand le bouton retour est push sur Android, dans le cas ou le Drawer est ouvert, il est refermé.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer != null){
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
        }

    }




    /**
     * Création du menu d'option, via un xml
     * Dans notre cas, j'ai commenté le contenu du XML, à voir pour des options
     * @param menu  le menu de l'application
     * @return retourne toujour vrai
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * Quand un item du menu est sélectionné. On appel à la fonction displayView
     * @param item quand un item est selectionner dans le menu
     * @return toujours vrai
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        cancel = false;
        if(fragment != null){
            if(isSessionEnregistrement && !fragment.isVisible()){   // dans le cas ou le fragment n'est pas visible,
                // mais que le précédent est bien issue de ce fragment
                uneSessionEstEnCours(id);
            }else{
                displayView(id);
            }
        }else{
            displayView(id);
        }
        return true;
    }

    /**
     * Affiche un fragment en fonction de la ressource
     * @param viewId l'identifiant à afficher
     */
    public void displayView(int viewId) {

        title = getString(R.string.app_name);

        if(!cancel){
            switch (viewId) {
                case R.id.nav_session_enregistrement:
                    fragment = new Fragment_LancerActivites();
                    title  = "Session d'enregistrement";
                    isSessionEnregistrement = true;

                    break;
                case R.id.nav_profil:
                    fragment = new Fragment_Profile();
                    title = "Profil";
                    isSessionEnregistrement = false;
                    break;
                case R.id.nav_historique:
                    fragment = new Fragment_Historique_Host();
                    title = "Historique";
                    isSessionEnregistrement = false;
                    break;
                case R.id.nav_monprogramme:
                    fragment = new Fragment_MonProgrammeEtVideo_Host();
                    title = "Mon programme";
                    isSessionEnregistrement = false;
                    break;
                case R.id.nav_succes:
                    fragment = new Fragment_Success();
                    title = "Succès";
                    isSessionEnregistrement = false;
                    break;
                case R.id.nav_reglages:
                    fragment = new Fragment_Reglages();
                    title = "Réglages";
                    isSessionEnregistrement = false;
                    break;
            }
        }

        // Dans le cas ou le fragment est différent de null, alors on peut faire un changement de fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }
        // On place le nouveau titre dans la toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
        //On ferme le drawer
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer != null)
              drawer.closeDrawer(GravityCompat.START);

    }

    /**
     * Dans le cas ou une session est en cours
     * Un dialog est afficher pour annuler ou non la session
     * @param id l'id de la nouvelle vue
     */
    private void uneSessionEstEnCours(final int id) {
        new AlertDialog.Builder(this)
                .setTitle("Session")
                .setMessage("Voulez vous annuler la session en cours?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((Fragment_LancerActivites) fragment).cancelSession();
                        displayView(id);

                        NotificationManager notificationManager =
                                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                        notificationManager.cancelAll();
                        startActivity(new Intent(Activity_Main.this, Activity_Feedback.class));
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                         drawer.closeDrawer(GravityCompat.START);
                    }
                })
                .setIcon(getResources().getDrawable(R.mipmap.ic_launcher))
                .show();
    }

    /**
     * Affichage du fragment
     * @param fragment le fragment à afficher
     */
    public void displayView(Fragment fragment){
        // Dans le cas ou le fragment est différent de null, alors on peut faire un changement de fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();

            // On place le nouveau titre dans la toolbar
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(fragment.toString());
            }
            //On ferme le drawer
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if(drawer != null)
                drawer.closeDrawer(GravityCompat.START);
        }
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


}
