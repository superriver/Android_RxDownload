package com.example.river.download.rxdownload;

import android.app.Application;
import android.content.Context;

/**
 * Created by river on 2018/3/31.
 */

public class App extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

    }
    public static Context getContext(){
        return mContext;
    }
}
