package com.zeowls.noteowl.provider;

/**
 * Created by root on 4/20/16.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zeowls.noteowl.provider.Contract.TaskEntry;
import com.zeowls.noteowl.provider.Contract.ListEntry;

public class DBHelper extends SQLiteOpenHelper {
    private static DBHelper instance;

    public final static String DATABASE_NAME = "noteOwl.db";

    private static int DATABASE_VERSION = 1;

    public static synchronized DBHelper getInstance(Context context)
    {
        if (instance == null) {
            instance = new DBHelper(context);
        }

        return instance;
    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_TASK_TABLE = "CREATE TABLE " + TaskEntry.TABLE_NAME + " ( " +

                TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TaskEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                TaskEntry.COLUMN_IS_FINISHED + " INTEGER NOT NULL DEFAULT 0 , " +
                TaskEntry.COLUMN_IS_REPEATED + " INTEGER NOT NULL DEFAULT 0 , " +
                TaskEntry.COLUMN_IS_TIMED + " INTEGER NOT NULL DEFAULT 0 ," +
                TaskEntry.COLUMN_IS_DATED + " INTEGER NOT NULL DEFAULT 0 ," +
                TaskEntry.COLUMN_DATE + " INTEGER ," +
                TaskEntry.COLUMN_TIME + " INTEGER ," +
                TaskEntry.COLUMN_DETAILS + " TEXT ," +
                TaskEntry.COLUMN_LIST_ID + " INTEGER NOT NULL," +
                TaskEntry.COLUMN_REPEATED_PERIOD + " INTEGER ," +
                TaskEntry.COLUMN_REPEATED_PERIOD_NUM + " INTEGER ," +
                " FOREIGN KEY (" + TaskEntry.COLUMN_LIST_ID + ") REFERENCES " +
                ListEntry.TABLE_NAME + " (" + ListEntry._ID + ")" +
                ");";

        final String SQL_CREATE_LIST_TABLE = "CREATE TABLE " + ListEntry.TABLE_NAME + " ( " +

                ListEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ListEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                ListEntry.COLUMN_TASKS_COUNT + " INTEGER NOT NULL DEFAULT 0 , " +
                ListEntry.COLUMN_IS_USER_DEFINED + " INTEGER NOT NULL DEFAULT 0 " +
                ");";

        db.execSQL(SQL_CREATE_TASK_TABLE);
        db.execSQL(SQL_CREATE_LIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TaskEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ListEntry.TABLE_NAME);
        onCreate(db);
    }}
