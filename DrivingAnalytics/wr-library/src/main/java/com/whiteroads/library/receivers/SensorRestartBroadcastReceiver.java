package com.whiteroads.library.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.whiteroads.library.services.LocationService;
import com.whiteroads.library.services.SensorService;
import com.whiteroads.library.utils.CommonMethods;

import java.util.Calendar;

public class SensorRestartBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        CommonMethods.ShowToast(context,"Booted");
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                Intent sensors = new Intent(context, SensorService.class);
                PendingIntent pIntent = PendingIntent.getService(context, 0, sensors, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarm.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), pIntent);
                Intent service = new Intent(context, LocationService.class);
                context.startService(service);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Intent sensors = new Intent(context, SensorService.class);
                    PendingIntent pIntent = PendingIntent.getForegroundService(context, 0, sensors, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    alarm.setExact(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), pIntent);
                    Intent service = new Intent(context, LocationService.class);
                    context.startForegroundService(service);
                }
            }
            CommonMethods.ShowToast(context,"Started");
        }
    }
}
