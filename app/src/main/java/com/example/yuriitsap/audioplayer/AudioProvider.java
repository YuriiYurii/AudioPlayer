package com.example.yuriitsap.audioplayer;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

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
    private OrmLiteDatabaseHelper mOrmLiteDatabaseHelper;


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
        public static final int DATABASE_VERSION = 14;
    }


    @Override
    public boolean onCreate() {
        mOrmLiteDatabaseHelper = OrmLiteDatabaseHelper.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        Dao<Song, Integer> songIntegerDao = mOrmLiteDatabaseHelper.getSongDao();
        QueryBuilder<Song, Integer> builder = songIntegerDao.queryBuilder();
        CloseableIterator<Song> iterator = null;
        Cursor cursor = null;
        switch (URI_MATCHER.match(uri)) {
            case ITEM_LIST:
                try {
                    iterator = songIntegerDao.iterator(builder.prepare());
                    cursor = ((AndroidDatabaseResults) iterator.getRawResults()).getRawCursor();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }

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

    public static class OrmLiteDatabaseHelper extends OrmLiteSqliteOpenHelper {

        private static volatile OrmLiteDatabaseHelper mInstance;
        private final int mImages[] = {R.drawable.placebo, R.drawable.ac_dc,
                R.drawable.arctic_monkeys, R.drawable.johny_cash};
        private Dao<Song, Integer> mSongDao = null;
        private Random mRandom = new Random();
        private final StringBuilder mDefaultSongUri = new StringBuilder(
                "android.resource://com.example.yuriitsap.audioplayer/" + String
                        .valueOf(R.raw.first));

        private OrmLiteDatabaseHelper(Context context) {
            super(context, AudioProvider.AudioContract.DATABASE_NAME, null,
                    AudioProvider.AudioContract.DATABASE_VERSION);
        }

        public static synchronized OrmLiteDatabaseHelper getInstance(Context context) {
            if (mInstance == null && context != null) {
                mInstance = new OrmLiteDatabaseHelper(context);
            }
            return mInstance;
        }

        @Override
        public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
            try {
                TableUtils.createTable(connectionSource, Song.class);
                getSongDao().callBatchTasks(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        for (Song song : generateDefaultPlaylist()) {
                            getSongDao().create(song);
                        }
                        return null;
                    }
                });
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource,
                int oldVersion, int newVersion) {
            try {
                TableUtils.dropTable(connectionSource, Song.class, true);
                onCreate(database, connectionSource);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public Dao<Song, Integer> getSongDao() {
            if (mSongDao == null) {
                try {
                    mSongDao = getDao(Song.class);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return mSongDao;
        }

        @Override
        public String toString() {
            return "Our helper = " + mRandom.nextInt(10000);
        }

        private List<Song> generateDefaultPlaylist() {
            LinkedList<Song> playlist = new LinkedList<>();
            for (int i = 100; i >= 0; --i) {
                playlist.add(
                        new Song().setArtist("Stepan Giga N" + i)
                                .setTitle("Awesome song about Ukraine")
                                .setDuration(180).setUri(mDefaultSongUri.toString()).setImageId(
                                mImages[mRandom.nextInt(mImages.length - 1)]));

            }
            return playlist;
        }
    }
}
