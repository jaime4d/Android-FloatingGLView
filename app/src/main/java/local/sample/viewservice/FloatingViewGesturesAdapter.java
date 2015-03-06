package local.sample.viewservice;

import android.animation.ValueAnimator;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.*;

/**
 * Gesture Adapter
 */
class FloatingViewGesturesAdapter {

    public interface ActionEventListener {
        boolean onDoubleTap(MotionEvent e);
    }
    private ActionEventListener mActionEventListener;
    private final static String TAG_NAME = FloatingViewGesturesAdapter.class.getSimpleName();
    private View mFloatingView;

    private boolean mIsFloatingEnabled;
    private boolean mIsScrolling;
    private ValueAnimator mAnimator = ValueAnimator.ofFloat(0f, 1f);
    private GestureListenerView mGestureListener;
    private GestureDetectorCompat mGestureDetector;
    private WindowHandler mFloatingWindowHandler;

    private float mTouchedX;
    private float mTouchedY;
    private Point mStartPos;

    public FloatingViewGesturesAdapter(View floatingView, WindowHandler floatingWindowHandler) {

        mFloatingView = floatingView;
        mFloatingWindowHandler = floatingWindowHandler;
        mIsFloatingEnabled = true;
    }

    public void setActionEventListener(ActionEventListener listener) {

        mActionEventListener = listener;
    }

    public void setFloatingEnabled(boolean enabled) {

        mIsFloatingEnabled = enabled;
    }

    public void init() {

        prepareViewGestureDetector(mFloatingView);
    }

    void destroy() {

        if(mFloatingView != null) {
            mFloatingView.setOnTouchListener(null);
        }
    }

    private void prepareViewGestureDetector(View view) {

        // Instantiate the gesture detector with the
        // view context and an implementation of
        // GestureDetector.OnGestureListener
        mGestureListener = new GestureListenerView();
        mGestureDetector = new GestureDetectorCompat(view.getContext(), mGestureListener);

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(!mIsFloatingEnabled) return false;

                mGestureDetector.onTouchEvent(event);

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    // mGestureDetector does not dispatch Up events, we do so manually here.
                    mGestureListener.onActionUp(event);
                }

                return true;
            }
        });
    }

    /**
     * Get animation time based on distance scrolled.
     * The reference time is one whole second for a distance equal to the
     * diagonal parent screen size.
     * Any scrolled amount will have an animation time in proportion to this reference distance.
     * @param dsx distance scrolled in the x direction
     * @param dsy distance scrolled int he y direction
     * @return time in milliseconds
     */
    private long getAnimationScrollingTime( float dsx, float dsy ) {

        final long min = 300; // min as per Android sdk
        long tm = 1000;

        Rect rect = mFloatingWindowHandler.getParentWindowRect();
        double dsrc = (rect.width() * rect.width()) + (rect.height() * rect.height());
        double dspt = (dsx * dsx) + (dsy * dsy);

        double tmdspt = ((dspt * tm) / dsrc);
        if(tmdspt > 0) {
            tm = Math.round(tmdspt);
            if(tm < min) {
                tm = min;
            }
        }

        Log.i(TAG_NAME, String.format("anim time for scroll-- %d", tm));

        return tm;
    }

    /**
     * gesture listener class
     */
    private class GestureListenerView extends GestureDetector.SimpleOnGestureListener {

        public boolean onActionUp(MotionEvent e) {

            Log.i(TAG_NAME, String.format("onActionUp-- (%f.2, %f.2), scrolling:%s", e.getRawX(), e.getRawY(), mIsScrolling ? "true" : "false"));

            if(mAnimator != null && mAnimator.isRunning()) {
                mAnimator.cancel();
            }

            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {

            Log.i(TAG_NAME, String.format("onDown-- (%f.2, %f.2)", e.getRawX(), e.getRawY()));

            if(mAnimator.isRunning()) {
                mAnimator.cancel();
            }

            mStartPos = mFloatingWindowHandler.getPosition();

            mTouchedX = e.getRawX();
            mTouchedY = e.getRawY();

            mIsScrolling = false;

            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {

            if( mActionEventListener != null) {
                return mActionEventListener.onDoubleTap(e);
            }

            return super.onDoubleTap(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            if(!mIsFloatingEnabled) return false;

            Log.i(TAG_NAME, String.format("onScroll-- (%f.2, %f.2)", e2.getRawX(), e2.getRawY()));

            mIsScrolling = true;

            Point newPos = new Point();
            newPos.x = mStartPos.x + Math.round(e2.getRawX() - mTouchedX);
            newPos.y = mStartPos.y + Math.round(e2.getRawY() - mTouchedY);

            mFloatingWindowHandler.setPosition(newPos);

            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            Log.i(TAG_NAME, String.format("onFling-- (%f.2, %f.2) v(%f.2, %f.2)", e2.getRawX(), e2.getRawY(), velocityX, velocityY));

            mIsScrolling = false;

            return true;
        }

    }

}
