package com.example.projektdyplomowyankiety;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

public class ConectionReciver extends BroadcastReceiver {

    public static ContectivityRecListener contectivityRecListener;

    public ConectionReciver() {
        super();
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetw = connectivityManager.getActiveNetworkInfo();

        boolean isConnect = actNetw != null && actNetw.isConnectedOrConnecting();

        if (contectivityRecListener != null)
        {
            contectivityRecListener.onNetworkConnectionCHanged(isConnect);
        }
    }




    public interface ContectivityRecListener {
        void onNetworkConnectionCHanged(boolean isConnected);

    }

}
