package com.whatstatus.DAL;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Use this class to get access to the database
 * Created by Gal Halali on 10/03/2017.
 */
public class StatusHelper  extends SQLiteOpenHelper {
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE t_people " +
                    "(cardId TEXT PRIMARY KEY," +
                    "cardNumber TEXT UNIQUE NOT NULL, " +
                    "firstName TEXT, " +
                    "lastName TEXT, " +
                    "phoneNumber TEXT, " +
                    "photo TEXT, " +
                    "rank TEXT, "+
                    "isPresentAndSafe INTEGER, "+
                    "isPresentGlobaly INTEGER)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS t_people";

    public static StatusHelper m_instance = null;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Status.db";

    private StatusHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static StatusHelper getInstance(Context context) {
        if (m_instance == null) {
            m_instance = new StatusHelper(context);
        }

        return m_instance;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
