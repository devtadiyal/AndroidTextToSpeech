package com.saffron.texttospeech;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by day on 16/2/18.
 */

public class MyApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
