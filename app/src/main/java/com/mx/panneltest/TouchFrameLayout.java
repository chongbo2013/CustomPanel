package com.mx.panneltest;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by x002 on 2017/2/6.
 */

public class TouchFrameLayout extends FrameLayout {
    public TouchFrameLayout(Context context) {
        super(context);
    }

    public TouchFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
      return true;
    }
}
