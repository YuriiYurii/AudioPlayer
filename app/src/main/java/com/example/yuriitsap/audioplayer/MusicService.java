package com.example.yuriitsap.audioplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuriitsap on 17.04.15.
 */
public class MusicService extends Service {

    private MediaPlayer mMediaPlayer;
    private int mPosition;
    private ArrayList<Song> mSongs;
    public IMyAidlInterface.Stub mStub = new IMyAidlInterface.Stub() {
        @Override
        public List<Song> getPlaylist() throws RemoteException {
            return mSongs;
        }

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
                double aDouble,
                String aString) throws RemoteException {

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        initPlaylist();
        mPosition = 0;
        mMediaPlayer = new MediaPlayer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mStub;
    }

    private void initPlaylist() {
//        Uri.parse("android.resource://com.my.package/" + R.raw.first);
        mSongs = new ArrayList<>();
        mSongs.add(new Song(R.raw.first).setName("Song 1").setDuration(200000));
        mSongs.add(new Song(R.raw.second).setName("Song 2").setDuration(200000));
        mSongs.add(new Song(R.raw.third).setName("Song 3").setDuration(200000));
    }
}
