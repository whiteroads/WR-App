package com.whiteroads.library.services;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.whiteroads.library.constants.IntentConstants;
import com.whiteroads.library.constants.NetworkConstants;
import com.whiteroads.library.data.SensorsDataWrapper;
import com.whiteroads.library.data.UserDataWrapper;
import com.whiteroads.library.model.CaptureModel;
import com.whiteroads.library.networks.NetworksCalls;
import com.whiteroads.library.roomdb.CommonDBTable;
import com.whiteroads.library.roomdb.SensorsDatabase;
import com.whiteroads.library.threads.ThreadManager;
import com.whiteroads.library.utils.CommonMethods;
//import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
//import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SensorService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accel, gyroscope, linearAccel, rotation, temp, orientation, proximity, light;
    private CaptureModel captureModel;
    private boolean firstLoad = true;
    private List<Sensor> listening;
    private long timestamp;
    //    private FirebaseRemoteConfig firebaseRemoteConfig;
    private Intent intent;

    @Override

    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            super.onStartCommand(intent, flags, startId);
            if (!UserDataWrapper.getInstance(getApplicationContext()).isServiceStopped()) {
                this.intent = intent;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //createNotificationChannel();
                    startForeground(123457, NotificationSingleton.getObject().getBuilder());
                }
                new StartAsync().execute();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    stopForeground(true);
                } else {
                    stopService(intent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

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
                channel.setShowBadge(false);
                channel.setLightColor(Color.BLUE);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.createNotificationChannel(channel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void unregisterAllListeners() {
        try {
            if (rotation != null) {
                sensorManager.unregisterListener(this, rotation);
            }
            if (accel != null) {
                sensorManager.unregisterListener(this, accel);
            }
            if (gyroscope != null) {
                sensorManager.unregisterListener(this, gyroscope);
            }
            if (linearAccel != null) {
                sensorManager.unregisterListener(this, linearAccel);
            }
            if (light != null) {
                sensorManager.unregisterListener(this, light);
            }
            if (temp != null) {
                sensorManager.unregisterListener(this, temp);
            }
            if (orientation != null) {
                sensorManager.unregisterListener(this, orientation);
            }
            if (proximity != null) {
                sensorManager.unregisterListener(this, proximity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startCaptruingSensorsData() {
        try {
            if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                listening.add(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
            } else {
                captureModel.setAccelerometer_X("NA");
                captureModel.setAccelerometer_Y("NA");
                captureModel.setAccelerometer_Z("NA");
            }
            if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
                gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
                listening.add(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
            } else {
                captureModel.setGyroscope_X("NA");
                captureModel.setGyroscope_Y("NA");
                captureModel.setGyroscope_Z("NA");
            }
            if (sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null) {
                linearAccel = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
                listening.add(sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION));
            } else {
                captureModel.setLinearAcceleration_X("NA");
                captureModel.setLinearAcceleration_Y("NA");
                captureModel.setLinearAcceleration_Z("NA");
            }
            if (sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null) {
                rotation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
                listening.add(sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR));
            } else {
                captureModel.setRotationVector_X("NA");
                captureModel.setRotationVector_Y("NA");
                captureModel.setRotationVector_Z("NA");
            }
            if (sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE) != null) {
                temp = sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE);
                listening.add(sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE));
            } else {
                captureModel.setTemperature("NA");
            }
            if (sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION) != null) {
                orientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
                listening.add(sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION));
            } else {
                captureModel.setOrientation_X("NA");
                captureModel.setOrientation_Y("NA");
                captureModel.setOrientation_Z("NA");
            }
            if (sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null) {
                proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
                listening.add(sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY));
            } else {
                captureModel.setProximity("NA");
            }
            if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
                light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
                listening.add(sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT));
            } else {
                captureModel.setLight("NA");
            }
            if (listening.size() == 0) {
                SensorsDataWrapper.getInstance(getApplicationContext()).saveCacheData(new Gson().toJson(captureModel));
                ThreadManager.getInstance().addToQue(new UploadCaptureData());
            }
            if (accel != null) {
                sensorManager.registerListener(this, accel, 0);
            }
            if (gyroscope != null) {
                sensorManager.registerListener(this, gyroscope, 0);
            }
            if (linearAccel != null) {
                sensorManager.registerListener(this, linearAccel, 0);
            }
            if (rotation != null) {
                sensorManager.registerListener(this, rotation, 0);
            }
            if (temp != null) {
                sensorManager.registerListener(this, temp, 0);
            }
            if (light != null) {
                sensorManager.registerListener(this, light, 0);
            }
            if (orientation != null) {
                sensorManager.registerListener(this, orientation, 0);
            }
            if (proximity != null) {
                sensorManager.registerListener(this, proximity, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void onSensorChanged(SensorEvent event) {
        try {
            if (firstLoad) {
                captureCommonInfo();
                firstLoad = false;
            }
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                captureModel.setAccelerometer_X(String.valueOf(event.values[0]));
                captureModel.setAccelerometer_Y(String.valueOf(event.values[1]));
                captureModel.setAccelerometer_Z(String.valueOf(event.values[2]));
                sensorManager.unregisterListener(this, accel);
                Intent intent = new Intent("CapturedDataAccel");
                intent.putExtra(IntentConstants.accelX, String.valueOf(event.values[0]));
                intent.putExtra(IntentConstants.accelY, String.valueOf(event.values[1]));
                intent.putExtra(IntentConstants.accelZ, String.valueOf(event.values[2]));
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                listening.remove(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
            }

            if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                captureModel.setGyroscope_X(String.valueOf(event.values[0]));
                captureModel.setGyroscope_Y(String.valueOf(event.values[1]));
                captureModel.setGyroscope_Z(String.valueOf(event.values[2]));
                Intent intent = new Intent("CapturedDataAccel");
                intent.putExtra(IntentConstants.GyroX, String.valueOf(event.values[0]));
                intent.putExtra(IntentConstants.GyroY, String.valueOf(event.values[1]));
                intent.putExtra(IntentConstants.GyroZ, String.valueOf(event.values[2]));
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                sensorManager.unregisterListener(this, gyroscope);
                listening.remove(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
            }

            if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                captureModel.setLinearAcceleration_X(String.valueOf(event.values[0]));
                captureModel.setLinearAcceleration_Y(String.valueOf(event.values[1]));
                captureModel.setLinearAcceleration_Z(String.valueOf(event.values[2]));
                Intent intent = new Intent("CapturedDataAccel");
                intent.putExtra(IntentConstants.LinaccelX, String.valueOf(event.values[0]));
                intent.putExtra(IntentConstants.LinaccelY, String.valueOf(event.values[1]));
                intent.putExtra(IntentConstants.LinaccelZ, String.valueOf(event.values[2]));
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                sensorManager.unregisterListener(this, linearAccel);
                listening.remove(sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION));
            }
            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                captureModel.setRotationVector_X(String.valueOf(event.values[0]));
                captureModel.setRotationVector_Y(String.valueOf(event.values[1]));
                captureModel.setRotationVector_Z(String.valueOf(event.values[2]));
                Intent intent = new Intent("CapturedDataAccel");
                intent.putExtra(IntentConstants.rotationX, String.valueOf(event.values[0]));
                intent.putExtra(IntentConstants.rotationY, String.valueOf(event.values[1]));
                intent.putExtra(IntentConstants.rotationZ, String.valueOf(event.values[2]));
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                sensorManager.unregisterListener(this, rotation);
                listening.remove(sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR));
            }

            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                captureModel.setLight(String.valueOf(event.values[0]));
                Intent intent = new Intent("CapturedDataAccel");
                intent.putExtra(IntentConstants.light, String.valueOf(event.values[0]));
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                sensorManager.unregisterListener(this, light);
                listening.remove(sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT));
            }

            if (event.sensor.getType() == Sensor.TYPE_TEMPERATURE) {
                captureModel.setTemperature(String.valueOf(event.values[0]));
                sensorManager.unregisterListener(this, temp);
                listening.remove(sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE));
            }

            if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                captureModel.setOrientation_X(String.valueOf(event.values[0]));
                captureModel.setOrientation_Y(String.valueOf(event.values[1]));
                captureModel.setOrientation_Z(String.valueOf(event.values[2]));
                Intent intent = new Intent("CapturedDataAccel");
                intent.putExtra(IntentConstants.OrientX, String.valueOf(event.values[0]));
                intent.putExtra(IntentConstants.OrientY, String.valueOf(event.values[1]));
                intent.putExtra(IntentConstants.OrientZ, String.valueOf(event.values[2]));
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                sensorManager.unregisterListener(this, orientation);
                listening.remove(sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION));
            }

            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                captureModel.setProximity(String.valueOf(event.values[0]));
                Intent intent = new Intent("CapturedDataAccel");
                intent.putExtra(IntentConstants.prox, String.valueOf(event.values[0]));
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                sensorManager.unregisterListener(this, proximity);
                listening.remove(sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY));
            }

            if (listening.size() == 0) {
                SensorsDataWrapper.getInstance(getApplicationContext()).saveCacheData(new Gson().toJson(captureModel));
                ThreadManager.getInstance().addToQue(new UploadCaptureData());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void captureCommonInfo() {
        try {
            captureModel.setPing_time(String.valueOf(timestamp));
            captureModel.setUser_fk(UserDataWrapper.getInstance(getApplicationContext()).getUserId());
            captureModel.setUser_email(UserDataWrapper.getInstance(getApplicationContext()).getUserEmail());
            captureModel.setServer_time(String.valueOf(timestamp));
            captureModel.setVehicle_no(UserDataWrapper.getInstance(getApplicationContext()).getVehicleNumber());
            captureModel.setDevice_OO(String.valueOf(CommonMethods.IsConnected(getApplicationContext())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public class UploadCaptureData extends Thread {
        public void run() {
            super.run();
            try {
                if (CommonMethods.IsConnected(getApplicationContext())) {
                    List<CommonDBTable> items = SensorsDatabase.getDatabase(getApplicationContext()).commonDAO().getAllItems();
                    if (items != null && items.size() > 0) {
                        for (CommonDBTable commonDBTable : items) {
                            new NetworksCalls(getApplicationContext()).storeCapturedData(commonDBTable.getValue());
                        }
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    stopForeground(true);
                } else {
                    stopService(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    private void fetchConfig() {
//        try {
//            long cacheExpiration = 0;
//            if (firebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
//                cacheExpiration = 0;
//            }
//            firebaseRemoteConfig.fetch(cacheExpiration).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if (task.isSuccessful()) {
//                        firebaseRemoteConfig.activateFetched();
//                    }
//                    UserDataWrapper.getInstance(getApplicationContext())().saveUploadTimeInMins((int)firebaseRemoteConfig.getLong(NetworkConstants.UploadTimeInMins));
//                    UserDataWrapper.getInstance(getApplicationContext())().saveFrequency((int)firebaseRemoteConfig.getLong(NetworkConstants.FrequencySeconds));
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterAllListeners();
            if (!UserDataWrapper.getInstance(getApplicationContext()).isServiceStopped()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Intent _Intent = new Intent(getApplicationContext(), SensorService.class);
                    final PendingIntent pIntent = PendingIntent.getForegroundService(SensorService.this, 0, _Intent, PendingIntent.FLAG_ONE_SHOT);
                    AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    long interval = UserDataWrapper.getInstance(getApplicationContext()).getFrequency() * 1000 + timestamp;
                    alarm.setExact(AlarmManager.RTC_WAKEUP, interval, pIntent);
                } else {
                    Intent _Intent = new Intent(getApplicationContext(), SensorService.class);
                    final PendingIntent pIntent = PendingIntent.getService(getApplicationContext(), 0, _Intent, PendingIntent.FLAG_ONE_SHOT);
                    AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    long interval = UserDataWrapper.getInstance(getApplicationContext()).getFrequency() * 1000 + timestamp;
                    alarm.set(AlarmManager.RTC_WAKEUP, interval, pIntent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class StartAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                captureModel = new CaptureModel();
                listening = new ArrayList<>();
                Log.v("MSG", "Capturing Data Now");
                timestamp = Calendar.getInstance().getTimeInMillis();
                sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                firstLoad = true;
                if (!UserDataWrapper.getInstance(getApplicationContext()).isServiceStopped()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Intent _Intent = new Intent(getApplicationContext(), SensorService.class);
                        final PendingIntent pIntent = PendingIntent.getForegroundService(SensorService.this, 0, _Intent, PendingIntent.FLAG_ONE_SHOT);
                        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        long interval = UserDataWrapper.getInstance(getApplicationContext()).getFrequency() * 1000 + timestamp;
                        alarm.setExact(AlarmManager.RTC_WAKEUP, interval, pIntent);
                    } else {
                        Intent _Intent = new Intent(getApplicationContext(), SensorService.class);
                        final PendingIntent pIntent = PendingIntent.getService(getApplicationContext(), 0, _Intent, PendingIntent.FLAG_ONE_SHOT);
                        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        long interval = UserDataWrapper.getInstance(getApplicationContext()).getFrequency() * 1000 + timestamp;
                        alarm.set(AlarmManager.RTC_WAKEUP, interval, pIntent);
                    }
                }
                unregisterAllListeners();
                startCaptruingSensorsData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
//            try {
//                firebaseRemoteConfig = FirebaseRemoteConfig.getInstance(getApplicationContext())();
//                FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(BuildConfig.DEBUG).build();
//                firebaseRemoteConfig.setConfigSettings(configSettings);
//                firebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
//                fetchConfig();
//            }catch (Exception e){
//                e.printStackTrace();
//            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}