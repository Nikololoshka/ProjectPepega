package com.github.nikololoshka.pepegaschedule.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import androidx.annotation.NonNull;

public class NetworkUtils {

    public static boolean isNetworkAvailable(@NonNull Context context) {
        ConnectivityManager manager = context.getSystemService(ConnectivityManager.class);
        if (manager == null) {
            return false;
        }

        Network network = manager.getActiveNetwork();
        if (network == null) {
            return false;
        }

        NetworkCapabilities networkCapabilities = manager.getNetworkCapabilities(network);
        if (networkCapabilities == null) {
            return false;
        }

        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
    }
}
