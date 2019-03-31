package anjuman.e.badri;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class WebViewFragment extends Fragment {

    public static final String TAG = "WebViewFragment";
    private static WebView mWebView;
    private TextView mEmptyView;
    private MainActivity activity;
    private ProgressDialog mProgressDialog;
    private FloatingActionButton mNotificationActionButton;
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";

    public WebViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        activity = (MainActivity) getActivity();

    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {

            View viewGroup = inflater.inflate(R.layout.fragment_web_view, container, false);

            mEmptyView = viewGroup.findViewById(R.id.no_internet);
            mNotificationActionButton = viewGroup.findViewById(R.id.notification_id);
            mWebView = viewGroup.findViewById(R.id.web_view);

            CookieManager.getInstance().setAcceptCookie(true);

            WebSettings webSettings = mWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDisplayZoomControls(false);
            webSettings.setSupportZoom(false);
            webSettings.setBuiltInZoomControls(false);
            webSettings.setUseWideViewPort(true);

            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

            mWebView.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {

                    super.onPageStarted(view, url, favicon);

                    MainActivity.mURL = url;

                    if (getActivity() != null) {
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("anjuman.e.badri", Context.MODE_PRIVATE);

                        sharedPreferences.edit().putString("URL", url).apply();
                    }

                    boolean isProgressVisible = false;
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        isProgressVisible = true;

                    if (!isProgressVisible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            if (!activity.isDestroyed()) {
                                mProgressDialog = ProgressDialog.show(activity, "Please Wait!", "Loading...");
                            }
                        } else {
                            mProgressDialog = ProgressDialog.show(activity, "Please Wait!", "Loading...");
                        }
                    }
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    super.onReceivedError(view, request, error);

                    if (mProgressDialog != null)
                        mProgressDialog.dismiss();

                    if (getActivity() != null && isAdded()) {
                        mWebView.setVisibility(View.GONE);
                        mEmptyView.setVisibility(View.VISIBLE);
                        mEmptyView.setText(R.string.webpage_load_error);
                        mNotificationActionButton.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    if (mProgressDialog != null)
                        mProgressDialog.dismiss();

                    mWebView.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);


                    if (!TextUtils.isEmpty(view.getTitle())) {
                        String title = view.getTitle().toLowerCase();

                        if (getActivity() != null && isAdded()) {
                            if (title.equalsIgnoreCase(getString(R.string.app_name)))
                                mNotificationActionButton.setVisibility(View.GONE);
                            else
                                mNotificationActionButton.setVisibility(View.VISIBLE);

                            if (title.equalsIgnoreCase("Dashboard")) {
                                String urlSeparated[] = url.split("/");
                                if (BuildConfig.DEBUG)
                                    Log.d(TAG, "@@@Url -> " + url.toString());
                                if (urlSeparated.length > 1)
                                    GcmMessageHandler.REGISTRATION_ID = urlSeparated[urlSeparated.length - 1];

                                if (getActivity() != null) {
                                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                    boolean aBoolean = sharedPreferences.getBoolean(RegistrationIntentService.SENT_TOKEN_TO_SERVER, false);
                                    if (!aBoolean) {

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            getActivity().startForegroundService(new Intent(getActivity(), RegistrationIntentService.class));
                                        } else {
                                            getActivity().startService(new Intent(getActivity(), RegistrationIntentService.class));
                                        }

//                                    getActivity().startService(new Intent(getActivity(), RegistrationIntentService.class));
                                    }
                                }
                            }
                        }
                    }
                }
            });
            if (getActivity() != null && isAdded()) {
                mWebView.setNetworkAvailable(isNetworkConnected(getActivity()));

                if (isNetworkConnected(getActivity())) {
                    mWebView.loadUrl(MainActivity.mURL);
                    mWebView.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                } else {
                    mWebView.setVisibility(View.GONE);
                    mNotificationActionButton.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                    if (isAdded())
                        mEmptyView.setText(getString(R.string.no_internet));
                }

                mNotificationActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (activity != null)
                            activity.startActivity(new Intent(activity, NotificationActivity.class));
                    }
                });
            }
            return viewGroup;
        } catch (Exception e) {
            if (BuildConfig.DEBUG)
                e.printStackTrace();
            return null;
        }
    }

    public static boolean canGoBack() {
        if (mWebView != null)
            return mWebView.canGoBack();
        return false;

    }

    public static void goBack() {
        if (mWebView != null)
            mWebView.goBack();
    }

    public boolean isNetworkConnected(Activity activity) {

        if (activity == null)
            return false;

        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        return networkInfo != null && networkInfo.isConnected();
    }
}
