package com.whiteroads.library.services;

import android.app.Notification;

public class NotificationSingleton {

    private static NotificationSingleton object=null;
    private Notification builder;


    private NotificationSingleton(){

    }

    public static NotificationSingleton getObject() {
        if(object == null){
            object = new NotificationSingleton();
        }
        return object;
    }

    public Notification getBuilder() {
        return builder;
    }

    public void setBuilder(Notification builder) {
        this.builder = builder;
    }
}
