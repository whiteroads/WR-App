package com.analytics.library.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.analytics.library.LibraryApplication;
import com.analytics.library.constants.ActivityContants;
import com.google.android.gms.location.DetectedActivity;

import java.util.Random;

public class UserDataWrapper {


    public static final String DATABASE_NAME = "user_db";
    private static final String USER_ACCOUNT = "user_account";
    private static final String USER_ACCOUNT_EMAIl = "user_account_email";
    private static final String LAST_UPLOAD_TIME = "last_upload_time";
    private static final String FREQUENCY_SECONDS = "frequency_seconds";
    private static final String UPLOAD_TIME = "upload_time";
    private static final String VEHICLE_NUMBER = "vehicle_number";
    private static final String IS_SERVICE_STOPPED = "is_service_stopped";
    private static final String SCORE = "score";
    private static final String LAST_DETECTED_ACTIVITY = "last_detected_activity";
    private static final String LAST_DETECTED_ACTIVITY_CONFIDENCE = "last_detected_activity_confidence";


    private static UserDataWrapper sInstance;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private UserDataWrapper() {
        mPreferences = (LibraryApplication.getCustomAppContext()).getSharedPreferences(DATABASE_NAME, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    public static UserDataWrapper getInstance() {
        if (sInstance == null) {
            sInstance = new UserDataWrapper();
        }
        return sInstance;
    }

    public SharedPreferences getPref() {
        return mPreferences;
    }

    public void saveUserId(int id) {
        try {
            mEditor.putInt(USER_ACCOUNT, id).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getUserId() {
        try {
            return mPreferences.getInt(USER_ACCOUNT, -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void saveVehicleNumber(String number) {
        try {
            mEditor.putString(VEHICLE_NUMBER, number).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getVehicleNumber() {
        try {
            return mPreferences.getString(VEHICLE_NUMBER, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void saveUserEmail(String email) {
        try {
            mEditor.putString(USER_ACCOUNT_EMAIl, email).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUserEmail() {
        try {
            return mPreferences.getString(USER_ACCOUNT_EMAIl, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void saveUploadTimeInMins(int time) {
        try {
            mEditor.putInt(UPLOAD_TIME, time).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getUploadTime() {
        try {
            return mPreferences.getInt(UPLOAD_TIME, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 2;
    }

    public int getFrequency() {
        try {
            return mPreferences.getInt(FREQUENCY_SECONDS, 5);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 2;
    }

    public void saveFrequency(int value) {
        try {
            mEditor.putInt(FREQUENCY_SECONDS, value).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveLastUploadTimeRandomScore(long time) {
        try {
            mEditor.putLong(LAST_UPLOAD_TIME, time).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getLastUploadTimeRandomScore() {
        try {
            return mPreferences.getLong(LAST_UPLOAD_TIME, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void saveRandomScore(float score) {
        try {
            mEditor.putFloat(SCORE, score).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float getRandomScore() {
        int num = 50;
        try {
            Random random = new Random();
            num = random.nextInt((60 - 50) + 1) + 50;
            return mPreferences.getFloat(SCORE, num);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    public boolean isServiceStopped() {
        try {
            return mPreferences.getBoolean(IS_SERVICE_STOPPED, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setIsServicesStopped(boolean value) {
        try {
            mEditor.putBoolean(IS_SERVICE_STOPPED, value).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getLastActivityRecorded() {
        try {
            int type = mPreferences.getInt(LAST_DETECTED_ACTIVITY, DetectedActivity.UNKNOWN);
            switch (type) {
                case DetectedActivity.IN_VEHICLE:
                    return ActivityContants.InVehicle;
                case DetectedActivity.ON_BICYCLE:
                    return ActivityContants.OnBicyle;
                case DetectedActivity.ON_FOOT:
                    return ActivityContants.OnFoot;
                case DetectedActivity.RUNNING:
                    return ActivityContants.Running;
                case DetectedActivity.STILL:
                    return ActivityContants.Still;
                case DetectedActivity.WALKING:
                    return ActivityContants.Walking;
                case DetectedActivity.UNKNOWN:
                    return ActivityContants.Unknown;
                case DetectedActivity.TILTING:
                    return ActivityContants.Tilting;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ActivityContants.Unknown;
    }

    public void setLastDetectedActivity(int value) {
        try {
            mEditor.putInt(LAST_DETECTED_ACTIVITY, value).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getLastActivityRecordedConfidence() {
        try {
            return mPreferences.getInt(LAST_DETECTED_ACTIVITY_CONFIDENCE, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setLastDetectedActivityConfidence(int value) {
        try {
            mEditor.putInt(LAST_DETECTED_ACTIVITY_CONFIDENCE, value).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
