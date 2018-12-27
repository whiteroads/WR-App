package com.whiteroads.application;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.whiteroads.library.StartService;
import com.whiteroads.library.data.UserDataWrapper;
import com.whiteroads.library.interfaces.DataEventListener;

import java.util.List;

public class SensorsFragment extends Fragment implements DataEventListener,OnMapReadyCallback, PermissionsListener {

    private TextView accel, speed, gyroscope, linaccel, location, rotation, light, proxim, steps, orientation;
    private BroadcastReceiver broadcastReceiver;
    private Button stopCapture;
    private Intent service, sensors;
    private PendingIntent pIntent;
    private AlarmManager alarm;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for getActivity() fragment
        Mapbox.getInstance(getActivity(), "pk.eyJ1Ijoid2hpdGVyb2FkcyIsImEiOiJjanB6bGJmM3AwYzJnM3hxajZoZHZuZmgxIn0.QP_yzqDa3feWJr1boDmAGw");
        View view = inflater.inflate(R.layout.fragment_sensors, container, false);
        try {
            accel = (TextView) view.findViewById(R.id.acceleration);
            linaccel = (TextView) view.findViewById(R.id.linacceleration);
            speed = (TextView) view.findViewById(R.id.speed);
            gyroscope = (TextView) view.findViewById(R.id.gyroscope);
            location = (TextView) view.findViewById(R.id.location);
            rotation = (TextView) view.findViewById(R.id.rotation);
            light = (TextView) view.findViewById(R.id.light);
            proxim = (TextView) view.findViewById(R.id.proxim);
            steps = (TextView) view.findViewById(R.id.steps);
            orientation = (TextView) view.findViewById(R.id.orient);
            stopCapture = (Button) view.findViewById(R.id.stopCapture);
            StartService startService = new StartService(getActivity());
            startService.registerForDataUpdates(this);
            stopCapture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        ((HomeActivity)getActivity()).startService.stopAllServices();
                        stopCapture.setVisibility(View.GONE);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        enableLocationComponent();
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getActivity())) {

            // Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            // Activate
            locationComponent.activateLocationComponent(getActivity());

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        //
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent();
        } else {

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapView.onDestroy();
    }

    @Override
    public void onAccelChange(String value) {
        if (!value.equalsIgnoreCase("")) {
            accel.setText(value);
        }
    }

    @Override
    public void onLinAccelChange(String value) {
        if (!value.equalsIgnoreCase("")) {
            linaccel.setText(value);
        }
    }

    @Override
    public void onSpeedChange(String value) {
        if (!value.equalsIgnoreCase("")) {
            speed.setText(value);
        }
    }

    @Override
    public void onLatLonChange(String value) {
        if (!value.equalsIgnoreCase("")) {
            location.setText(value);
        }
    }

    @Override
    public void onGyroChange(String value) {
        if (!value.equalsIgnoreCase("")) {
            gyroscope.setText(value);
        }
    }

    @Override
    public void onStepsChange(String value) {
        if (!value.equalsIgnoreCase("")) {
            steps.setText(value);
        }
    }

    @Override
    public void onProxiChange(String value) {
        if (!value.equalsIgnoreCase("")) {
            proxim.setText(value);
        }
    }

    @Override
    public void onRotationChange(String value) {
        if (!value.equalsIgnoreCase("")) {
            rotation.setText(value);
        }
    }

    @Override
    public void onOrientChange(String value) {
        if (!value.equalsIgnoreCase("")) {
            orientation.setText(value);
        }
    }

    @Override
    public void onLightChange(String value) {
        if (!value.equalsIgnoreCase("")) {
            light.setText(value);
        }
    }

    @Override
    public void onScoreChange(int value) {

    }

    @Override
    public void onAddressChange(String value) {

    }
}
