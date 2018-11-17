package com.analytics.library;

import android.app.Application;
import android.content.Context;

import com.analytics.library.networks.APISetup;

public class LibraryApplication extends Application {
    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!
        try {
            context = getApplicationContext();
            APISetup api = new APISetup(this);
            api.setupAPI();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Context getCustomAppContext() {
        return context;
    }
}
