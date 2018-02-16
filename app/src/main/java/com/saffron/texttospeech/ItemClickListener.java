package com.saffron.texttospeech;

import android.view.View;

/**
 * Created by saffron on 2/15/2018.
 */

public interface ItemClickListener {
    void onClick(View view, int position);
    void onLongClick(View view, int position);
}

