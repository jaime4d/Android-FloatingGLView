# Android-FloatingGLView
Android sample app demonstrating a transparent gl surface view floating on top of all device apps.

![android-floatingglview](https://cloud.githubusercontent.com/assets/11356144/6536547/6fa2eca8-c41c-11e4-8210-ccaa3efc9a15.png)

The basic jist is that you create a service, inflate a fragment and extract its content view, then add it to Window Manager.  We use a gesture adapter to move our floating view around.

Your AndroidManifest.xml file needs to have these permisions
```
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
<uses-permission android:name="android.permission.GET_TASKS" />
```

Once you start the service, obtain a binder interface and call addViewFromFragment().  You cannot add a fragment to Window Manager, only Views.  We work around this by passing a new instance of your fragment class to the service, it will inflate and extract the content view from it and pass it to Window Manager accordingly.  The added benefit of this approach is that you do not need to create dynamic views programaticaly at all; just use a regular fragment as usual and our service implementation extract its content view.
```
private void startService() {

        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                // We've bound to our service, cast the IBinder and get FloatingViewService instance
                FloatingViewService.FloatingViewServiceBinder binder = (FloatingViewService.FloatingViewServiceBinder)service;
                mFloatingViewService = binder.getService();

                mFloatingViewService.addViewFromFragment(new SampleFragment());
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {

            }
        };

        bindService(new Intent(getBaseContext(), FloatingViewService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
    }
```

In order to create a floating window in Android, you need add your view hierarchy using Window Manager with a Service.  The trick is to use correct WindowManager.LayoutParams as shown below.  
```
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
```
