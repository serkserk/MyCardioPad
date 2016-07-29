package com.mycardiopad.g1.mycardiopad.activity;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.mycardiopad.g1.mycardiopad.R;
import com.mycardiopad.g1.mycardiopad.adapter.Adapter_Feedback;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Compte;
import com.mycardiopad.g1.mycardiopad.fragment.Fragment_Feedback_1;
import com.mycardiopad.g1.mycardiopad.fragment.Fragment_Feedback_2;

import java.util.List;
import java.util.Vector;

/**
 * Réalisé par Serkan <br/>
 * Feedback de l'utilisateur <br/>
 */

public class Activity_Feedback extends AppCompatActivity {

    Toolbar toolbar;
    ViewPager viewPager;
    List<Fragment> fragments = new Vector<>();
    PagerAdapter pagerAdapter;

    FloatingActionButton nextSend;

    MyDBHandler_Compte dbCompte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_host);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Feedback (1 sur 2)");

        dbCompte = new MyDBHandler_Compte(getApplicationContext(), null, null, 1);

        Fragment feed1 = new Fragment_Feedback_1();
        Fragment feed2 = new Fragment_Feedback_2();
        fragments.add(feed1);
        fragments.add(feed2);

        viewPager = (ViewPager) findViewById(R.id.pager);
        /** set the adapter for ViewPager */
        //viewPager.setAdapter(new SamplePagerAdapter(getSupportFragmentManager()));
        pagerAdapter = new Adapter_Feedback(super.getSupportFragmentManager(), fragments) {
        };
        viewPager.setAdapter(pagerAdapter);

        nextSend = (FloatingActionButton) findViewById(R.id.suivant);
        nextSend.setImageDrawable(ContextCompat.getDrawable(Activity_Feedback.this, R.drawable.ic_navigate_next_white_48dp));
        nextSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(1, true);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (viewPager.getCurrentItem() == 0) {
                    getSupportActionBar().setTitle("Feedback (1 sur 2)");
                    nextSend.setImageDrawable(ContextCompat.getDrawable(Activity_Feedback.this, R.drawable.ic_navigate_next_white_48dp));
                    nextSend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            viewPager.setCurrentItem(1, true);
                        }
                    });

                }
                if (viewPager.getCurrentItem() == 1) {
                    getSupportActionBar().setTitle("Feedback (2 sur 2)");
                    nextSend.setImageDrawable(ContextCompat.getDrawable(Activity_Feedback.this, R.drawable.ic_send_white_48dp));
                    nextSend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(Activity_Feedback.this,
                                    ((Fragment_Feedback_1) fragments.get(0)).getBorg() +
                                            ((Fragment_Feedback_2) fragments.get(1)).getdPoitrine() +
                                            ((Fragment_Feedback_2) fragments.get(1)).getdResp() +
                                            ((Fragment_Feedback_2) fragments.get(1)).getpalpitation() +
                                            ((Fragment_Feedback_2) fragments.get(1)).getfatigue() +
                                            ((Fragment_Feedback_2) fragments.get(1)).getdMusc() +
                                            ((Fragment_Feedback_2) fragments.get(1)).getavis()
                                    , Toast.LENGTH_LONG).show();

                            /*Log.i("Feedback", dPoitrineB);
                            Log.i("Feedback", dRespB);
                            Log.i("Feedback", palpitationB);
                            Log.i("Feedback", fatigueB);
                            Log.i("Feedback", dMuscB);
                            Log.i("Feedback", String.valueOf(borgValue));

                            if (dbCompte.numberLine() > 0) {
                                _Compte compte = dbCompte.lastRowCompte();
                                Log.i("Feedback", compte.get_email());
                                RequestBody formBody = new FormBody.Builder()
                                        .add("email", compte.get_email())
                                        .add("dPoitrineB", dPoitrineB)
                                        .add("dRespB", dRespB)
                                        .add("palpitationB", palpitationB)
                                        .add("fatigueB", fatigueB)
                                        .add("dMuscB", dMuscB)
                                        .add("BORG", String.valueOf(borgValue))
                                        .build();

                                Log.i("Feedback", ServeurURL.ADD_FEEDBACK);
                                Request request = new Request.Builder()
                                        .url(ServeurURL.ADD_FEEDBACK)
                                        .post(formBody)
                                        .build();

                                OkHttpSingleton.getInstance().newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        Log.i("Feedback", "onFailure");
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {

                                        Log.i("Feedback", "onResponse");
                                        JSONObject json;
                                        try {
                                            json = new JSONObject(response.body().string());
                                            Log.i("Feedback", json.toString());
                                            String status = json.getString("status");
                                            Log.i("Feedback", status);

                                            if (status.equals("ok")) {
                                                Log.e("Feedback", "OK");
                                                Toast.makeText(Activity_Feedback.this, "Envoyer", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        } catch (JSONException e) {
                                            Log.e("Feedback", "try catch");
                                            e.printStackTrace();
                                        }


                                    }
                                });
                            }*/
                        }
                    });
                }
            }
        });

    }

}