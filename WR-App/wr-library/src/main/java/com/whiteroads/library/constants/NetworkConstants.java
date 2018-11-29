package com.whiteroads.library.constants;

public class NetworkConstants {

    public static final String TAG = "ExceptionLog";
    public static final String BASE_URL = "http://api.whiteroads.ai:8585/";
    public static final String STORE_CAPTURE_DATA = "user/store_data.ns";
    public static final String STORE_CAPTURE_DATA_LOCATIONS = "user/store_data_locations.ns";
    public static final String STORE_CAPTURE_DATA_STEPS = "user/store_data_steps.ns";
    public static final String LOGIN_USER = "user/login.ns";

    public final static String captureData = "captureData";
    public final static String loginData = "loginData";



    //common response params
    public final static int ResultCodeFail = 0;
    public final static int ResultCodeSuccess = 1;

    //firebase constants
    public final static String UploadTimeInMins = "upload_time_in_mins";
    public final static String FrequencySeconds = "frequency_seconds";
}
