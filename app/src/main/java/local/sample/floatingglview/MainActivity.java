package local.sample.floatingglview;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import local.sample.viewservice.FloatingViewService;

import java.util.Date;


public class MainActivity extends Activity {

    ServiceConnection mServiceConnection;
    FloatingViewService mFloatingViewService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        View v = getWindow().getDecorView().findViewById(android.R.id.content);

        final TextView statusTextView = (TextView)findViewById(R.id.status_text);
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Date now = new Date();
                statusTextView.setText(String.format("Last activity touch event (%d,%d) @ %s",
                        Math.round(event.getRawX()), Math.round(event.getRawY()), now.toString()));

                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_floating_view:

                startService();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startService() {

        if(mServiceConnection != null) return;

        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                // We've bound to ConciergeService, cast the IBinder and get FloatingViewService instance
                FloatingViewService.FloatingViewServiceBinder binder = (FloatingViewService.FloatingViewServiceBinder)service;
                mFloatingViewService = binder.getService();

                mFloatingViewService.addViewFromFragment(new SampleFragment());
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {

            }
        };

        // startService(new Intent(getBaseContext(), FloatingViewService.class));
        bindService(new Intent(getBaseContext(), FloatingViewService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void stopService() {

        stopService(new Intent(getBaseContext(), FloatingViewService.class));
    }
}
