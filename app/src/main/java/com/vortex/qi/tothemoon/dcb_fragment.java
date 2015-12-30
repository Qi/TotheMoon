package com.vortex.qi.tothemoon;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by qw on 12/29/2015.
 */
public class dcb_fragment extends android.app.Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dcb, container, false);
        return view;
    }

    public void selectedBalls(){
        TextView textView1 = (TextView) getView().findViewById(R.id.dcb_section_1);
        textView1.setText(((MainActivity)getActivity()).generateNum(33, 6, true));
        TextView textView2 = (TextView) getView().findViewById(R.id.dcb_section_2);
        textView2.setText(((MainActivity)getActivity()).generateNum(16, 1, true));
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!getUserVisibleHint())
        {
            return;
        }

        MainActivity mainActivity = (MainActivity)getActivity();
        mainActivity.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Successfully generated!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                selectedBalls();
            }
        });
    }
}
