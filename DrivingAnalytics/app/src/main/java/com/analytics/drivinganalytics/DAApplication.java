package com.analytics.drivinganalytics;

import android.app.Application;
import android.content.Context;

import com.analytics.library.LibraryApplication;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class DAApplication extends LibraryApplication {
    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!
        try {
            try {
                FacebookSdk.sdkInitialize(getApplicationContext());
                AppEventsLogger.activateApp(this);
            }catch (Exception e){
                e.printStackTrace();
            }
            context = getApplicationContext();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Context getCustomAppContext() {
        return context;
    }
}
