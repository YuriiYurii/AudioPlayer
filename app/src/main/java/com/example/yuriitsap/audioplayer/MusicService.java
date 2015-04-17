package com.example.yuriitsap.audioplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.ArrayList;

/**
 * Created by yuriitsap on 17.04.15.
 */
public class MusicService extends Service {

    private MediaPlayer mMediaPlayer;
    private int mPosition;
    private ArrayList<Song> mSongs;

    @Override
    public void onCreate() {
        super.onCreate();
        mPosition = 0;
        mMediaPlayer = new MediaPlayer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
