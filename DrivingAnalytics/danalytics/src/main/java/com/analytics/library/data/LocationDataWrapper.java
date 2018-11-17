package com.analytics.library.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.analytics.library.LibraryApplication;
import com.analytics.library.roomdb.CommonDBTable;
import com.analytics.library.roomdb.LocationDatabase;

public class LocationDataWrapper {


    public static final String DATABASE_NAME = "location_db";

    private static final String CAPTURED_CACHE = "captured_cache_location";


    private static LocationDataWrapper sInstance;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private LocationDataWrapper() {
        mPreferences = (LibraryApplication.getCustomAppContext()).getSharedPreferences(DATABASE_NAME, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    public static LocationDataWrapper getInstance() {
        if (sInstance == null) {
            sInstance = new LocationDataWrapper();
        }
        return sInstance;
    }

    public SharedPreferences getPref(){
        return mPreferences;
    }

    public void saveCacheData(String data) {
        try {
            CommonDBTable commonDBTable = new CommonDBTable();
            commonDBTable.setKey(CAPTURED_CACHE+"_"+data.hashCode());
            commonDBTable.setValue(data);
            LocationDatabase.getDatabase(LibraryApplication.getCustomAppContext()).commonDAO().saveCommonData(commonDBTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getCacheData(String id) {
        try {
            return LocationDatabase.getDatabase(LibraryApplication.getCustomAppContext()).commonDAO().getCommonData(CAPTURED_CACHE+"_"+id).getValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void removeCacheData(String data) {
        try {
            CommonDBTable commonDBTable = new CommonDBTable();
            commonDBTable.setKey(CAPTURED_CACHE+"_"+data.hashCode());
            commonDBTable.setValue(data);
            LocationDatabase.getDatabase(LibraryApplication.getCustomAppContext()).commonDAO().removeCache(commonDBTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
