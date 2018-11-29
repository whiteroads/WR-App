package com.whiteroads.library.networks;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import com.whiteroads.library.constants.NetworkConstants;
import com.whiteroads.library.networks.Interceptors.NetworkCheckInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APISetup {

    public static final String slash = "/";
    public static final String colon = ":";

    public static OkHttpClient.Builder okHttpBuilder;
    public static Retrofit.Builder retrofitBuilder;
    public static Retrofit retrofit;
    public static OkHttpClient client;
    private static Gson gson;
    public static APIInterface apiInterface;
    public static Context context;
    public static ConnectivityManager connectivityManager;

    private static final long CONNECT_TIMEOUT_MILLIS = 20;
    private static final long READ_TIMEOUT_MILLIS = 20;
    private static final long WRITE_TIMEOUT_MILLIS = 20;

    public static final String http = "http://";
    public static final String https = "https://";

    // Cache variable
    public static Cache cache;
    private static final long maxSize = 10 * 1024 * 1024;
    private static File cacheDirectory;

    // Get the cache directory
    private static File getCacheDirectory() {
        Application appContext = (Application) context.getApplicationContext();
        if (cacheDirectory == null)
            cacheDirectory = new File(appContext.getCacheDir(),"cache");
        return cacheDirectory;
    }

    // Get the cache
    public static Cache getCache() {
        if (cache == null)
            cache = new Cache(getCacheDirectory(),maxSize);
        return cache;
    }

    public APISetup(Context contextFunc) {
        context = contextFunc;
    }

    public static void setupAPI() {

        // Setup the cache
        cache = getCache();

        // Setup the connectivity manager
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Setup the OkHttpClient
        okHttpBuilder = new OkHttpClient.Builder();
        okHttpBuilder.cache(cache);
        okHttpBuilder.addInterceptor(new NetworkCheckInterceptor(connectivityManager));

        // Set timeout
        okHttpBuilder.connectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.SECONDS);
        okHttpBuilder.readTimeout(READ_TIMEOUT_MILLIS, TimeUnit.SECONDS);
        okHttpBuilder.writeTimeout(WRITE_TIMEOUT_MILLIS, TimeUnit.SECONDS);
        client = okHttpBuilder.build();

        gson = new GsonBuilder().setLenient().create();

        // Setup Retrofit
        retrofitBuilder = new Retrofit.Builder();
        retrofit = retrofitBuilder
                .baseUrl(NetworkConstants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        // Setup read interface
        apiInterface = retrofit.create(APIInterface.class);
    }
}


