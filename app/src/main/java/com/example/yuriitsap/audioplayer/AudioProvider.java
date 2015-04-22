package com.example.yuriitsap.audioplayer;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by yuriitsap on 21.04.15.
 */
public class AudioProvider extends ContentProvider {

    public static final String AUTHORITY = "com.example.yuriitsap.audioplayer.AudioProvider";
    public static final Uri BASE_URL = Uri.parse("content://" + AUTHORITY);
    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URL, AudioContract.TABLE_NAME);
    private SQLiteDatabase mSQLiteDatabase;
    private SQLiteOpenHelper mSQLiteOpenHelper;

    public static class AudioContract implements BaseColumns {

        public static final String DATABASE_NAME = "audio_player_database";
        public static final String TABLE_NAME = "playlist";
        public static final String TITLE = "title";
        public static final String ARTIST = "artist";
        public static final String DURATION = "duration";
        public static final String CREATE_PLAYLIST_TABLE = "CREATE TABLE "
                + TABLE_NAME + "(" + _ID + " INTEGER PRIMARY KEY," +
                TITLE + " TEXT," +
                ARTIST + " TEXT," +
                DURATION + " REAL)";
        public static final int DATABASE_VERSION = 1;
    }


    @Override
    public boolean onCreate() {
        mSQLiteOpenHelper = new MyDatabaseHelper(getContext());

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        mSQLiteDatabase = mSQLiteOpenHelper.getReadableDatabase();
        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private class MyDatabaseHelper extends SQLiteOpenHelper {

        public MyDatabaseHelper(Context context) {
            super(context, AudioContract.DATABASE_NAME, null, AudioContract.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(AudioContract.CREATE_PLAYLIST_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + AudioContract.TABLE_NAME);
            onCreate(db);

        }
    }
}
