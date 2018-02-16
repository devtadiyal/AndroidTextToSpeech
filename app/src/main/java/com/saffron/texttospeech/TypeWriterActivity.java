package com.saffron.texttospeech;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class TypeWriterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Handler animationCompleteCallBack = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Log.i("Log", "Animation Completed");
                return false;
            }
        });

        TypeWriter typewriter = new TypeWriter(this);
        typewriter.setCharacterDelay(100);
        typewriter.setTextSize(30);
        typewriter.setTypeface(null, Typeface.BOLD);
        typewriter.setPadding(20, 20, 20, 20);
        typewriter.setAnimationCompleteListener(animationCompleteCallBack);
        typewriter.animateText("CoderzHeaven\nHeaven of all working codes");

        setContentView(typewriter);
    }
}