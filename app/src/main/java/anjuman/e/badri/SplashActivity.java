package anjuman.e.badri;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;


public class SplashActivity extends AppCompatActivity {

    private static final int MSG_CONTINUE = 2000;
    private static final int DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        try {
            setContentView(R.layout.activity_splash);
            ActionBar actionBar = getSupportActionBar();

            if (actionBar != null)
                actionBar.hide();

            mHandler.sendEmptyMessageDelayed(MSG_CONTINUE, DELAY);
        } catch (Exception e) {
            if (BuildConfig.DEBUG)
                e.printStackTrace();
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MSG_CONTINUE:
                    startHomeActivity();
                    break;
            }
        }
    };

    private void startHomeActivity() {

        startActivity(new Intent(this, MainActivity.class));

        finish();
    }

    @Override
    protected void onDestroy() {
        mHandler.removeMessages(MSG_CONTINUE);
        super.onDestroy();
    }
}
