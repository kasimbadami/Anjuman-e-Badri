package anjuman.e.badri;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

class NotificationsManager {

    private DBHelper mDBHelper;

    NotificationsManager(Context context) {
        mDBHelper = DBHelper.get_Instance(context);
    }

    void insertNotificationInDB(final String message, final String title,final String imageUrl) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    ContentValues contentValues = new ContentValues();

//                    System.out.println("Inserting notification in DB = " + message);

                    contentValues.put(Notifications._NOTIFICATIONTITLE, title);
                    contentValues.put(Notifications._NOTIFICATIONMESSAGE, message);
                    contentValues.put(Notifications._NOTIFICATIONIMAGEURL, imageUrl);

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy   hh:mm:ss a");
                    String formattedDate = simpleDateFormat.format(calendar.getTime());

                    contentValues.put(Notifications.NOTIFICATION_DATE, formattedDate);

                    mDBHelper.insert(Notifications._NOTIFICATIONS_TABLE_NAME, contentValues);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }


    ArrayList<NotificationPOJO> getNotificationFromDB() {

        try {
            Cursor cursor = mDBHelper.select(Notifications._NOTIFICATIONS_TABLE_NAME, null, null, null, null, null, Notifications._ID + " DESC");

            ArrayList<NotificationPOJO> list = null;
            if (cursor.moveToFirst()) {
                list = new ArrayList<>();
                do {
                    NotificationPOJO notificationPOJO = new NotificationPOJO();
                    notificationPOJO.mNotificationTitle = cursor.getString(cursor.getColumnIndex(Notifications._NOTIFICATIONTITLE));
                    notificationPOJO.mNotificationMessage = cursor.getString(cursor.getColumnIndex(Notifications._NOTIFICATIONMESSAGE));
                    notificationPOJO.mNotificationImageUrl = cursor.getString(cursor.getColumnIndex(Notifications._NOTIFICATIONIMAGEURL));
                    notificationPOJO.mNotificationDateTime = cursor.getString(cursor.getColumnIndex(Notifications.NOTIFICATION_DATE));

                    list.add(notificationPOJO);
                } while (cursor.moveToNext());
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    public int getNotificationCountFromDB() {
//        int count = 0;
//        try {
//            Cursor cursor = mDBHelper.select(Notifications._NOTIFICATIONS_TABLE_NAME, null, null, null,
//                    null, null, null);
//            if (cursor.moveToFirst())
//                count = cursor.getCount();
//            cursor.close();
//            return count;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return count;
//    }
}
