package local.sample.viewservice;

import android.app.Fragment;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.*;
import local.sample.floatingglview.R;

/**
 * Created by jaime on 3/5/15.
 */
public class FloatingViewService extends Service implements View.OnTouchListener, WindowHandler {

    private static final String TAG = FloatingViewService.class.getSimpleName();
    private final IBinder mBinder = new FloatingViewServiceBinder();
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowLayoutParams;
    private View mFragmentView;
    private FloatingViewGesturesAdapter mGuestureAdapter;

    public class FloatingViewServiceBinder extends Binder {
        public FloatingViewService getService() {
            // Return this instance of LocalService so clients can call public methods
            return FloatingViewService.this;
        }
    }

    public void addViewFromFragment(Fragment fragment) {

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mFragmentView = fragment.onCreateView(inflater, null, null);
        mGuestureAdapter = new FloatingViewGesturesAdapter(mFragmentView, this);
        mGuestureAdapter.init();

        mWindowLayoutParams = new WindowManager.LayoutParams(
                Math.round(getResources().getDimension(R.dimen.floating_view_width)),
                Math.round(getResources().getDimension(R.dimen.floating_view_height)),
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        mWindowLayoutParams.x = 0;
        mWindowLayoutParams.y = 0;
        mWindowLayoutParams.gravity = Gravity.TOP | Gravity.START;

        mWindowManager.addView(mFragmentView, mWindowLayoutParams);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mWindowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
    }

    @Override
    public void onDestroy() {
        if(mWindowManager != null) {
            if(mFragmentView != null) {
                mWindowManager.removeView(mFragmentView);
            }
        }
        super.onDestroy();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, "Touch event: " + event.toString());

        // return false since we are not handling the event
        return false;
    }
    /*
    @Override
    public void setWindowState(WindowStateEnum state) {

        mWindowLayoutParams = createLayoutParams(state);

        if(state != WindowStateEnum.invisible) {
            // maximized or minimized
            if (mDialerFragmentAdded) {
                mWindowManager.updateViewLayout(mFragmentLayout, mWindowLayoutParams);
            } else {
                mWindowManager.addView(mFragmentLayout, mWindowLayoutParams);
                mDialerFragmentAdded = true;
            }
        } else {
            // invisible
            if(mDialerFragmentAdded) {
                mWindowManager.removeView(mFragmentLayout);
                mDialerFragmentAdded = false;
            }
        }

        mWindowState = state;
    }

    @Override
    public WindowStateEnum getWindowState() {

        return mWindowState;
    }
    */
    @Override
    public void setPosition(Point loc) {

        mWindowLayoutParams.x = loc.x;
        mWindowLayoutParams.y = loc.y;

        mWindowManager.updateViewLayout(mFragmentView, mWindowLayoutParams);
    }

    @Override
    public Point getPosition() {

        Point p = new Point();

        int[] loc = new int[2];
        mFragmentView.getLocationOnScreen(loc);
        p.x = loc[0];
        p.y = loc[1];

        return p;
    }

    @Override
    public Rect getParentWindowRect() {

        Rect rect = new Rect();

        Display display = mWindowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        rect.set(0, 0, size.x, size.y);

        return rect;
    }
}
