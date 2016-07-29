package com.mycardiopad.g1.mycardiopad.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.mycardiopad.g1.mycardiopad.R;
import com.mycardiopad.g1.mycardiopad.database.MyDBHandler_Compte;

/**
 * Réalisé par Serkan <br/>
 * Second fragment feedback <br/>
 */

public class Fragment_Feedback_2 extends Fragment {

    CheckBox dPoitrine, dResp, palpitation, fatigue, dMusc;
    String dPoitrineB = "false", dRespB = "false", palpitationB = "false", fatigueB = "false", dMuscB = "false";
    EditText avis;

    MyDBHandler_Compte dbCompte;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment__feedback_2, container, false);

        dbCompte = new MyDBHandler_Compte(getContext(), null, null, 1);

        dPoitrine = (CheckBox) v.findViewById(R.id.douleur_poitrine);
        dResp = (CheckBox) v.findViewById(R.id.diff_respirer);
        palpitation = (CheckBox) v.findViewById(R.id.palpitation);
        fatigue = (CheckBox) v.findViewById(R.id.fatique);
        dMusc = (CheckBox) v.findViewById(R.id.douleur_musculaire);
        avis = (EditText) v.findViewById(R.id.autres);

        dPoitrine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    dPoitrineB = "true";
                } else {
                    dPoitrineB = "false";
                }
            }
        });
        dResp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    dRespB = "true";
                } else {
                    dRespB = "false";
                }
            }
        });
        palpitation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    fatigueB = "true";
                } else {
                    fatigueB = "false";
                }
            }
        });
        fatigue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    palpitationB = "true";
                } else {
                    palpitationB = "false";
                }
            }
        });
        dMusc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    dMuscB = "true";
                } else {
                    dMuscB = "false";
                }
            }
        });

        return v;
    }

    public String getdPoitrine() {
        return dPoitrineB;
    }

    public String getdResp() {
        return dRespB;
    }

    public String getpalpitation() {
        return palpitationB;
    }

    public String getfatigue() {
        return fatigueB;
    }

    public String getdMusc() {
        return dMuscB;
    }

    public String getavis() {
        return avis.getText().toString();
    }
}