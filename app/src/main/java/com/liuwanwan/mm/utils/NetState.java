package com.liuwanwan.mm.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetState {
    public static final int getNetWorkConnectionType(Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        Permission.verifyNetworkPermissions((Activity) context);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.isConnected()) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    return 1; //"当前WiFi连接可用 "
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    return 2;//                    Log.e(TAG, "当前移动网络连接可用 ");
                }
            } else {
                return 0;// "当前没有网络连接，请确保你已经打开网络 ");
            }
        }
        else {   // not connected to the internet
            return 0;// "当前没有网络连接，请确保你已经打开网络 ");
        }
    return 0;
    }
}
