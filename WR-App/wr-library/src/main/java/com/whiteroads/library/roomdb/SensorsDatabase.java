package com.whiteroads.library.roomdb;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import java.io.File;


@Database(entities = {CommonDBTable.class}, version = 1)

public abstract class SensorsDatabase extends RoomDatabase {

    private static SensorsDatabase INSTANCE;
    public abstract CommonDAO commonDAO();


    public static SensorsDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (SensorsDatabase.class) {
                if (INSTANCE == null) {
                    String databasname= "sensors_db";
                    databasname= context.getFilesDir().getAbsolutePath()+File.separator+ "whiteroads"+ File.separator+ databasname;
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            SensorsDatabase.class, databasname)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
