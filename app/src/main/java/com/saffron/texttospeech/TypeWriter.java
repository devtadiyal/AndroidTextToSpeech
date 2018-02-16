package com.saffron.texttospeech;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class TypeWriter extends TextView {

    private static final String TAG = TypeWriter.class.getSimpleName();
    private CharSequence mText;
    private int mIndex;
    private long mDelay = 500; // Default 500ms character delay
    Handler animationCompleteCallBack;

    public TypeWriter(Context context) {
        super(context);
    }

    public void setAnimationCompleteListener(Handler animationCompleteCallBack) {
        this.animationCompleteCallBack = animationCompleteCallBack;
    }

    public TypeWriter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private Handler mHandler = new Handler();
    private Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            setText(mText.subSequence(0, mIndex++));
            if (mIndex <= mText.length()) {
                mHandler.postDelayed(characterAdder, mDelay);
            } else {
                if (null != animationCompleteCallBack)
                    animationCompleteCallBack.sendMessage(new Message());
                else
                    Log.i(TAG, "Animation Complete Listener not set...");
            }
        }
    };

    public void animateText(CharSequence text) {
        mText = text;
        mIndex = 0;
        setText("");
        mHandler.removeCallbacks(characterAdder);
        mHandler.postDelayed(characterAdder, mDelay);
    }

    public void setCharacterDelay(long millis) {
        mDelay = millis;
    }
}