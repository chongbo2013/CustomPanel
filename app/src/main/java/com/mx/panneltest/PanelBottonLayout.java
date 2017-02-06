package com.mx.panneltest;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

/**
 * 底部伸缩隐藏面板
 * 只允许3个子View
 * Created by xff on 2017/2/6.
 */

public class PanelBottonLayout extends LinearLayout {
    private int[] layoutHight = new int[3];
    private final static int INVALID_ID = -1;
    private int mActivePointerId = INVALID_ID;
    private float mLastX = 0;
    private float mLastY = 0;


    private int mSecondaryPointerId = INVALID_ID;
    private float mSecondaryLastX = 0;
    private float mSecondaryLastY = 0;

    private boolean mIsBeingDragged = false;

    private int mTouchSlop;
    private int mMinFlingSpeed;
    private int mMaxFlingSpeed;
    //总的移动距离，判断方向
    float totalDeltal = 0;
    //标记当前收缩状态
    int currentPosition=0;

    private LauncherScroller mScroller;
    private VelocityTracker mVelocityTracker;

//    private int parentHight=0;
    public void target() {
        currentPosition+=1;
        if(currentPosition==3)
            currentPosition=0;
        snapToPostion(currentPosition);
    }



    public static class ScrollInterpolator implements Interpolator {
        public ScrollInterpolator() {
        }

        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1;
        }
    }

    public static class OvershootInterpolator implements Interpolator {
        private static final float DEFAULT_TENSION = 1.3f;
        private float mTension;

        public OvershootInterpolator() {
            mTension = DEFAULT_TENSION;
        }

        public void setDistance(int distance) {
            mTension = distance > 0 ? DEFAULT_TENSION / distance : DEFAULT_TENSION;
        }

        public void disableSettle() {
            mTension = 0.f;
        }

        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * ((mTension + 1) * t + mTension) + 1.0f;
        }
    }

    public PanelBottonLayout(Context context) {
        super(context);
        init();
    }

    public PanelBottonLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PanelBottonLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinFlingSpeed = configuration.getScaledMinimumFlingVelocity();
        mMaxFlingSpeed = configuration.getScaledMaximumFlingVelocity();
        mScroller = new LauncherScroller(getContext(), new OvershootInterpolator());
        setWillNotDraw(false);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
//        if(getParent()!=null&&getParent() instanceof ViewGroup){
//            ((ViewGroup)getParent()).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    parentHight=((ViewGroup)getParent()).getMeasuredHeight();
//                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                }
//            });
//        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //初始化高度
        if (getChildCount() != 3) {
            throw new IllegalStateException("only 3 child view");
        }
        for (int i = 0; i < getChildCount(); i++) {
            layoutHight[i] = getChildAt(i).getMeasuredHeight();
        }
    }


    private void initVelocityTrackerIfNotExist() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }



    /**
     * 只进行判断是否可以进行拖动和初始化移动数据
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                int index = MotionEventCompat.getActionIndex(ev);
                float y = MotionEventCompat.getY(ev, index);
                float x = MotionEventCompat.getX(ev, index);
                initVelocityTrackerIfNotExist();
                mVelocityTracker.addMovement(ev);
                mLastY = y;
                mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                //分两种情况，一种是初始动作，一个是界面正在滚动，down触摸停止滚动
                mIsBeingDragged = false;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                index = MotionEventCompat.getActionIndex(ev);
                mSecondaryPointerId = MotionEventCompat.getPointerId(ev, index);
                mSecondaryLastY = MotionEventCompat.getY(ev, index);
                break;

            case MotionEvent.ACTION_MOVE:
                //获取活跃手指
                index = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                y = MotionEventCompat.getY(ev, index);
                x = MotionEventCompat.getX(ev, index);
                //移动增量
                final float yDiff = Math.abs(y - mLastY);
                if (yDiff > mTouchSlop&&isTouchOnButton(x,y)) {
                    //是滚动状态啦
                    mIsBeingDragged = true;
                    mLastY = y;
                    initVelocityTrackerIfNotExist();
                    mVelocityTracker.addMovement(ev);
                    //可以移动，告诉父View不要拦截事件
                    final ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }

                break;
            case MotionEvent.ACTION_POINTER_UP:
                //手指抬起情况
                index = MotionEventCompat.getActionIndex(ev);
                int curId = MotionEventCompat.getPointerId(ev, index);
                if (curId == mActivePointerId) {
                    mActivePointerId = mSecondaryPointerId;
                    mLastY = mSecondaryLastY;
                    mVelocityTracker.clear();
                } else {
                    mSecondaryPointerId = INVALID_ID;
                    mSecondaryLastY = 0;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsBeingDragged = false;
                mActivePointerId = INVALID_ID;
                recycleVelocityTracker();
                break;
            default:
        }
        return mIsBeingDragged;
    }

    /**
     * 是否触摸在按钮上面
     * @param x
     * @param y
     * @return
     */
    private  boolean isTouchOnButton(float x,float y){
        int scrollY=Math.abs(getScrollY());
        return y>=scrollY;
    }

    /**
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mScroller == null) {
            return false;
        }
        initVelocityTrackerIfNotExist();
        int action = MotionEventCompat.getActionMasked(event);
        int index = -1;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) { //fling
                    mScroller.abortAnimation();
                }
                index = MotionEventCompat.getActionIndex(event);
                mActivePointerId = MotionEventCompat.getPointerId(event, index);
                mLastY = MotionEventCompat.getY(event, index);

                break;
            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_ID) {
                    break;
                }
                index = MotionEventCompat.findPointerIndex(event, mActivePointerId);
                if (index == -1) {
                    break;
                }
                float y = MotionEventCompat.getY(event, index);
                float x = MotionEventCompat.getX(event, index);
                float deltaY = mLastY - y;

                if (isTouchOnButton(x,y)&&!mIsBeingDragged && Math.abs(deltaY) > mTouchSlop) {
                    requestParentDisallowInterceptTouchEvent();

                    mIsBeingDragged = true;
                    // 减少滑动的距离
                    if (deltaY > 0) {
                        deltaY -= mTouchSlop;
                    } else {
                        deltaY += mTouchSlop;
                    }
                }

                if (mIsBeingDragged) {
                    totalDeltal += deltaY;
                    scrollTo(0, Math.min(Math.max(getScrollY() + (int) deltaY, -(layoutHight[1] + layoutHight[2])), 0));
                    mLastY = y;
                }
                if (mSecondaryPointerId != INVALID_ID) {
                    index = MotionEventCompat.findPointerIndex(event, mSecondaryPointerId);
                    mSecondaryLastY = MotionEventCompat.getY(event, index);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                endDrag();
                break;
            case MotionEvent.ACTION_UP:
                if (mIsBeingDragged) {
                    mVelocityTracker.computeCurrentVelocity(1000, mMaxFlingSpeed);
                    int initialVelocity = (int) mVelocityTracker.getYVelocity(mActivePointerId);
                    if (Math.abs(initialVelocity) > mMinFlingSpeed) {
                        // fling
                        doFling(-initialVelocity);
                    } else {
                        snapToDestination(totalDeltal < 0);

                    }
                    endDrag();
                }
                break;
            default:
        }
        if (mVelocityTracker != null) {
            mVelocityTracker.addMovement(event);
        }
        return mIsBeingDragged;
    }

    private void snapToDestination(boolean isSnapDown) {
        mScroller.startScroll(0, getScrollY(), 0, getCurrentLayout(isSnapDown));
        invalidate();
    }


    /**
     * scrollto layout1  layout2 layout3
     *
     * @param speed
     */
    private void doFling(int speed) {
        if (mScroller == null) {
            return;
        }
        mScroller.startScroll(0, getScrollY(), 0, getCurrentLayout(speed > 0 ? false : true));
        invalidate();
    }

    private int getCurrentLayout(boolean isFlingDown) {
        int scrollY = getScrollY();
        if (isFlingDown) {
            if (scrollY < 0 && scrollY >= -layoutHight[2]) {
                currentPosition=1;
                return -layoutHight[2] - getScrollY();
            } else if (scrollY < -layoutHight[2] && scrollY > -(layoutHight[2] + layoutHight[1])) {
                currentPosition=2;
                return -(layoutHight[2] + layoutHight[1]) - getScrollY();
            }
        } else {
            if (scrollY < -layoutHight[2] && scrollY >= -(layoutHight[2] + layoutHight[1])) {
                currentPosition=1;
                return -layoutHight[2] - getScrollY();
            } else if (scrollY < 0 && scrollY >= -layoutHight[2]) {
                currentPosition=0;
                return -getScrollY();
            }
        }
        return 0;
    }
    private void snapToPostion(int currentPosition) {
        switch (currentPosition){
            case 0:
                mScroller.startScroll(0, getScrollY(), 0, -getScrollY());
                break;
            case 1:
                mScroller.startScroll(0, getScrollY(), 0, -layoutHight[2] - getScrollY());
                break;
            case 2:
                mScroller.startScroll(0, getScrollY(), 0, -(layoutHight[2] + layoutHight[1]) - getScrollY());
                break;
        }
        invalidate();
    }

    private void endDrag() {
        mIsBeingDragged = false;
        recycleVelocityTracker();
        mActivePointerId = INVALID_ID;
        mLastY = 0;
        totalDeltal = 0;
    }

    private void requestParentDisallowInterceptTouchEvent() {
        final ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }


}
