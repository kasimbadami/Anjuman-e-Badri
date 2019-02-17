package anjuman.e.badri;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyInstanceIDListenerService extends FirebaseInstanceIdService {

    private static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    static final String GCM_TOKEN = "gcmToken";
    static String REGISTRATION_ID;
    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            // Get updated InstanceID token.
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            if (BuildConfig.DEBUG)
                Log.d(TAG, "@@@Refreshed token: " + refreshedToken);

            sharedPreferences.edit().putString(GCM_TOKEN, refreshedToken).apply();

            // Send token to server
            sendRegistrationToServer(refreshedToken);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();

                Log.d(TAG, "Failed to complete token refresh", e);
            }
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, false).apply();
        }
    }

    /**
     * @param token -
     */
    private void sendRegistrationToServer(String token) {

        // Add custom implementation, as needed.
        if (BuildConfig.DEBUG)
            Log.d(TAG, "@@@In sendRegistrationToServer()");

        // send network request
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("deviceId", token);
            jsonObject.put("itsid", REGISTRATION_ID);

            String result = register(MainActivity.mRegisterURL, jsonObject.toString());
            if (BuildConfig.DEBUG)
                Log.d(TAG, "@@@Registration result -> " + result);
            if (TextUtils.isEmpty(result)) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, false).apply();
            } else {
                // if registration sent was successful, store a boolean that indicates whether the generated token has been sent to server
                JSONObject jsonObject1 = new JSONObject(result);
                if (jsonObject1.has("status")) {
                    String status = jsonObject1.getString("status");
                    if (!TextUtils.isEmpty(status) && status.equalsIgnoreCase("success")) {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "@@@Registration success");
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                        sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, true).apply();
                    } else {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "@@@Registration failure");
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                        sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, false).apply();
                    }
                } else {
                    if (BuildConfig.DEBUG)
                        Log.d(TAG, "@@@Registration failure");
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                    sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, false).apply();
                }
            }
        } catch (JSONException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
                Log.d(TAG, "@@@Registration failure");
            }
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, false).apply();
        } catch (Exception e) {
            if (BuildConfig.DEBUG)
                e.printStackTrace();
        }
    }

    /**
     * @param urlString -
     * @param parameter -
     * @return -
     */
    private static String register(String urlString, String parameter) {

        URL url;
        try {

            url = new URL(urlString);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");

            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            connection.setDoInput(true);

            connection.setDoOutput(true);

            connection.setFixedLengthStreamingMode(parameter.length());

            connection.setReadTimeout(10000);

            connection.setConnectTimeout(45000);

            connection.connect();

            DataOutputStream wr;

            wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(parameter);
            wr.flush();
            wr.close();

            InputStream in = new BufferedInputStream(connection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            return result.toString();

        } catch (Exception e) {
            if (BuildConfig.DEBUG)
                e.printStackTrace();
            return null;
        }
    }
}
