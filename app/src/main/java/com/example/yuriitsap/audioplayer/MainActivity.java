package com.example.yuriitsap.audioplayer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Formatter;
import java.util.Locale;


public class MainActivity extends ActionBarActivity
        implements SeekBar.OnSeekBarChangeListener, RecyclerCursorAdapter.OnRowClickedCallBack {

    //messages
    private static final int UPDATE_PLAY_PAUSE = 1;
    private static final int UPDATE_PROGRESS = 2;
    //controlls
    private View mControls;
    private ImageView mSongImage;
    private TextView mArtist, mTitle;
    private ImageButton mPlayPause, mPrevious, mNext;
    private TextView mDuration, mCurrentTime;
    private SeekBar mProgess;
    private RecyclerView mRecyclerView;
    Calendar c = Calendar.getInstance();

    private IMyAidlInterface mIMyAidlInterface;
    private boolean mUserDragging;
    private StringBuilder mCurrentTimeFormatter = new StringBuilder();
    private Formatter mFormatter;
    private Cursor mCursor;
    private int mCurrentPosition = -1;
    private IAsyncCallback.Stub mStub = new IAsyncCallback.Stub() {

        @Override
        public void playbackStarted() throws RemoteException {
            mHandler.sendEmptyMessage(UPDATE_PLAY_PAUSE);
            mHandler.sendEmptyMessage(UPDATE_PROGRESS);
        }

        @Override
        public void playbackEnded() throws RemoteException {

        }
    };
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                mIMyAidlInterface = IMyAidlInterface.Stub.asInterface(service);
                mIMyAidlInterface.registerCallback(mStub);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIMyAidlInterface = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mCursor = getContentResolver()
                .query(AudioProvider.PLAYLIST_CONTENT_URI, null, null, null, "");
        mRecyclerView = (RecyclerView) findViewById(R.id.playlist);
        mRecyclerView.setAdapter(new RecyclerCursorAdapter(mCursor, this));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        initPlaybarControls();
        mFormatter = new Formatter(mCurrentTimeFormatter, Locale.getDefault());
        Intent playIntent = new Intent(this, MusicService.class);
        startService(playIntent);
        bindService(playIntent, mServiceConnection, BIND_AUTO_CREATE);
    }


    private void initPlaybarControls() {
        mControls = findViewById(R.id.play_bar_controls);
        mSongImage = (ImageView) findViewById(R.id.song_image);
        mArtist = (TextView) findViewById(R.id.song_artist);
        mTitle = (TextView) findViewById(R.id.song_title);
        mPrevious = (ImageButton) findViewById(R.id.next);
        mPlayPause = (ImageButton) findViewById(R.id.play_pause);
        mNext = (ImageButton) findViewById(R.id.next);
        mCurrentTime = (TextView) findViewById(R.id.current_song_time);
        mDuration = (TextView) findViewById(R.id.song_duration_time);
        mProgess = (SeekBar) findViewById(R.id.song_progress);

        mProgess.setMax(1000);
        mProgess.setOnSeekBarChangeListener(this);
        mPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPlayPause();
            }
        });
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int pos;
            switch (msg.what) {
                case UPDATE_PLAY_PAUSE:
//                    Log.e("TAG", "Updating button");
                    updatePlayButton();
                    break;
                case UPDATE_PROGRESS:
                    pos = updateProgress();
                    try {
                        if (!mUserDragging && mIMyAidlInterface.isPlaying()) {
                            msg = obtainMessage(UPDATE_PROGRESS);
                            sendMessageDelayed(msg, 1000 - (pos % 1000));
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    private void doPlayPause() {

        int seconds = c.get(Calendar.SECOND);
        try {
            if (mIMyAidlInterface != null && mIMyAidlInterface.isPlaying()) {

                mIMyAidlInterface.pause();
            } else {
                mIMyAidlInterface.start();
            }
            mHandler.sendEmptyMessage(UPDATE_PLAY_PAUSE);
            mHandler.sendEmptyMessage(UPDATE_PROGRESS);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void updatePlayButton() {
        try {
            Log.e("tag", "is playing activity = " + mIMyAidlInterface.isPlaying());
            if (mIMyAidlInterface != null && mIMyAidlInterface.isPlaying()) {
                mPlayPause.setSelected(true);
            } else {
                mPlayPause.setSelected(false);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private int updateProgress() {
        int position = 0;
        try {
            position = mIMyAidlInterface.getCurrentPosition();
            int duration = mIMyAidlInterface.getDuration();
            int currentPosition = 1000 * position / duration;
            mProgess.setProgress(currentPosition);
            mDuration.setText(formatTime(duration));
            mCurrentTime.setText(formatTime(position));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return position;
    }

    private String formatTime(int millis) {
        int totalTime = millis / 1000;
        int seconds = totalTime % 60;
        int minutes = (totalTime / 60) % 60;
        mCurrentTimeFormatter.setLength(0);
        return mFormatter.format("%02d:%02d", minutes, seconds).toString();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) {
            return;
        }
        try {
            int duration = mIMyAidlInterface.getDuration();
            long tempPosition = (progress * duration) / 1000L;
            mIMyAidlInterface.seekTo((int) tempPosition);
            mCurrentTime.setText(formatTime((int) tempPosition));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mUserDragging = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mUserDragging = false;
    }

    @Override
    public void onHolderClicked(int position) {
        if (mIMyAidlInterface != null) {
            try {
                if (mCurrentPosition != position) {
                    Log.e("TAG", "requested");
                    mIMyAidlInterface.play(getSongUri());
                    mCurrentPosition = position;
                    return;
                }
                doPlayPause();

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private String getSongUri() {
        return Uri.parse("android.resource://com.example.yuriitsap.audioplayer/" + String
                .valueOf(R.raw.first))
                .toString();
    }
}
