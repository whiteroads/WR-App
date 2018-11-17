package com.analytics.library.roomdb;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "common_data")
public class CommonDBTable {

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "key_id")
    private String key;

    @ColumnInfo(name = "value_data")
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
