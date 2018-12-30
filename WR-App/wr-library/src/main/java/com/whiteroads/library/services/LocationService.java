package com.whiteroads.library.services;


import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.whiteroads.library.constants.IntentConstants;
import com.whiteroads.library.constants.NetworkConstants;
import com.whiteroads.library.data.LocationDataWrapper;
import com.whiteroads.library.data.UserDataWrapper;
import com.whiteroads.library.model.CaptureModel;
import com.whiteroads.library.networks.NetworksCalls;
import com.whiteroads.library.roomdb.CommonDBTable;
import com.whiteroads.library.roomdb.LocationDatabase;
import com.whiteroads.library.threads.ThreadManager;
import com.whiteroads.library.utils.CommonMethods;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

/**
 *
 */
public class LocationService extends Service implements SensorEventListener {

    boolean checkGPS = false;
    boolean checkNetwork = false;
    boolean canGetLocation = false;
    protected LocationManager locationManager;
    private CaptureModel captureModel, captureModelSteps;
    private long timestamp, timestampSteps;
    private List<CaptureModel> list;
    private List<CaptureModel> listSteps;
    private Notification.Builder builder;
    private LocationRequest mLocationRequest;
    private SensorManager sensorManager;
    private Sensor steps;
    private String callState = "";

    private static final int UPLOAD_STEPS = 100;
    private static final int UPLOAD_LOCATION = 101;
    private static final int UPLOAD_LOCATION_IN_BATCH = 102;
    private ActivityRecognitionClient mActivityRecognitionClient;
    private Intent intent;

    /**
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            super.onStartCommand(intent, flags, startId);
            if(!UserDataWrapper.getInstance(getApplicationContext()).isServiceStopped()) {
                this.intent = intent;
                if (!UserDataWrapper.getInstance(getApplicationContext()).isServiceStopped()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && builder == null) {
                        //createNotificationChannel();
                        startForeground(123457, NotificationSingleton.getObject().getBuilder());
                    }
                    new StartAsync().execute();
                }
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    stopForeground(true);
                }else{
                    stopService(intent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    /**
     *
     */
    private void createNotificationChannel() {
        try {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "Whiteroads Notifications";
                String description = "Persistent Notification for Data capturing";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel(NetworkConstants.ChannelId, name, importance);
                channel.setDescription(description);
                channel.setSound(null, null);
                channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                channel.setLightColor(Color.BLUE);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.createNotificationChannel(channel);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public void startCapturingSensorsData() {
        try {
            captureModelSteps = new CaptureModel();
            listSteps = new ArrayList<>();
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            timestampSteps = Calendar.getInstance().getTimeInMillis();
            captureModelSteps.setUser_fk(UserDataWrapper.getInstance(getApplicationContext()).getUserId());
            captureModelSteps.setUser_email(UserDataWrapper.getInstance(getApplicationContext()).getUserEmail());
            captureModelSteps.setVehicle_no(UserDataWrapper.getInstance(getApplicationContext()).getVehicleNumber());
            if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null
                    && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                steps = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            } else {
                captureModelSteps.setPedometer("NA");
            }
            if (steps != null) {
                sensorManager.registerListener(this, steps, SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
            } else {
                captureModelSteps.setPing_time(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                captureModelSteps.setServer_time(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                listSteps.add(captureModelSteps);
                if (Calendar.getInstance().getTimeInMillis() - timestampSteps > (UserDataWrapper.getInstance(getApplicationContext()).getUploadTime() * 1000)) {
                    ThreadManager.getInstance().addToQue(new UploadCaptureData(UPLOAD_STEPS));
                    timestampSteps = Calendar.getInstance().getTimeInMillis();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param event
     */
    @Override
    public synchronized void onSensorChanged(SensorEvent event) {
        try {
            if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                captureModelSteps.setPedometer(String.valueOf(event.values[0]));
                captureModelSteps.setPing_time(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                captureModelSteps.setServer_time(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                listSteps.add(captureModelSteps);
                if (Calendar.getInstance().getTimeInMillis() - timestampSteps > (UserDataWrapper.getInstance(getApplicationContext()).getUploadTime() * 1000)) {
                    ThreadManager.getInstance().addToQue(new UploadCaptureData(UPLOAD_STEPS));
                    timestampSteps = Calendar.getInstance().getTimeInMillis();
                }
                Intent intent = new Intent("CapturedDataElse");
                intent.putExtra(IntentConstants.steps, String.valueOf(String.valueOf(event.values[0])));
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param sensor
     * @param accuracy
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     *
     */
    private void getLocation() {
        try {

            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //Restarting service again
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    Intent sensors = new Intent(this, LocationService.class);
                    PendingIntent pIntent = PendingIntent.getService(this, 0, sensors, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    alarm.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis()+5000, pIntent);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Intent sensors = new Intent(this, LocationService.class);
                        PendingIntent pIntent = PendingIntent.getForegroundService(this, 0, sensors, PendingIntent.FLAG_UPDATE_CURRENT);
                        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        alarm.setExact(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis()+5000, pIntent);
                    }
                }
                return;
            }

            captureModel = new CaptureModel();
            list = new ArrayList<>();
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            timestamp = Calendar.getInstance().getTimeInMillis();
            captureModel.setUser_fk(UserDataWrapper.getInstance(getApplicationContext()).getUserId());
            captureModel.setUser_email(UserDataWrapper.getInstance(getApplicationContext()).getUserEmail());
            captureModel.setVehicle_no(UserDataWrapper.getInstance(getApplicationContext()).getVehicleNumber());

            // get GPS status
            checkGPS = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // get network provider status
            checkNetwork = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!checkGPS && !checkNetwork) {
                increaseScore();
                captureModel.setLat("NA");
                captureModel.setLon("NA");
                captureModel.setSpeed("NA");
                captureModel.setPing_time(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                captureModel.setServer_time(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                captureModel.setCallSatate(callState);
                captureModel.setScorePingTime(String.valueOf(UserDataWrapper.getInstance(getApplicationContext()).getLastUploadTimeRandomScore()));
                captureModel.setScore(String.valueOf(UserDataWrapper.getInstance(getApplicationContext()).getRandomScore()));
                list.add(captureModel);
                LocationDataWrapper.getInstance(getApplicationContext()).saveCacheData(new Gson().toJson(list));
                ThreadManager.getInstance().addToQue(new UploadCaptureData(UPLOAD_LOCATION));
                list = new ArrayList<>();
                timestamp = Calendar.getInstance().getTimeInMillis();

                //Restarting service again
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    stopForeground(true);
                }else{
                    stopService(intent);
                }


            } else {
                getLastLocation();
                startLocationUpdates();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     *
     */
    public class UploadCaptureData extends Thread {

        private int requestType;
        private LocationResult locationResult;

        public UploadCaptureData(int requestType) {
            this.requestType = requestType;
        }

        public UploadCaptureData(int requestType, LocationResult locationResult) {
            this.requestType = requestType;
            this.locationResult = locationResult;
        }

        public void run() {
            super.run();
            try {
                if (CommonMethods.IsConnected(getApplicationContext())) {
                    if (requestType == UPLOAD_LOCATION_IN_BATCH && locationResult != null && locationResult.getLocations() != null
                            && locationResult.getLocations().size() > 0) {
                        for (Location location : locationResult.getLocations()) {
                            increaseScore();
                            captureModel.setLat(String.valueOf(location.getLatitude()));
                            captureModel.setLon(String.valueOf(location.getLongitude()));
                            captureModel.setSpeed(String.valueOf(location.getSpeed()));
                            captureModel.setPing_time(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                            captureModel.setServer_time(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                            captureModel.setCallSatate(callState);
                            captureModel.setScorePingTime(String.valueOf(UserDataWrapper.getInstance(getApplicationContext()).getLastUploadTimeRandomScore()));
                            captureModel.setScore(String.valueOf(UserDataWrapper.getInstance(getApplicationContext()).getRandomScore()));
                            captureModel.setActivityType(UserDataWrapper.getInstance(getApplicationContext()).getLastActivityRecorded());
                            captureModel.setActivityConfidence(UserDataWrapper.getInstance(getApplicationContext()).getLastActivityRecordedConfidence());
                            list.add(captureModel);

                            //Batching list of size 10 because to handle out of memory errors
                            if (list.size() > 10) {
                                LocationDataWrapper.getInstance(getApplicationContext()).saveCacheData(new Gson().toJson(list));
                                list.clear();
                            }

                            Intent intent = new Intent("CapturedDataElse");
                            intent.putExtra(IntentConstants.lat, String.valueOf(location.getLatitude()));
                            intent.putExtra(IntentConstants.lon, String.valueOf(location.getLongitude()));
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                            Intent intentSpeed = new Intent("CapturedDataSpeed");
                            intentSpeed.putExtra(IntentConstants.speed, String.valueOf(location.getSpeed()));
                            intentSpeed.putExtra(IntentConstants.address, getAddress(location));
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentSpeed);
                        }
                        if (list.size() > 0) {
                            LocationDataWrapper.getInstance(getApplicationContext()).saveCacheData(new Gson().toJson(list));
                            list.clear();
                        }
                        if (Calendar.getInstance().getTimeInMillis() - timestamp > (UserDataWrapper.getInstance(getApplicationContext()).getUploadTime() * 1000)) {
                            timestamp = Calendar.getInstance().getTimeInMillis();
                            List<CommonDBTable> items = LocationDatabase.getDatabase(getApplicationContext()).commonDAO().getAllItems();
                            if (items != null && items.size() > 0) {
                                for (CommonDBTable commonDBTable : items) {
                                    new NetworksCalls(getApplicationContext()).storeCapturedDataLocations(commonDBTable.getValue());
                                }
                            }
                        }
                    }
                    if (requestType == UPLOAD_LOCATION) {
                        List<CommonDBTable> items = LocationDatabase.getDatabase(getApplicationContext()).commonDAO().getAllItems();
                        if (items != null && items.size() > 0) {
                            for (CommonDBTable commonDBTable : items) {
                                new NetworksCalls(getApplicationContext()).storeCapturedDataLocations(commonDBTable.getValue());
                            }
                        }
                    }
                    if (requestType == UPLOAD_STEPS) {
                        new NetworksCalls(getApplicationContext()).storeCapturedDataSteps(new Gson().toJson(listSteps));
                        listSteps = new ArrayList<>();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     */
    protected void startLocationUpdates() {

        try {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            long interval = UserDataWrapper.getInstance(getApplicationContext()).getFrequency() * 1000;
            mLocationRequest.setInterval(interval);
            mLocationRequest.setFastestInterval(1000);
            mLocationRequest.setMaxWaitTime(UserDataWrapper.getInstance(getApplicationContext()).getFrequency() + 10000);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            LocationSettingsRequest locationSettingsRequest = builder.build();

            SettingsClient settingsClient = LocationServices.getSettingsClient(this);
            settingsClient.checkLocationSettings(locationSettingsRequest);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                increaseScore();
                captureModel.setLat(String.valueOf("Permission Denied"));
                captureModel.setLon(String.valueOf("Permission Denied"));
                captureModel.setSpeed(String.valueOf("Permission Denied"));
                captureModel.setPing_time(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                captureModel.setServer_time(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                captureModel.setCallSatate(callState);
                captureModel.setScorePingTime(String.valueOf(UserDataWrapper.getInstance(getApplicationContext()).getLastUploadTimeRandomScore()));
                captureModel.setScore(String.valueOf(UserDataWrapper.getInstance(getApplicationContext()).getRandomScore()));
                list.add(captureModel);
                LocationDataWrapper.getInstance(getApplicationContext()).saveCacheData(new Gson().toJson(list));
                ThreadManager.getInstance().addToQue(new UploadCaptureData(UPLOAD_LOCATION));
                list = new ArrayList<>();
                timestamp = Calendar.getInstance().getTimeInMillis();
                return;
            }
            new Handler(getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (ActivityCompat.checkSelfPermission(LocationService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LocationService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    }
                    getFusedLocationProviderClient(LocationService.this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(LocationResult locationResult) {
                                    // do work here
                                    //do not execute large computation here it runs on main thread
                                    requestActivityUpdatesButtonHandler();
                                    ThreadManager.getInstance().addToQue(new UploadCaptureData(UPLOAD_LOCATION_IN_BATCH, locationResult));
                                }
                            },
                            Looper.myLooper());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public void increaseScore() {
        try {
            if ((int) ((Calendar.getInstance().getTimeInMillis() - UserDataWrapper.getInstance(getApplicationContext()).getLastUploadTimeRandomScore()) / (1000 * 60 * 60)) > 4) {
                UserDataWrapper.getInstance(getApplicationContext()).saveRandomScore(Math.min(UserDataWrapper.getInstance(getApplicationContext()).getRandomScore() + (float) 1.8, 100));
                UserDataWrapper.getInstance(getApplicationContext()).saveLastUploadTimeRandomScore(Calendar.getInstance().getTimeInMillis());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public void getLastLocation() {
        try {
            FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                captureModel.setLat(String.valueOf("Permission Denied"));
                captureModel.setLon(String.valueOf("Permission Denied"));
                captureModel.setSpeed(String.valueOf("Permission Denied"));
                captureModel.setPing_time(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                captureModel.setServer_time(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                list.add(captureModel);
                LocationDataWrapper.getInstance(getApplicationContext()).saveCacheData(new Gson().toJson(list));
                ThreadManager.getInstance().addToQue(new UploadCaptureData(UPLOAD_LOCATION));
                list = new ArrayList<>();
                timestamp = Calendar.getInstance().getTimeInMillis();
                return;
            }
            locationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // GPS location can be null if GPS is switched off
                            if (location != null) {
                                onLocationChanged(location);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("MapDemoActivity", "Error trying to get last GPS location");
                            e.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     *
     * @param location
     */
    public void onLocationChanged(Location location) {
        try {
            captureModel.setLat(String.valueOf(location.getLatitude()));
            captureModel.setLon(String.valueOf(location.getLongitude()));
            captureModel.setSpeed(String.valueOf(location.getSpeed()));
            captureModel.setPing_time(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            captureModel.setServer_time(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            captureModel.setCallSatate(callState);
            list.add(captureModel);
            increaseScore();
            if (Calendar.getInstance().getTimeInMillis() - timestamp > (UserDataWrapper.getInstance(getApplicationContext()).getUploadTime() * 1000)) {
                LocationDataWrapper.getInstance(getApplicationContext()).saveCacheData(new Gson().toJson(list));
                ThreadManager.getInstance().addToQue(new UploadCaptureData(UPLOAD_LOCATION));
                list = new ArrayList<>();
                timestamp = Calendar.getInstance().getTimeInMillis();
            }

            Intent intent = new Intent("CapturedDataElse");
            intent.putExtra(IntentConstants.lat, String.valueOf(location.getLatitude()));
            intent.putExtra(IntentConstants.lon, String.valueOf(location.getLongitude()));
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

            Intent intentSpeed = new Intent("CapturedDataSpeed");
            intentSpeed.putExtra(IntentConstants.speed, String.valueOf(location.getSpeed()));
            intentSpeed.putExtra(IntentConstants.address, getAddress(location));
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentSpeed);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param location
     * @return
     */
    public String getAddress(Location location) {
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            return addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     *
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            LocationDataWrapper.getInstance(getApplicationContext()).saveCacheData(new Gson().toJson(list));
            ThreadManager.getInstance().addToQue(new UploadCaptureData(UPLOAD_LOCATION));
            ThreadManager.getInstance().addToQue(new UploadCaptureData(UPLOAD_STEPS));
            if(sensorManager!=null) {
                sensorManager.unregisterListener(this, steps);
            }
            //restarting in case of failure
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                Intent sensors = new Intent(this, LocationService.class);
                PendingIntent pIntent = PendingIntent.getService(this, 0, sensors, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarm.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis()+5000, pIntent);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Intent sensors = new Intent(this, LocationService.class);
                    PendingIntent pIntent = PendingIntent.getForegroundService(this, 0, sensors, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    alarm.setExact(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis()+5000, pIntent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public class StartAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            if (steps != null) {
                sensorManager.unregisterListener(LocationService.this, steps);
            }
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                StateListener phoneStateListener = new StateListener();
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            startCapturingSensorsData();
            getLocation();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    /**
     *
     */
    class StateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    callState = "ringing";
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    callState = "running";
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    callState = "idle";
                    break;
            }
        }
    }

    /**
     *
     */
    public void requestActivityUpdatesButtonHandler() {
        mActivityRecognitionClient = new ActivityRecognitionClient(this);
        Task<Void> task = mActivityRecognitionClient.requestActivityUpdates(
                1000,
                getActivityDetectionPendingIntent());

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.v("TAG","Data");
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("TAG","Data");
            }
        });
    }


    /**
     *
     */
    public void removeActivityUpdatesButtonHandler() {
        Task<Void> task = mActivityRecognitionClient.removeActivityUpdates(
                getActivityDetectionPendingIntent());
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {

            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    /**
     * Gets a PendingIntent to be sent for each activity detection.
     */
    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(this, DetectedActivitiesIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}


