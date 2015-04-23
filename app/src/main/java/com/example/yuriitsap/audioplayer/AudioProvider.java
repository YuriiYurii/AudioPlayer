package com.example.yuriitsap.audioplayer;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.Random;

/**
 * Created by yuriitsap on 21.04.15.
 */
public class AudioProvider extends ContentProvider {

    public static final String AUTHORITY = "com.example.yuriitsap.audioplayer.AudioProvider";
    public static final Uri BASE_URL = Uri.parse("content://" + AUTHORITY);
    public static final Uri CONTENT_URI = Uri
            .withAppendedPath(BASE_URL, AudioContract.TABLE_NAME);
    public static final Uri PLAYLIST_CONTENT_URI = Uri
            .withAppendedPath(BASE_URL, AudioContract.ALL_ITEMS_PLAYLIST);
    private static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.yuriitsap.audio/songs";
    private static final int ITEM_LIST = 1;
    private static final UriMatcher URI_MATCHER;
    private SQLiteDatabase mSQLiteDatabase;
    private SQLiteOpenHelper mSQLiteOpenHelper;
    private final int mImages[] = {R.drawable.placebo, R.drawable.ac_dc,
            R.drawable.arctic_monkeys, R.drawable.johny_cash};

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AUTHORITY, AudioContract.ALL_ITEMS_PLAYLIST, ITEM_LIST);
    }

    public static class AudioContract implements BaseColumns {

        public static final String DATABASE_NAME = "audio_player_database";
        public static final String ALL_ITEMS_PLAYLIST = "items";
        public static final String TABLE_NAME = "playlist";
        public static final String ARTIST = "artist";
        public static final String TITLE = "title";
        public static final String DURATION = "duration";
        public static final String IMAGE_ID = "image_id";
        public static final String CREATE_PLAYLIST_TABLE = "CREATE TABLE "
                + TABLE_NAME + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ARTIST + " TEXT," +
                TITLE + " TEXT," +
                DURATION + " REAL," +
                IMAGE_ID + " REAL)";
        public static final int DATABASE_VERSION = 3;
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

        switch (URI_MATCHER.match(uri)) {
            case ITEM_LIST:
                sqLiteQueryBuilder.setTables(AudioContract.TABLE_NAME);
        }

        Cursor cursor = sqLiteQueryBuilder
                .query(mSQLiteDatabase, projection, selection, selectionArgs, null, null,
                        sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case ITEM_LIST:
                return CONTENT_TYPE;
            default:
                return null;
        }
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

        private static final String INSERT_STATEMENT
                = "Insert into playlist (artist,title,duration,image_id) values(?,?,?,?)";

        public MyDatabaseHelper(Context context) {
            super(context, AudioContract.DATABASE_NAME, null, AudioContract.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Random random = new Random();
            db.execSQL(AudioContract.CREATE_PLAYLIST_TABLE);
            SQLiteStatement insert = db.compileStatement(INSERT_STATEMENT);
            db.beginTransaction();
            for (int i = 500; i > 0; i--) {
                insert.bindString(1, "Stepan Giga N#" + i);
                insert.bindString(2, "Stepan Giga N#" + i);
                insert.bindLong(3, i * 60);
                insert.bindLong(4,
                        mImages[random.nextInt(mImages.length - 1)]);
                insert.execute();
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + AudioContract.TABLE_NAME);
            onCreate(db);

        }
    }
}
