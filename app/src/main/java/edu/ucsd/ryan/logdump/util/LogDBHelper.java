package edu.ucsd.ryan.logdump.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import edu.ucsd.ryan.logdump.data.LogSchema;

/**
 * Created by ryan on 1/12/15.
 */
public class LogDBHelper extends SQLiteOpenHelper {

    public final static String DBNAME = "logs.db";
    public final static int DBVER = 1;

    private final static String TAG = "LogDBHelper";

    public final static String SQL_CREATE_LOG = "CREATE TABLE " +
            LogSchema.TABLE_NAME + "(" +
            LogSchema._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            LogSchema.COLUMN_PKGNAME + " TEXT," +
            LogSchema.COLUMN_APP + " TEXT," +
            LogSchema.COLUMN_LEVEL + " TEXT," +
            LogSchema.COLUMN_TAG + " TEXT," +
            LogSchema.COLUMN_TIME + " INTEGER," +
            LogSchema.COLUMN_TEXT + " TEXT" +
            ");";

    public final static String SQL_DROP_LOG = "DROP TABLE IF EXISTS " +
            LogSchema.TABLE_NAME;


    public static String getLevelFilterSelect(LogLevel level) {
        if (level == null || level.equals(LogLevel.UNKNOWN))
            return null;
        int ordinal = level.ordinal();
        StringBuilder sb = new StringBuilder();
        for (; ordinal < LogLevel.values().length; ++ordinal) {
            sb.append(LogSchema.COLUMN_LEVEL);
            sb.append("='");
            sb.append(LogLevel.getLevelLetter(ordinal));
            sb.append("'");
            if (ordinal != LogLevel.values().length - 1)
                sb.append(" OR ");
        }
        return sb.toString();
    }

    public LogDBHelper(Context context) {
        super(context, DBNAME, null, DBVER);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_LOG);
        Log.d(TAG, "Table " + LogSchema.TABLE_NAME + " created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_LOG);
        db.execSQL(SQL_CREATE_LOG);
        Log.d(TAG, "Table " + LogSchema.TABLE_NAME + " upgraded");
    }
}
