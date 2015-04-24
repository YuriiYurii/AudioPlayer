package com.example.yuriitsap.audioplayer;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.Random;

/**
 * Created by yuriitsap on 24.04.15.
 */
public class OrmLiteDatabaseHelper extends OrmLiteSqliteOpenHelper {

    private Dao<Song, Integer> mSongDao = null;
    private static volatile OrmLiteDatabaseHelper mInstance;
    private Random mRandom = new Random();

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
        } catch (SQLException e) {
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
}
