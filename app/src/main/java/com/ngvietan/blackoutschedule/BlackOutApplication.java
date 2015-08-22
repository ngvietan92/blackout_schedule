package com.ngvietan.blackoutschedule;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.app.Application;

/**
 * Created by NgVietAn on 15/08/2015.
 */
public class BlackOutApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }
}
