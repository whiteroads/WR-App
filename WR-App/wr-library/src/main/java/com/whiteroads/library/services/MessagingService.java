//package com.analytics.library.services;
//
//import android.app.ActivityManager;
//import android.app.AlarmManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//import UserDataWrapper;
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//
//import java.util.Calendar;
//
//public class MessagingService extends FirebaseMessagingService {
//
//    private Intent service,sensors;
//    private PendingIntent pIntent;
//    private AlarmManager alarm;
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
//        try{
//            if(UserDataWrapper.getInstance(getApplicationContext())().isServiceStopped()){
//                UserDataWrapper.getInstance(getApplicationContext())().setIsServicesStopped(false);
//            }
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//                sensors = new Intent(getApplicationContext(), SensorService.class);
//                pIntent = PendingIntent.getService(this, 0, sensors, PendingIntent.FLAG_UPDATE_CURRENT);
//                alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
//                alarm.set(AlarmManager.RTC_WAKEUP,Calendar.getInstance(getApplicationContext())().getTimeInMillis(), pIntent);
//                service = new Intent(this, LocationService.class);
//                startService(service);
//            }else{
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    sensors = new Intent(getApplicationContext(), SensorService.class);
//                    pIntent = PendingIntent.getForegroundService(this, 0, sensors, PendingIntent.FLAG_UPDATE_CURRENT);
//                    alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
//                    alarm.setExact(AlarmManager.RTC_WAKEUP,Calendar.getInstance(getApplicationContext())().getTimeInMillis(), pIntent);
//                    service = new Intent(this, LocationService.class);
//                    startForegroundService(service);
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onNewToken(String s) {
//        super.onNewToken(s);
//    }
//
//    @Override
//    public void onDeletedMessages() {
//        super.onDeletedMessages();
//    }
//
//    @Override
//    public void onSendError(String s, Exception e) {
//        super.onSendError(s, e);
//    }
//
//    @Override
//    public void onMessageSent(String s) {
//        super.onMessageSent(s);
//    }
//
//    private boolean isServiceRunning(Class<?> serviceClass) {
//        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (serviceClass.getName().equals(service.service.getClassName())) {
//                return true;
//            }
//        }
//        return false;
//    }
//}
