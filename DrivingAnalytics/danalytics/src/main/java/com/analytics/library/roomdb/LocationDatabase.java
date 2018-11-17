package com.analytics.library.roomdb;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import java.io.File;


@Database(entities = {CommonDBTable.class}, version = 1)

public abstract class LocationDatabase extends RoomDatabase {

    private static LocationDatabase INSTANCE;
    public abstract CommonDAO commonDAO();


    public static LocationDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (LocationDatabase.class) {
                if (INSTANCE == null) {
                    String databasname= "location_db";
                    databasname= context.getFilesDir().getAbsolutePath()+File.separator+ "analyticsdb"+ File.separator+ databasname;
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            LocationDatabase.class, databasname)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
