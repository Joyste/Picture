package com.test.picture;

import android.app.Application;

public class MyApp extends Application {



    public static MyApp instance;

    public static MyApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

}
