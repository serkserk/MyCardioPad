package com.mycardiopad.g1.mycardiopad.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.TextView;

import com.mycardiopad.g1.mycardiopad.R;

/**
 * Réalisé par nicolassalleron le 22/01/16.  <br/>
 * Pose une question à l'utilisateur précisée dans l'extra   <br/>
 */
public class Activity_Question extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_question);

        WatchViewStub stub = (WatchViewStub) findViewById(R.id.confirmation_watch_view_stub);

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub watchViewStub) {

                CircledImageView bValid = (CircledImageView) findViewById(R.id.ok_btn);
                CircledImageView bCancel = (CircledImageView) findViewById(R.id.cancel_btn);
                TextView description = (TextView) findViewById(R.id.description);
                Intent i = getIntent();
                if(i.getExtras()!=null) {
                    //noinspection ConstantConditions
                    description.setText(i.getExtras().get("result").toString());
                }
                bValid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setResult(Activity.RESULT_OK, new Intent());
                        finish();
                    }
                });
                bCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setResult(Activity.RESULT_CANCELED, new Intent());
                        finish();
                    }
                });
            }
        });

    }

}
