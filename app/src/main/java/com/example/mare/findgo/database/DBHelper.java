package com.example.mare.findgo.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.mare.findgo.database.TablesDeclaration.Friends;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 11;
    public static final String DATABASE_NAME = "ZZTop.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String NUMERIC_TYPE = " NUMERIC";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String SQL_CREATE_FRIENDS = "CREATE TABLE "
            + Friends.TABLE_NAME + " (" + Friends._ID
            + " INTEGER PRIMARY KEY," + Friends.COLUMN_NAME_FRIENDS_ID
            + INTEGER_TYPE + COMMA_SEP + Friends.COLUMN_NAME_FRIENDS_IME
            + TEXT_TYPE + COMMA_SEP + Friends.COLUMN_NAME_FRIENDS_PREZIME
            + TEXT_TYPE + COMMA_SEP + Friends.COLUMN_NAME_FRIENDS_USERNAME
            + TEXT_TYPE + COMMA_SEP +
            // Any other options for the CREATE command
            " )";

    private static final String SQL_DELETE_MESSAGES = "DROP TABLE IF EXISTS "
            + Friends.TABLE_NAME;

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_FRIENDS);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy
        // is
        // to simply to discard the data and start over
        db.execSQL(SQL_CREATE_FRIENDS);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
}
