package com.zeowls.noteowl.provider;

/**
 * Created by root on 4/20/16.
 */
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

public class Provider extends ContentProvider {

    static final int TASKS = 100;
    static final int LISTS = 101;

    private DBHelper mOpenHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    @Override
    public boolean onCreate() {
        mOpenHelper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case TASKS:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(Contract.TaskEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case LISTS: {
                retCursor = mOpenHelper.getReadableDatabase().query(Contract.ListEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case TASKS:
                return Contract.TaskEntry.CONTENT_TYPE;
            case LISTS:
                return Contract.ListEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case TASKS: {
                long _id = db.insert(Contract.TaskEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = Contract.TaskEntry.BuildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case LISTS: {
                long _id = db.insert(Contract.ListEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = Contract.ListEntry.BuildItemUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
//        db.close();
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnRow;
        if (selection == null) selection = "1";
        switch (match) {
            case TASKS: {
                returnRow = db.delete(Contract.TaskEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case LISTS: {
                returnRow = db.delete(Contract.ListEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (returnRow != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
//        db.close();
        return returnRow;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnRow;
        if (selection == null) selection = "1";
        switch (match) {
            case TASKS: {
                returnRow = db.update(Contract.TaskEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case LISTS: {
                returnRow = db.update(Contract.ListEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (returnRow != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
//        db.close();
        return returnRow;
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = Contract.CONTENT_AUTHORITY;
        matcher.addURI(authority,Contract.PATH_TASKS, TASKS);
        matcher.addURI(authority,Contract.PATH_LISTS, LISTS);
        return matcher;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TASKS:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(Contract.ListEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_IGNORE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
