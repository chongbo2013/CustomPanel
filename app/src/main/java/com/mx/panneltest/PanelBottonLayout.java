package com.mx.panneltest;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**底部伸缩隐藏面板
 * Created by xff on 2017/2/6.
 */

public class PanelBottonLayout extends LinearLayout {
    public PanelBottonLayout(Context context) {
        super(context);
    }

    public PanelBottonLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PanelBottonLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
