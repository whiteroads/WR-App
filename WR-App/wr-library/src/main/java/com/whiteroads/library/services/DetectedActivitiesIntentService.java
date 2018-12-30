package com.whiteroads.library.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.whiteroads.library.data.UserDataWrapper;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DetectedActivitiesIntentService extends IntentService {

    protected static final String TAG = "DetectedActivitiesIS";

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public DetectedActivitiesIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Handles incoming intents.
     * @param intent The Intent is provided (inside a PendingIntent) when requestActivityUpdates()
     *               is called.
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

            // Get the list of the probable activities associated with the current state of the
            // device. Each activity is associated with a confidence level, which is an int between
            // 0 and 100.
            ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();
            Log.v("TAG", detectedActivities.get(0).getType() + " " + detectedActivities.get(0).getConfidence());
            Collections.sort(detectedActivities, new CustomComparator());
            if (detectedActivities!=null && detectedActivities.size()>0){
                UserDataWrapper.getInstance(getApplicationContext()).setLastDetectedActivity(detectedActivities.get(0).getType());
                UserDataWrapper.getInstance(getApplicationContext()).setLastDetectedActivityConfidence(detectedActivities.get(0).getConfidence());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public class CustomComparator implements Comparator<DetectedActivity> {
        @Override
        public int compare(DetectedActivity o1, DetectedActivity o2) {
            if(o1.getConfidence()<o2.getConfidence()){
                return 1;
            }else{
                return 0;
            }
        }
    }
}
