package com.zeowls.noteowl.provider;

/**
 * Created by root on 4/20/16.
 */
import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class Contract {

    public static final String CONTENT_AUTHORITY = "com.zeowls.noteowl";
    public static final Uri BASE_CONTENT = Uri.parse("content://"+ CONTENT_AUTHORITY);
    public static final String PATH_TASKS = "tasks";
    public static final String PATH_LISTS = "lists";

    public static final class TaskEntry implements BaseColumns {
        public static Uri CONTENT_URI = BASE_CONTENT.buildUpon().appendPath(PATH_TASKS).build();
        public static String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TASKS;
        public static String TABLE_NAME = "tasks";
        public static String COLUMN_NAME = "task_name";
        public static String COLUMN_IS_FINISHED = "isFinished";
        public static String COLUMN_IS_REPEATED = "isRepeated";
        public static String COLUMN_IS_TIMED = "isTimed";
        public static String COLUMN_IS_DATED = "isDated";
        public static String COLUMN_DAY = "day";
        public static String COLUMN_MONTH = "month";
        public static String COLUMN_YEAR = "year";
        public static String COLUMN_HOUR = "hour";
        public static String COLUMN_MINUTE = "minute";
        public static String COLUMN_LIST_ID = "listID";
        public static String COLUMN_REPEATED_PERIOD = "repeatPeriod";
        public static String COLUMN_REPEATED_PERIOD_NUM = "repeatPeriodNum";

        public static Uri BuildMovieUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

    }

    public static final class ListEntry implements BaseColumns {
        public static Uri CONTENT_URI = BASE_CONTENT.buildUpon().appendPath(PATH_LISTS).build();
        public static String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LISTS;
        public static String TABLE_NAME = "lists";
        public static String COLUMN_NAME = "list_name";
        public static String COLUMN_TASKS_COUNT = "count";
        public static String COLUMN_IS_USER_DEFINED = "isUserDefined";

        public static Uri BuildItemUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static Uri BuildItemShopwUri(String shop){
            return CONTENT_URI.buildUpon().appendPath(shop).build();
        }

    }
}
