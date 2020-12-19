package mumineen.connect.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.BuildConfig;

import mumineen.connect.app.view.LoginActivity;
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

                    SharedPreferences sharedPreferences = getSharedPreferences("anjuman.e.badri", Context.MODE_PRIVATE);
                    String url = sharedPreferences.getString("URL", "");
                    //MainActivity.mRegisterURL = sharedPreferences.getString(AppConstants., "");

                    if(url!=null && !(url.isEmpty()))
                    {
                        startHomeActivity();
                    }
                    else
                    {
                       startLoginActivity();
                    }

                    break;
            }
        }
    };

    private void startHomeActivity()
    {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void startLoginActivity()
    {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        mHandler.removeMessages(MSG_CONTINUE);
        super.onDestroy();
    }
}
