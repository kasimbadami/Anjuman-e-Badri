package mumineen.connect.app;

public class Notifications {

    static final String _ID = "_id";
    static final String _NOTIFICATIONTITLE = "notificationTitle";
    static final String _NOTIFICATIONMESSAGE = "notificationmessage";
    static final String _NOTIFICATIONIMAGEURL = "notificationimageurl";
    static final String NOTIFICATION_DATE = "notificationDate";
    static final String _NOTIFICATIONS_TABLE_NAME = "NOTIFICATIONS";


    /* -------------- Start - Notifications  Table ----------------- */
    static final String CREATE_NOTIFICATIONS_TABLE = "CREATE TABLE IF NOT EXISTS " + _NOTIFICATIONS_TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            _NOTIFICATIONTITLE + " VARCHAR , " +
            _NOTIFICATIONMESSAGE + " VARCHAR , " +
            _NOTIFICATIONIMAGEURL + " VARCHAR , " +
            NOTIFICATION_DATE + " VARCHAR " +
            ")";

    public static final String DROP_NOTIFICATIONS_TABLE = "DROP TABLE IF EXISTS NOTIFICATIONS";
    /* -------------- End - Notifications Table ----------------- */

    public Notifications(int notificationId, String mNotificationTitle, String mNotificationMessage, String mNotificationDateTime, String imageUrl) {
        super();

        this.mNotificationId = notificationId;
        this.mNotificationTitle = mNotificationTitle;
        this.mNotificationMessage = mNotificationMessage;
        this.mNotificationDateTime = mNotificationDateTime;
        this.mNotificationImageUrl = imageUrl;
    }

    /**
     * Default Notifications Constructor
     */
    public Notifications() {
        super();
    }

    private int mNotificationId;
    private String mNotificationTitle;
    private String mNotificationMessage;
    private String mNotificationImageUrl;
    private String mNotificationDateTime;
}
