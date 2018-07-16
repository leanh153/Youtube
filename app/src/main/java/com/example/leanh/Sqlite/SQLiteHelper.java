package com.example.leanh.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.leanh.model.User;
import com.example.leanh.model.Video;

import java.util.ArrayList;
import java.util.List;

/**
 * this class help other class manipulate with the
 * database
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHelper.class.getSimpleName();
    private static SQLiteHelper mHelper;

    private SQLiteHelper(Context context) {
        super(context, FeedEntry.DB_NAME, null, FeedEntry.DATABASE_VERSION);
    }

    // get instance of SQLiteHelper class
    public static SQLiteHelper getInstance(Context context) {// singleOn in JAVA
        if (mHelper == null) {
            mHelper = new SQLiteHelper(context);
        }
        return mHelper;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // this create table to store user data
        String CREATE_TABLE = "CREATE TABLE " + FeedEntry.UserEntry.USER_TABLE_NAME + " ("
                + FeedEntry.UserEntry.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FeedEntry.UserEntry.KEY_USER + " TEXT, "
                + FeedEntry.UserEntry.KEY_PASSWORD + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FeedEntry.UserEntry.USER_TABLE_NAME);
        onCreate(db);
    }

    /**
     * this method not only add user to table but also create new table to
     * store user's watching history and it's name is the same userName
     * the variable insert return the row's index of user, return -1 if inserting false
     */
    public long addUser(User user) {
        long insert = -1;
        try {
            // ContentValues like a box to contain value in there
            ContentValues values = new ContentValues();
            // put value to each column
            values.put(FeedEntry.UserEntry.KEY_USER, user.getUserName());
            values.put(FeedEntry.UserEntry.KEY_PASSWORD, user.getPassWord());
            SQLiteDatabase db = this.getWritableDatabase();
            // insert to table
            insert = db.insert(FeedEntry.UserEntry.USER_TABLE_NAME, null, values);
            // Why use "'" + user.getUserName() + "'" instead of + user.getUserName()+ because the
            // user name contain number or specific symbol then create table will return error
            String CREATE_TABLE_HISTORY = "CREATE TABLE " + "'" + user.getUserName() + "'" + " ("
                    + FeedEntry.HistoryEntry.KEY_VIDEO_ID + " TEXT, "
                    + FeedEntry.HistoryEntry.KEY_TITLE + " TEXT, "
                    + FeedEntry.HistoryEntry.KEY_DESCRIPTION + " TEXT, "
                    + FeedEntry.HistoryEntry.KEY_THUMBNAIL + " TEXT)";
            db.execSQL(CREATE_TABLE_HISTORY);
        } catch (Exception e) {
            Log.e(TAG, "addUser Exception  " + e);
        }
        return insert;
    }


    /**
     * this method check whether the user, password exist and return one integer.
     * case contain == -1, user not existed
     * case contain == 1 userName exist and password is incorrect
     * case contain == 2 user existed
     */
    public int isUserExist(String username, String password) {
        int exist = -1;
        // select two column from database
        String where = "SELECT * FROM " + FeedEntry.UserEntry.USER_TABLE_NAME + " WHERE "
                + FeedEntry.UserEntry.KEY_USER + "='" + username + "'";
        Cursor cursor = null;
        try {
            // get read
            SQLiteDatabase db = this.getReadableDatabase();
            // get cursor
            cursor = db.rawQuery(where, null);
            // cursor move to first true which mean userName existed
            if (cursor.moveToFirst()) {
                do {
                    exist = 1;
                    String passWordBase = cursor.getString(2);
                    if (passWordBase.equals(password)) {
                        exist = 2;
                    }

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "isUserExist Exception  " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return exist;
    }

    /**
     * this method save data to the table name same the userName and also
     * use "'" + userName + "'" instead of +userName+ to prevent error
     */
    public void addVideo(Video video, String userName) {
        boolean exist = isVideoExist(video, userName);
        if (!exist) {
            // ContentValues like a box to contain value in there
            ContentValues values = new ContentValues();
            values.put(FeedEntry.HistoryEntry.KEY_VIDEO_ID, video.getVideoId());
            values.put(FeedEntry.HistoryEntry.KEY_TITLE, video.getTitle());
            values.put(FeedEntry.HistoryEntry.KEY_DESCRIPTION, video.getDescription());
            values.put(FeedEntry.HistoryEntry.KEY_THUMBNAIL, video.getThumbnail());
            SQLiteDatabase db = this.getWritableDatabase();
            db.insert("'" + userName + "'", null, values);
        }
    }

    /**
     * this method check the video exist in the table userName
     * or not
     */
    private boolean isVideoExist(Video video, String userName) {
        boolean exist = false;
        Cursor cursor = null;
        // select all all columns from table userName where KEY_VIDEO_ID = video'id
        String where = "SELECT * FROM " + "'" + userName + "'" + " WHERE "
                + FeedEntry.HistoryEntry.KEY_VIDEO_ID + "='" + video.getVideoId() + "'";
        try {
            // gey read database
            SQLiteDatabase db = this.getReadableDatabase();
            cursor = db.rawQuery(where, null);
            // if cursor move to first which mean video existed in the database
            if (cursor.moveToFirst()) {
                exist = true;
            }
        } catch (Exception e) {
            Log.e(TAG, "isVideoExist Exception  " + e);
        } finally {
            // always close cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        return exist;
    }

    /**
     * this method get User's video history by userName. this loop all row in table
     * to get data.
     */
    public List<Video> getVideosByUser(String userName) {
        Cursor cursor = null;
        List<Video> videoList = new ArrayList<>();
        try {
            // get reade
            SQLiteDatabase db = this.getReadableDatabase();
            String where = "SELECT * FROM " + "'" + userName + "'";
            cursor = db.rawQuery(where, null);
            if (cursor.moveToFirst()) {

                do {
                    Video video = new Video();
                    // get the column index of the user's watching history
                    int videoIdColumnIndex = cursor.getColumnIndex(FeedEntry.HistoryEntry.KEY_VIDEO_ID);
                    int thumbnailColumnIndex = cursor.getColumnIndex(FeedEntry.HistoryEntry.KEY_THUMBNAIL);
                    int titleColumnIndex = cursor.getColumnIndex(FeedEntry.HistoryEntry.KEY_TITLE);
                    int descriptionColumnIndex = cursor.getColumnIndex(FeedEntry.HistoryEntry.KEY_DESCRIPTION);
                    // set video's data
                    video.setVideoId(cursor.getString(videoIdColumnIndex));
                    video.setTitle(cursor.getString(titleColumnIndex));
                    video.setDescription(cursor.getString(descriptionColumnIndex));
                    video.setThumbnail(cursor.getString(thumbnailColumnIndex));
                    videoList.add(video);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "getVideosByUser Exception  " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return videoList;

    }
}
