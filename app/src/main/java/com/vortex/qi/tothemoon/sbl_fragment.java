package com.vortex.qi.tothemoon;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by qw on 12/29/2015.
 */
public class sbl_fragment extends android.app.Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.sbl, container, false);
    }
    public void selectedBalls(){
        TextView textView1 = (TextView) getView().findViewById(R.id.sbl_section_1);
        textView1.setText(((MainActivity)getActivity()).generateNum(1, 35, 5, true));
        TextView textView2 = (TextView) getView().findViewById(R.id.sbl_section_2);
        textView2.setText(((MainActivity)getActivity()).generateNum(1, 12, 2, true));
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
