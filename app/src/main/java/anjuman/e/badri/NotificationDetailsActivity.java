package anjuman.e.badri;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

public class NotificationDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.notification_details);

            ActionBar supportActionBar = getSupportActionBar();

            if (supportActionBar != null) {
                SpannableString s = new SpannableString("Jamaat Notifications");
                s.setSpan(new TypefaceSpan(this, "calibri.ttf"), 0, s.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                supportActionBar.setTitle(s);
            }

            Bundle extras = getIntent().getExtras();
            if (extras != null && extras.containsKey("POSITION")) {
                int position = extras.getInt("POSITION");
                NotificationPOJO notification = NotificationActivity.mNotificationses.get(position);

                ((TextView) findViewById(R.id.text_notification_name)).setText(notification.mNotificationTitle);
                ((TextView) findViewById(R.id.message)).setText(notification.mNotificationMessage);
                if (!TextUtils.isEmpty(notification.mNotificationDateTime))
                    ((TextView) findViewById(R.id.text_notification_date)).setText(notification.mNotificationDateTime);
                else
                    findViewById(R.id.text_notification_date).setVisibility(View.GONE);
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG)
                e.printStackTrace();
        }
    }
}
