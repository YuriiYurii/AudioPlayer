package com.example.yuriitsap.audioplayer;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by yuriitsap on 21.04.15.
 */
public class AudioProvider extends ContentProvider {

    public static final String AUTHORITY = "com.example.yuriitsap.audioplayer.AudioProvider";
    public static final Uri BASE_URL = Uri.parse("content://" + AUTHORITY);
    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URL, AudioContract.TABLE_NAME);

    public static class AudioContract implements BaseColumns {

        public static final String DATABSE_NAME = "audio_player_database";
        public static final String TABLE_NAME = "playlist";
        public static final String TITLE = "title";
        public static final String ARTIST = "artist";
        public static final String DURATION = "duration";


    }


    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
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
}
