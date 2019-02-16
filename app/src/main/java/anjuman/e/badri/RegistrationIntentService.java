package anjuman.e.badri;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegistrationIntentService extends IntentService {

    // abbreviated tag name
    private static final String TAG = "RegIntentService";
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_id_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Anjuman-e-Badri",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            sendRegistrationToServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    private void sendRegistrationToServer() {

        // send network request
        Log.d(TAG, "@@@In sendRegistrationToServer()");
        JSONObject jsonObject = new JSONObject();
        try {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            jsonObject.put("deviceId", sp.getString(MyInstanceIDListenerService.GCM_TOKEN, ""));
            jsonObject.put("itsid", MyInstanceIDListenerService.REGISTRATION_ID);

            String result = register(MainActivity.mRegisterURL, jsonObject.toString());
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
                        Log.d(TAG, "@@@Registration success");
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                        sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, true).apply();
                    } else {
                        Log.d(TAG, "@@@Registration failure");
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                        sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, false).apply();
                    }
                } else {
                    Log.d(TAG, "@@@Registration failure");
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                    sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, false).apply();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "@@@Registration failure");
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, false).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String register(String urlString, String parameter) {

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
            e.printStackTrace();
            return null;
        }
    }
}
