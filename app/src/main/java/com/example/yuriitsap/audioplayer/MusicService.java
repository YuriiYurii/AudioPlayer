package com.example.yuriitsap.audioplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import java.io.IOException;

/**
 * Created by yuriitsap on 17.04.15.
 */
public class MusicService extends Service
        implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private MediaPlayer mMediaPlayer;
    private RemoteCallbackList<IAsyncCallback> mIAsyncCallbackRemoteCallbackList
            = new RemoteCallbackList<>();
    private IMyAidlInterface.Stub mStub = new IMyAidlInterface.Stub() {
        @Override
        public void play(String uri) throws RemoteException {
            try {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(getApplicationContext(), Uri.parse(uri));
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void start() throws RemoteException {
            mMediaPlayer.start();
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return mMediaPlayer.getCurrentPosition();
        }

        @Override
        public void pause() throws RemoteException {
            mMediaPlayer.pause();
        }

        @Override
        public void registerCallback(IAsyncCallback callback) throws RemoteException {
            mIAsyncCallbackRemoteCallbackList.register(callback);
        }

        @Override
        public void unRegisterCallback(IAsyncCallback callback) throws RemoteException {

        }

        @Override
        public int getDuration() throws RemoteException {
            return mMediaPlayer.getDuration();
        }

        @Override
        public void seekTo(int position) throws RemoteException {
            mMediaPlayer.seekTo(position);
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return mMediaPlayer.isPlaying();
        }
    };

    @Override
    public void onCreate() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mStub;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        mMediaPlayer.stop();
        mMediaPlayer.release();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        try {
            for (int i = mIAsyncCallbackRemoteCallbackList.beginBroadcast() - 1; i >= 0; i--) {
                mIAsyncCallbackRemoteCallbackList.getBroadcastItem(i).playbackStarted();
            }
            mIAsyncCallbackRemoteCallbackList.finishBroadcast();

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.ic_action_play)
                .setTicker("Lalala")
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText("First");
        Notification not = builder.build();
        startForeground(1, not);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        for (int i = mIAsyncCallbackRemoteCallbackList.beginBroadcast(); i >= 0; i--) {
            try {
                mIAsyncCallbackRemoteCallbackList.getBroadcastItem(i).playbackEnded();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mIAsyncCallbackRemoteCallbackList.finishBroadcast();
    }
}
