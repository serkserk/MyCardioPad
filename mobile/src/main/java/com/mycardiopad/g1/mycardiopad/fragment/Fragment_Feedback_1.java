package com.mycardiopad.g1.mycardiopad.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.mycardiopad.g1.mycardiopad.R;

/**
 * Réalisé par Serkan <br/>
 * Premier fragment feedback <br/>
 */

public class Fragment_Feedback_1 extends Fragment {

    SeekBar borg;
    int borgValue = 5;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment__feedback_1, container, false);

        borg = (SeekBar) v.findViewById(R.id.borg);

        borg.setProgress(0);
        borg.incrementProgressBy(1);
        borg.setMax(10);
        borg.setProgress(5);

        borg.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.i("FEEDBACK", String.valueOf(i));
                borgValue = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        return v;
    }

    public int getBorg() {
        return borgValue;
    }

}
