package com.whiteroads.library.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.whiteroads.library.roomdb.CommonDBTable;
import com.whiteroads.library.roomdb.LocationDatabase;

public class LocationDataWrapper {


    public static final String DATABASE_NAME = "location_db";

    private static final String CAPTURED_CACHE = "captured_cache_location";


    private static LocationDataWrapper sInstance;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private Context context;

    private LocationDataWrapper(Context context) {
        this.context = context;
        mPreferences = (context).getSharedPreferences(DATABASE_NAME, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    public static LocationDataWrapper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new LocationDataWrapper(context);
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
            LocationDatabase.getDatabase(context).commonDAO().saveCommonData(commonDBTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getCacheData(String id) {
        try {
            return LocationDatabase.getDatabase(context).commonDAO().getCommonData(CAPTURED_CACHE+"_"+id).getValue();
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
            LocationDatabase.getDatabase(context).commonDAO().removeCache(commonDBTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
