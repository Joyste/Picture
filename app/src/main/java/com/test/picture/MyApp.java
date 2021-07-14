package com.test.picture;

import android.app.Application;
import android.content.Context;


import androidx.multidex.MultiDex;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class MyApp extends Application {



    public static MyApp instance;
    private static Context context;

    public static MyApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        context = getApplicationContext();
        MultiDex.install(this);
    }


    public static Context getContext() {
        return context;
    }

}
