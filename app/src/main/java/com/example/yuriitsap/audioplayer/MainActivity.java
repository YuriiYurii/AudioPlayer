package com.example.yuriitsap.audioplayer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.sql.SQLException;
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
    private TextView mSongDescription;
    private ImageButton mPlayPause, mPrevious, mNext;
    private TextView mDuration, mCurrentTime;
    private SeekBar mProgess;
    private RecyclerView mRecyclerView;

    private IMyAidlInterface mIMyAidlInterface;
    private RecyclerCursorAdapter mRecyclerCursorAdapter;
    private Formatter mFormatter;
    private StringBuilder mCurrentTimeFormatter = new StringBuilder();
    private boolean mUserDragging;
    private IAsyncCallback.Stub mStub = new IAsyncCallback.Stub() {

        @Override
        public void playbackStarted() throws RemoteException {
            mHandler.sendEmptyMessage(UPDATE_PLAY_PAUSE);
            mHandler.sendEmptyMessage(UPDATE_PROGRESS);
        }

        @Override
        public void playbackEnded() throws RemoteException {
            mHandler.sendEmptyMessage(UPDATE_PLAY_PAUSE);
            mHandler.sendEmptyMessage(UPDATE_PROGRESS);

        }
    };
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                mIMyAidlInterface = IMyAidlInterface.Stub.asInterface(service);
                mIMyAidlInterface.registerCallback(mStub);
                if (mIMyAidlInterface.isInProgress()) {
                    Song song = mIMyAidlInterface.getCurrentSong();
                    updateSongInfo(song);
                    mRecyclerView.scrollToPosition(mRecyclerCursorAdapter.setCurrentSong(song));
                }
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
        try {
            mRecyclerView = (RecyclerView) findViewById(R.id.playlist);
            mRecyclerCursorAdapter = new RecyclerCursorAdapter(
                    AudioProvider.OrmLiteDatabaseHelper
                            .getInstance(MainActivity.this).getSongDao().queryForAll(), this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mRecyclerView.setAdapter(mRecyclerCursorAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        initPlaybarControls();
        mFormatter = new Formatter(mCurrentTimeFormatter, Locale.getDefault());
        Intent playIntent = new Intent(this, MusicService.class);
        startService(playIntent);
        bindService(playIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            mHandler.removeCallbacksAndMessages(Handler.class);
            mIMyAidlInterface.unRegisterCallback(mStub);
            unbindService(mServiceConnection);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void initPlaybarControls() {
        mControls = findViewById(R.id.play_bar_controls);
        mSongImage = (ImageView) findViewById(R.id.song_image);
        mSongDescription = (TextView) findViewById(R.id.song_description);
        mSongDescription.setMovementMethod(new ScrollingMovementMethod());
        mPrevious = (ImageButton) findViewById(R.id.previous);
        mPlayPause = (ImageButton) findViewById(R.id.play_pause);
        mNext = (ImageButton) findViewById(R.id.next);
        mCurrentTime = (TextView) findViewById(R.id.current_song_time);
        mDuration = (TextView) findViewById(R.id.song_duration_time);
        mProgess = (SeekBar) findViewById(R.id.song_progress);
        mProgess.setMax(1000);
        mProgess.setOnSeekBarChangeListener(this);
        initializeListeners();

    }

    private void initializeListeners() {
        mPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPlayPause();
            }
        });
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNextSong();
            }
        });
        mPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPreviousSong();
            }
        });
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int pos;
            switch (msg.what) {
                case UPDATE_PLAY_PAUSE:
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
        try {
            if (mRecyclerCursorAdapter.getCurrentSong() == null) {
                mIMyAidlInterface.play(mRecyclerCursorAdapter.next());
                updateSongInfo(mRecyclerCursorAdapter.getCurrentSong());
                return;
            }
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
        mHandler.sendEmptyMessage(UPDATE_PROGRESS);
    }

    @Override
    public void onHolderClicked(Song song) {
        if (mIMyAidlInterface != null && song != null) {
            playSong(song);
            mSongImage.setAlpha(0.5f);
            mSongImage.animate().alpha(1.0f);
            return;
        }
        doPlayPause();
    }

    private void playSong(Song song) {
        try {
            mIMyAidlInterface.play(song);
            updateSongInfo(song);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void playPreviousSong() {
        Song song = mRecyclerCursorAdapter.previous();
        playSong(song);
    }

    private void playNextSong() {
        Song song = mRecyclerCursorAdapter.next();
        playSong(song);
    }

    private void updateSongInfo(Song song) {
        mSongImage.setImageResource(song.getImageId());
        mSongDescription.setText(song.getArtist() + " - " + song.getTitle());
        mHandler.sendEmptyMessage(UPDATE_PLAY_PAUSE);
        mHandler.sendEmptyMessage(UPDATE_PROGRESS);
    }
}
