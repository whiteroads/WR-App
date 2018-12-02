package com.analytics.application.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CommonMethods {

    public static void ShowToast(Context context,
                                 String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static boolean IsConnected(Context context) {

        try
        {
            final ConnectivityManager conMgr =  (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.getState() == NetworkInfo.State.CONNECTED;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }

    public static double round(double value) {
        BigDecimal bd = new BigDecimal(value);
        try {
            bd = bd.setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bd.doubleValue();
    }
}
