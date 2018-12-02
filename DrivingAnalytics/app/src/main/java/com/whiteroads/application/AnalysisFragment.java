package com.analytics.application;

import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.whiteroads.library.StartService;
import com.whiteroads.library.interfaces.DataEventListener;

public class AnalysisFragment extends Fragment implements DataEventListener{
    private TextView speedText,address,score,staticText;
    private BroadcastReceiver broadcastReceiver;
    private ImageView marker;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for getActivity() fragment
        View view = inflater.inflate(R.layout.fragment_analysis, container, false);
        try {
            speedText = (TextView) view.findViewById(R.id.speedText);
            score = (TextView) view.findViewById(R.id.score);
            address = (TextView) view.findViewById(R.id.address);
            marker = (ImageView) view.findViewById(R.id.marker);
            speedText.setVisibility(View.GONE);
            StartService startService = new StartService(getActivity());
            startService.registerForDataUpdates(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onAccelChange(String value) {

    }

    @Override
    public void onLinAccelChange(String value) {

    }

    @Override
    public void onSpeedChange(String value) {

    }

    @Override
    public void onLatLonChange(String value) {

    }

    @Override
    public void onGyroChange(String value) {

    }

    @Override
    public void onStepsChange(String value) {

    }

    @Override
    public void onProxiChange(String value) {

    }

    @Override
    public void onRotationChange(String value) {

    }

    @Override
    public void onOrientChange(String value) {

    }

    @Override
    public void onLightChange(String value) {

    }

    @Override
    public void onScoreChange(int value) {
        if(value<65){
            score.setBackgroundResource(R.drawable.circle);
        }else if(value<75){
            score.setBackgroundResource(R.drawable.circle_yellow);
        }else{
            score.setBackgroundResource(R.drawable.circle_gree);
        }
        score.setText(Html.fromHtml("<big>"+value+"</big><br><small>out of<br>100<small>"));
    }

    @Override
    public void onAddressChange(String value) {
        address.setText(value);
    }
}
