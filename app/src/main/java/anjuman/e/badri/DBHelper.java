package anjuman.e.badri;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DataBaseHelper";
    private static DBHelper _Instance;
    private SQLiteDatabase mSqLiteDatabase;

    private static String DATABASE_NAME = "kasim.db";
    private static int DATABASE_VERSION = 1;

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        mSqLiteDatabase = this.getWritableDatabase();
    }

    static DBHelper get_Instance(Context context) {
        if (_Instance == null)
            _Instance = new DBHelper(context);

        // If database is close accidentally, it will reopen here
        if (!_Instance.mSqLiteDatabase.isOpen()) {
            _Instance.mSqLiteDatabase = _Instance.getWritableDatabase();
        }
        return _Instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Notifications.CREATE_NOTIFICATIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "updating database from " + oldVersion + " to" + newVersion);

        switch (db.getVersion()) {
            case 1:
                // upgrade logic from version 1 to 2
                // create new table with changed column Name
                // copy data from old table to new table
                // drop old table

                break;
            case 2:
                // upgrade logic from version 2 to 3
                break;
        }
    }

    long insert(String table_name, ContentValues contentValues) {
        return mSqLiteDatabase.insert(table_name, null, contentValues);
    }

//    public int update(String table_name, ContentValues contentValues, String whereClasue, String[] whereArgs) {
//        return mSqLiteDatabase.update(table_name, contentValues, whereClasue, whereArgs);
//    }

//    public int delete(String table_name, String whereClause, String[] whereArgs) {
//        return mSqLiteDatabase.delete(table_name, whereClause, whereArgs);
//    }

    Cursor select(String table_name, String[] columns, String selections, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return mSqLiteDatabase.query(table_name, columns, selections, selectionArgs, groupBy, having, orderBy);
    }

//    public Cursor execSQL(String sql, String[] selectionArgs) {
//        return mSqLiteDatabase.rawQuery(sql, selectionArgs);
//    }

//    /**
//     * Close database
//     */
//    public void closeDb() {
//        mSqLiteDatabase.close();
//    }
}
