package com.whiteroads.library.constants;

import android.graphics.Bitmap;

/**
 * Created by RahulGoyal on 08/07/17.
 */

public class EventBusContext {
    String response;
    Bitmap file;
    int requestCode;
    int resultCode;

    public static final int Server_Failure = 500;
    public static final int Server_Success = 200;

    public EventBusContext(String response, int resultCode){
        this.response  = response;
        this.resultCode = resultCode;
    }

    public EventBusContext(int requestCode, int resultCode){
        this.requestCode = requestCode;
        this.resultCode = resultCode;
    }

    public EventBusContext(String response, int resultCode, int requestCode){
        this.response  = response;
        this.resultCode = resultCode;
        this.requestCode = requestCode;
    }
    public EventBusContext(Bitmap response, int resultCode){
        this.file  = response;
        this.resultCode = resultCode;
    }
    public EventBusContext(Bitmap response, int resultCode, int requestCode){
        this.file  = response;
        this.resultCode = resultCode;
        this.requestCode = requestCode;
    }

    public EventBusContext(int requestCode){
        this.requestCode = requestCode;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public Bitmap getFile() {
        return file;
    }
}
