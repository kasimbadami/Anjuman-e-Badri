package anjuman.e.badri;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

public class MainActivity extends AppCompatActivity {

    private static String mBaseUrl = "http://jabalpur.technologyworkshops.net/index.php/app/mobileapp/";

    public static String mURL = mBaseUrl + "index";

    public static String mRegisterURL = mBaseUrl + "registerapp";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            setContentView(R.layout.activity_main);

            ActionBar supportActionBar = getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.hide();
            }

            SharedPreferences sharedPreferences = getSharedPreferences("anjuman.e.badri", Context.MODE_PRIVATE);

            String url = sharedPreferences.getString("URL", "");

            if (!TextUtils.isEmpty(url))
                mURL = url;
            else
                mURL = mBaseUrl + "index";

            FragmentManager supportFragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, new WebViewFragment(), WebViewFragment.TAG);
            fragmentTransaction.commit();
        } catch (Exception e) {
            if (BuildConfig.DEBUG)
                e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

        if (WebViewFragment.canGoBack()) {
            WebViewFragment.goBack();
        } else
            super.onBackPressed();
    }
}
