package com.example.administrator.sqt.base;

import android.app.Application;

import com.example.administrator.sqt.util.SharedPreferencesUtils;

public class MyApplication extends Application {
    private static SharedPreferencesUtils shared;

    public static SharedPreferencesUtils getShared() {
        return shared;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        shared = new SharedPreferencesUtils(this, "Login");
    }
}
