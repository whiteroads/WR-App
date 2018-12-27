package com.whiteroads.library;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Button;

import com.whiteroads.library.constants.EventBusContext;
import com.whiteroads.library.constants.IntentConstants;
import com.whiteroads.library.constants.NetworkConstants;
import com.whiteroads.library.data.UserDataWrapper;
import com.whiteroads.library.interfaces.DataEventListener;
import com.whiteroads.library.model.UserModel;
import com.whiteroads.library.networks.NetworksCalls;
import com.whiteroads.library.services.LocationService;
import com.whiteroads.library.services.NotificationSingleton;
import com.whiteroads.library.services.SensorService;
import com.whiteroads.library.utils.CommonMethods;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.List;

import de.greenrobot.event.EventBus;

import static android.content.Context.SENSOR_SERVICE;
import static com.whiteroads.library.constants.NetworkConstants.TAG;

public class StartService implements ActivityCompat.PermissionCompatDelegate{

    private BroadcastReceiver broadcastReceiver;
    private Button stopCapture;
    private Intent service, sensors;
    private PendingIntent pIntent;
    private AlarmManager alarm;
    private Activity context;
    public final static int UPLOAD_DATA = 100;
    EventBus eventBus;
    String[] data = new String[5];
    DataEventListener listener;

    public StartService(Activity context) {
        this.context = context;
    }

    public void initializeSDK(String merchantName, String merchantKey, String userId, String vehicleId) {
        data[0] = userId;
        data[1] = merchantName;
        data[2] = "Gender";
        data[3] = vehicleId;
        data[4] = merchantKey;
        if (ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE}, 100);
        }
        basicSetup();

    }

    public void registerForDataUpdates(DataEventListener listener){
        try{
            this.listener = listener;
            registerReceiver();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void registerReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null && intent != null && intent.getAction().equalsIgnoreCase("CapturedDataAccel")) {

                    String accelText = "";
                    if (intent.getStringExtra(IntentConstants.accelX) != null) {
                        accelText += intent.getStringExtra(IntentConstants.accelX) + ", ";
                    }
                    if (intent.getStringExtra(IntentConstants.accelY) != null) {
                        accelText += intent.getStringExtra(IntentConstants.accelY) + ", ";
                    }
                    if (intent.getStringExtra(IntentConstants.accelZ) != null) {
                        accelText += intent.getStringExtra(IntentConstants.accelZ) + ", ";
                    }
                    if(!accelText.equalsIgnoreCase("")) {
                        listener.onAccelChange(accelText);
                    }


                    String linaccelText = "";
                    if (intent.getStringExtra(IntentConstants.LinaccelX) != null) {
                        linaccelText += intent.getStringExtra(IntentConstants.LinaccelX) + ", ";
                    }
                    if (intent.getStringExtra(IntentConstants.LinaccelY) != null) {
                        linaccelText += intent.getStringExtra(IntentConstants.LinaccelY) + ", ";
                    }
                    if (intent.getStringExtra(IntentConstants.LinaccelZ) != null) {
                        linaccelText += intent.getStringExtra(IntentConstants.LinaccelZ) + ", ";
                    }
                    if(!linaccelText.equalsIgnoreCase("")) {
                        listener.onLinAccelChange(linaccelText);
                    }


                    String gyroText = "";
                    if (intent.getStringExtra(IntentConstants.GyroX) != null) {
                        gyroText += intent.getStringExtra(IntentConstants.GyroX) + ", ";
                    }
                    if (intent.getStringExtra(IntentConstants.GyroY) != null) {
                        gyroText += intent.getStringExtra(IntentConstants.GyroY) + ", ";
                    }
                    if (intent.getStringExtra(IntentConstants.GyroZ) != null) {
                        gyroText += intent.getStringExtra(IntentConstants.GyroZ) + ", ";
                    }
                    if(!gyroText.equalsIgnoreCase("")) {
                        listener.onGyroChange(gyroText);
                    }


                    String rotationText = "";
                    if (intent.getStringExtra(IntentConstants.rotationX) != null) {
                        rotationText += intent.getStringExtra(IntentConstants.rotationX) + ", ";
                    }
                    if (intent.getStringExtra(IntentConstants.rotationY) != null) {
                        rotationText += intent.getStringExtra(IntentConstants.rotationY) + ", ";
                    }
                    if (intent.getStringExtra(IntentConstants.rotationZ) != null) {
                        rotationText += intent.getStringExtra(IntentConstants.rotationZ) + ", ";
                    }
                    if(!rotationText.equalsIgnoreCase("")) {
                        listener.onRotationChange(rotationText);
                    }



                    String orientText = "";
                    if (intent.getStringExtra(IntentConstants.OrientX) != null) {
                        orientText += intent.getStringExtra(IntentConstants.OrientX) + ", ";
                    }
                    if (intent.getStringExtra(IntentConstants.OrientY) != null) {
                        orientText += intent.getStringExtra(IntentConstants.OrientY) + ", ";
                    }
                    if (intent.getStringExtra(IntentConstants.OrientZ) != null) {
                        orientText += intent.getStringExtra(IntentConstants.OrientZ) + ", ";
                    }
                    if(!orientText.equalsIgnoreCase("")) {
                        listener.onOrientChange(orientText);
                    }

                    if (intent.getStringExtra(IntentConstants.prox) != null) {
                        listener.onProxiChange(intent.getStringExtra(IntentConstants.prox));
                    }

                    if (intent.getStringExtra(IntentConstants.light) != null) {
                        listener.onLightChange(intent.getStringExtra(IntentConstants.light));
                    }


                }else if (intent.getAction() != null && intent != null && intent.getAction().equalsIgnoreCase("CapturedDataSpeed")) {
                    if (intent.getStringExtra(IntentConstants.address) != null) {
                        listener.onAddressChange(intent.getStringExtra(IntentConstants.address));
                    }
                    listener.onScoreChange((int) UserDataWrapper.getInstance().getRandomScore());
                }else {
                    String coord = "";
                    if (intent.getStringExtra(IntentConstants.lat) != null) {
                        coord += intent.getStringExtra(IntentConstants.lat) + ", ";
                    }
                    if (intent.getStringExtra(IntentConstants.lon) != null) {
                        coord += intent.getStringExtra(IntentConstants.lon) + ", ";
                    }
                    if(!coord.equalsIgnoreCase("")) {
                        listener.onLatLonChange(coord);
                    }
                    if (intent.getStringExtra(IntentConstants.speed) != null) {
                        listener.onSpeedChange(intent.getStringExtra(IntentConstants.speed));
                    }

                    if (intent.getStringExtra(IntentConstants.steps) != null) {
                        listener.onStepsChange(intent.getStringExtra(IntentConstants.steps));
                    }
                }

            }
        };
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, new IntentFilter("CapturedDataAccel"));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, new IntentFilter("CapturedDataElse"));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, new IntentFilter("CapturedDataSpeed"));
    }

    private void basicSetup(){
        try{
            displayLocationSettingsRequest(context);
            UserDataWrapper.getInstance().setIsServicesStopped(false);
            try {
//            try {
//                FirebaseApp.initializeApp(context);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
                eventBus = new EventBus();
                eventBus.register(this);
                if(UserDataWrapper.getInstance().getUserId()<1) {
                    new ConfigureLogin(UPLOAD_DATA, data).start();
                }else{
                    startAllServices();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public boolean stopAllServices(){
        try{
            UserDataWrapper.getInstance().setIsServicesStopped(true);
            if(sensors!=null) {
                context.stopService(sensors);
            }
            if(service!=null) {
                context.stopService(service);
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private void startAllServices() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel();
            }
            UserDataWrapper.getInstance().setIsServicesStopped(false);
            if (ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(context,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE}, 101);
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                sensors = new Intent(context, SensorService.class);
                pIntent = PendingIntent.getService(context, 0, sensors, PendingIntent.FLAG_UPDATE_CURRENT);
                alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarm.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), pIntent);
                service = new Intent(context, LocationService.class);
                context.startService(service);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Notification.Builder builder = new Notification.Builder(context, NetworkConstants.ChannelId)
                            .setContentTitle(context.getString(R.string.app_name))
                            .setSmallIcon(R.drawable.notif_icon)
                            .setOnlyAlertOnce(true)
                            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                                    R.drawable.icon_white))
                            .setContentText("Extracting information from sensors...");

                    Notification notification = builder.build();
                    NotificationSingleton.getObject().setBuilder(notification);
                    sensors = new Intent(context, SensorService.class);
                    pIntent = PendingIntent.getForegroundService(context, 0, sensors, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    alarm.setExact(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), pIntent);
                    service = new Intent(context, LocationService.class);
                    context.startForegroundService(service);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                channel.setShowBadge(true);
                channel.setLightColor(Color.BLUE);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.createNotificationChannel(channel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void displayLocationSettingsRequest(final Activity context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(0);
        locationRequest.setFastestInterval(0);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                            Intent service = new Intent(context, LocationService.class);
                            context.startService(service);
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                Intent service = new Intent(context, LocationService.class);
                                context.startForegroundService(service);
                            }
                        }
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(context, 100);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    @Override
    public boolean requestPermissions(@NonNull Activity activity, @NonNull String[] permissions, int requestCode) {
        return false;
    }

    @Override
    public boolean onActivityResult(@NonNull Activity activity, int requestCode, int resultCode, @Nullable Intent data) {
        try {
            if (requestCode == 101) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    Intent service = new Intent(context, LocationService.class);
                    context.startService(service);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Intent service = new Intent(context, LocationService.class);
                        context.startForegroundService(service);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public class ConfigureLogin extends Thread {

        int requestType;
        String[] data;

        public ConfigureLogin(int requestType, String... data) {
            this.requestType = requestType;
            this.data = data;
        }

        public void run() {
            super.run();
            try {
                UserModel userModel = new UserModel();
                userModel.setEmail(data[0]);
                userModel.setUser_name(data[1]);
                userModel.setMerchantName(data[1]);
                userModel.setGender(data[2]);
                userModel.setVehicle_no(data[3]);
                userModel.setMerchantKey(data[4]);
                try {
                    List<Sensor> sensors = ((SensorManager) context.getSystemService(SENSOR_SERVICE)).getSensorList(Sensor.TYPE_ALL);
                    userModel.setSensor_list(new Gson().toJson(sensors));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    BatteryManager mBatteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
                    Long currentNow = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        currentNow = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
                    }
                    userModel.setBattery_cap(String.valueOf(currentNow));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    ActivityManager actManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                    ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
                    actManager.getMemoryInfo(memInfo);
                    long totalMemory = memInfo.totalMem;
                    userModel.setRAM((int) totalMemory);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                userModel.setCreation_time(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                userModel.setDevice_name(getDeviceName());
                userModel.setOs("Android");
                userModel.setOwner_name(data[1]);

                new NetworksCalls(context).loginUser(new Gson().toJson(userModel), eventBus);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model;
        } else {
            return manufacturer + " " + model;
        }
    }

    public void onEventMainThread(EventBusContext eventBusContext) {
        try {
            if (eventBusContext.getRequestCode() == UPLOAD_DATA) {
                if (eventBusContext.getResultCode() == NetworkConstants.ResultCodeSuccess) {
                    startAllServices();
                } else {
                    CommonMethods.ShowToast(context, "Invalid Hash!! Please contact support admin@whiteroads.ai");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
