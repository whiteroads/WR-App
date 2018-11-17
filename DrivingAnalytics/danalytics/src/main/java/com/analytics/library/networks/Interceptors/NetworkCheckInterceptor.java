package com.analytics.library.networks.Interceptors;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class NetworkCheckInterceptor implements Interceptor {

    private ConnectivityManager connectivityManager;

    public NetworkCheckInterceptor(ConnectivityManager connectivityManager) {
        this.connectivityManager = connectivityManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED)
            return chain.proceed(chain.request());

        else throw new IOException("No Network");
    }
}
