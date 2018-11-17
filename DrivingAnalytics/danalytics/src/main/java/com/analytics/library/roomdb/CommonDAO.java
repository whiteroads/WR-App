package com.analytics.library.roomdb;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import java.util.List;

@Dao
public interface CommonDAO {


    //Notification center methods
    @Query("Select * from common_data where key_id=:key")
    @Transaction
    CommonDBTable getCommonData(String key);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Transaction
    void saveCommonData(CommonDBTable commonDBTable);

    @Delete
    @Transaction
    void removeCache(CommonDBTable commonDBTable);

    @Query("Select * from common_data")
    @Transaction
    List<CommonDBTable> getAllItems();
}
