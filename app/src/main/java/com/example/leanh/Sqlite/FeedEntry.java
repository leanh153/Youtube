package com.example.leanh.Sqlite;

import android.provider.BaseColumns;

/**
 * this class define database's information
 */
class FeedEntry {
    // database version
    public static final int DATABASE_VERSION = 1;
    // database name
    public static final String DB_NAME = "db_user.db";

    public static final class UserEntry implements BaseColumns {
        public static final String USER_TABLE_NAME = "dataUser";  // user table's name
        public static final String KEY_ID = "id";            // column id
        public static final String KEY_USER = "user";        // column userName
        public static final String KEY_PASSWORD = "password";// column password
    }

    public static final class HistoryEntry implements BaseColumns {
        public static final String KEY_VIDEO_ID = "videoId";// column video's id
        public static final String KEY_THUMBNAIL = "thumbnail";// column video's thumbnail url
        public static final String KEY_TITLE = "title";// column video's title
        public static final String KEY_DESCRIPTION = "description";// column video's description
    }
}
