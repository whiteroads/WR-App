package com.whiteroads.library.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.whiteroads.library.roomdb.CommonDBTable;
import com.whiteroads.library.roomdb.SensorsDatabase;

public class SensorsDataWrapper {


    public static final String DATABASE_NAME = "sensors_db";

    private static final String CAPTURED_CACHE = "captured_cache_sensors";


    private static SensorsDataWrapper sInstance;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private Context context;

    private SensorsDataWrapper(Context context) {
        this.context =context;
        mPreferences = (context).getSharedPreferences(DATABASE_NAME, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    public static SensorsDataWrapper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SensorsDataWrapper(context);
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
            SensorsDatabase.getDatabase(context).commonDAO().saveCommonData(commonDBTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getCacheData(String id) {
        try {
            return SensorsDatabase.getDatabase(context).commonDAO().getCommonData(CAPTURED_CACHE+"_"+id).getValue();
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
            SensorsDatabase.getDatabase(context).commonDAO().removeCache(commonDBTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
