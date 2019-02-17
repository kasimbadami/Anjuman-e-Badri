package anjuman.e.badri;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    TextView mTextViewEmptyMessage;
    ProgressBar mProgressBarLoading;
    LinearLayout mLinearLayoutLoading;
    public static ArrayList<NotificationPOJO> mNotificationses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_notification);

            ActionBar supportActionBar = getSupportActionBar();

            if (supportActionBar != null) {

                SpannableString s = new SpannableString("Jamaat Notifications");
                s.setSpan(new TypefaceSpan(this, "calibri.ttf"), 0, s.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                supportActionBar.setTitle(s);
            }

            mRecyclerView = findViewById(R.id.recycler_view_notifications);
            mTextViewEmptyMessage = findViewById(R.id.empty_view);
            mProgressBarLoading = findViewById(R.id.progress_download);
            mLinearLayoutLoading = findViewById(R.id.loading_layout);

            mRecyclerView.setHasFixedSize(true);

            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
            mRecyclerView.setLayoutManager(layoutManager);


            mNotificationses = new ArrayList<>();

            new DisplayNotificationTask().execute();


        } catch (Exception e) {
            if (BuildConfig.DEBUG)
                e.printStackTrace();
        }
    }

    class DisplayNotificationTask extends AsyncTask<Void, Void, ArrayList<NotificationPOJO>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mRecyclerView.setVisibility(View.GONE);
            mLinearLayoutLoading.setVisibility(View.VISIBLE);
            mProgressBarLoading.setVisibility(View.VISIBLE);
            mTextViewEmptyMessage.setText(R.string.please_wait);
            mTextViewEmptyMessage.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<NotificationPOJO> doInBackground(Void... params) {
            return new NotificationsManager(NotificationActivity.this).getNotificationFromDB();
        }

        @Override
        protected void onPostExecute(ArrayList<NotificationPOJO> notificationPOJOs) {
            super.onPostExecute(notificationPOJOs);

            if (notificationPOJOs != null && notificationPOJOs.size() > 0) {

                mRecyclerView.setVisibility(View.VISIBLE);
                mLinearLayoutLoading.setVisibility(View.GONE);

                mNotificationses = notificationPOJOs;
                mRecyclerView.setAdapter(new NotificationsAdapter(NotificationActivity.this));
            } else {
                mRecyclerView.setVisibility(View.GONE);
                mLinearLayoutLoading.setVisibility(View.VISIBLE);
                mProgressBarLoading.setVisibility(View.GONE);
                mTextViewEmptyMessage.setText(R.string.no_notifications);
                mTextViewEmptyMessage.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (BuildConfig.DEBUG)
            Log.d("AMRUT", " called");

        mNotificationses.clear();

        new DisplayNotificationTask().execute();
    }

}
