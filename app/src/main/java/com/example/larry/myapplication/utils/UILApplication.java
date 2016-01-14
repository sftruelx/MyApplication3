package com.example.larry.myapplication.utils;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.android.volley.base.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.larry.myapplication.network.NetworkModule;


/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class UILApplication extends com.android.volley.base.ApplicationController{
    public RequestQueue mQueue;
    private NetworkModule networkModule;
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onCreate() {
        if (Constants.Config.DEVELOPER_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());
        }
        mQueue = Volley.newRequestQueue(this.getBaseContext());
        super.onCreate();
    }
    public NetworkModule getNetworkModule() {
        if (networkModule == null) {
            networkModule = new NetworkModule(this);
        }
        return networkModule;
    }
}
