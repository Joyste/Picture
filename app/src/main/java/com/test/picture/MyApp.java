package com.test.picture;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraXConfig;

public class MyApp extends Application implements CameraXConfig.Provider{

    private static final String TAG = MyApp.class.getSimpleName();

    public static MyApp instance;

    public static MyApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    @NonNull
    @Override
    public CameraXConfig getCameraXConfig() {
        return null;
    }
}
