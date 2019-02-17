package anjuman.e.badri;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class GcmMessageHandler extends FirebaseMessagingService {

    private static final String TAG = GcmMessageHandler.class.getSimpleName();
    private static final String PUSH_NOTIFICATION = "pushNotification";
    private NotificationUtils notificationUtils;


    private static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    static final String GCM_TOKEN = "gcmToken";
    static String REGISTRATION_ID;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            // Get updated InstanceID token.
//            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            if (BuildConfig.DEBUG)
                Log.d(TAG, "@@@Refreshed token: " + s);

            sharedPreferences.edit().putString(GCM_TOKEN, s).apply();

            // Send token to server
            sendRegistrationToServer(s);

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

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            }
            // Create pending intent
            createNotification("Anjuman-e-Badri", remoteMessage.getNotification().getBody(), "");

            // Save to notification database
            NotificationsManager notificationsManager = new NotificationsManager(getBaseContext());
            notificationsManager.insertNotificationInDB(remoteMessage.getNotification().getBody(), "Anjuman-e-Badri", "");
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            if (BuildConfig.DEBUG)
                Log.e(TAG, "@@@Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                if (BuildConfig.DEBUG)
                    Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    /**
     * @param message -
     */
   /* private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        } else {
            // If the app is in background, firebase itself handles the notification
        }
    }*/
    private void handleDataMessage(JSONObject json) {
        if (BuildConfig.DEBUG)
            Log.e(TAG, "@@@push json: " + json.toString());

        try {
            JSONObject notification = json.getJSONObject("notification");
            String title = notification.optString("title");
            String message = notification.optString("body");
            String image = notification.optString("image");
            String picture = notification.optString("picture");
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "title: " + title);
                Log.e(TAG, "body: " + message);
                Log.e(TAG, "image: " + image);
                Log.e(TAG, "picture: " + picture);
            }
            // Create pending intent
            createNotification(title, message, image);

            // Save to notification database
            NotificationsManager notificationsManager = new NotificationsManager(getBaseContext());
            notificationsManager.insertNotificationInDB(message, title, image);

            /*if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                resultIntent.putExtra("message", message);

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }
            }*/
        } catch (JSONException e) {
            if (BuildConfig.DEBUG)
                Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            if (BuildConfig.DEBUG)
                Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }

    // Creates notification based on title and body received
    private void createNotification(String title, String body, String imageUrl) {
        try {

            if (TextUtils.isEmpty(imageUrl)) {
                Context context = getBaseContext();

                Intent notificationActivity = new Intent(context, NotificationActivity.class);

                NotificationManager mNotificationManager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);

                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationActivity, 0);

                NotificationCompat.Builder mBuilder;
                String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

                    notificationChannel.setDescription("Anjuman-e-Badri Notification Channel");
                    notificationChannel.enableLights(true);
                    notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                    notificationChannel.enableVibration(true);
                    if (mNotificationManager != null) {
                        mNotificationManager.createNotificationChannel(notificationChannel);
                    }

                    mBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
                } else {
                    mBuilder = new NotificationCompat.Builder(context);
                }
                mBuilder.setSmallIcon(R.drawable.ic_menu_alerts_push)
                        .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

                if (!TextUtils.isEmpty(body))
                    mBuilder.setContentText(body);

                if (!TextUtils.isEmpty(title))
                    mBuilder.setContentTitle(title);

                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                mBuilder.setLargeIcon(bm);

                mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);


                if (mNotificationManager != null) {
                    mNotificationManager.notify(new Random().nextInt(), mBuilder.build());
                }
            } else {
                Context context = getBaseContext();
                new sendNotification(context).execute(title, body, imageUrl);
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG)
                e.printStackTrace();
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


    private class sendNotification extends AsyncTask<String, Void, Bitmap> {

        Context context;
        String title;
        String body;
        String imageUrl;

        sendNotification(Context context) {
            super();
            this.context = context;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            InputStream in;
            title = params[0];
            body = params[1];
            imageUrl = params[2];

            try {

                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                return BitmapFactory.decodeStream(in);


            } catch (MalformedURLException e) {
                if (BuildConfig.DEBUG)
                    e.printStackTrace();
            } catch (IOException e) {
                if (BuildConfig.DEBUG)
                    e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            super.onPostExecute(result);
            try {

                NotificationManager mNotificationManager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);

                Intent notificationActivity = new Intent(context, NotificationActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationActivity, 0);

                NotificationCompat.Builder mBuilder;
                String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

                    notificationChannel.setDescription("Anjuman-e-Badri Notification Channel");
                    notificationChannel.enableLights(true);
                    notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                    notificationChannel.enableVibration(true);
                    if (mNotificationManager != null) {
                        mNotificationManager.createNotificationChannel(notificationChannel);
                    }

                    mBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
                } else {
                    mBuilder = new NotificationCompat.Builder(context);
                }
                mBuilder.setSmallIcon(R.drawable.ic_menu_alerts_push)
                        .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

                if (!TextUtils.isEmpty(body))
                    mBuilder.setContentText(body);

                if (!TextUtils.isEmpty(title))
                    mBuilder.setContentTitle(title);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if (result != null)
                        mBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(result));

                    Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                    mBuilder.setLargeIcon(bm);

                } else {
                    if (result != null)
                        mBuilder.setLargeIcon(result);
                    else {
                        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                        mBuilder.setLargeIcon(bm);
                    }
                }

                mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

                if (mNotificationManager != null) {
                    mNotificationManager.notify(new Random().nextInt(), mBuilder.build());
                }

            } catch (Exception e) {
                if (BuildConfig.DEBUG)
                    e.printStackTrace();
            }
        }
    }
}
