package com.analytics.application.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.analytics.application.DAApplication;

public class UserDataWrapper {


    public static final String DATABASE_NAME = "user_db";
    private static final String USER_ACCOUNT = "user_account";
    private static final String USER_ACCOUNT_EMAIl = "user_account_email";
    private static final String VEHICLE_NUMBER = "vehicle_number";
    private static final String MOBILE_NUMBER = "mobile_number";
    private static final String USER_NAME = "user_name";


    private static UserDataWrapper sInstance;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private UserDataWrapper() {
        mPreferences = (DAApplication.getCustomAppContext()).getSharedPreferences(DATABASE_NAME, Context.MODE_PRIVATE);
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

    public void saveUserMobile(String mobile) {
        try {
            mEditor.putString(MOBILE_NUMBER,  mobile).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUserMobile() {
        try {
            return mPreferences.getString(MOBILE_NUMBER, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void saveUserName(String name) {
        try {
            mEditor.putString(USER_NAME,  name).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUserName() {
        try {
            return mPreferences.getString(USER_NAME, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
