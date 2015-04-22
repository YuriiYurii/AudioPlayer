package com.example.yuriitsap.audioplayer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Formatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {

    private static final int SHOW_CONTROLS = 1;
    private static final int UPDATE_PROGRESS = 2;
    private IMyAidlInterface mIMyAidlInterface;
    private android.widget.ListView mListView;
    private ListViewAdapter mListViewAdapter;
    private List<Song> mSongs;
    private LinearLayout mControlls;
    private TextView mSongDescription;
    private TextView mDuration, mCurrentTime;
    private SeekBar mProgess;
    private ImageButton mPlayPause;
    private int mCurrentSongPosition = -1;
    private boolean mControllsVisible;
    private boolean mUserDragging;
    private StringBuilder mCurrentTimeFormatter = new StringBuilder();
    private Formatter mFormatter;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIMyAidlInterface = IMyAidlInterface.Stub.asInterface(service);
            show(5000);
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
        initPlaylist();
        initControls();
        mFormatter = new Formatter(mCurrentTimeFormatter, Locale.getDefault());
        mListView = (android.widget.ListView) findViewById(R.id.playlist);
        mListViewAdapter = new ListViewAdapter();
        Intent playIntent = new Intent(this, MusicService.class);
        bindService(playIntent, mServiceConnection, BIND_AUTO_CREATE);
        startService(playIntent);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                try {
                    show(5000);
                    if (mCurrentSongPosition == position) {
                        doPlayPause();
                    }
                    mIMyAidlInterface
                            .play(Uri.parse("android.resource://com.example.yuriitsap.audioplayer/"
                                    + mSongs.get(position).getId()).toString());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                mCurrentSongPosition = position;
            }
        });
        mListView.setAdapter(mListViewAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIMyAidlInterface != null) {
            show(3000);
        }
    }

    private void initControls() {
        mProgess = (SeekBar) findViewById(R.id.song_progress);
        mProgess.setMax(1000);
        mProgess.setOnSeekBarChangeListener(new ProgressListener());
        mSongDescription = (TextView) findViewById(R.id.song_description);
        mDuration = (TextView) findViewById(R.id.song_duration_time);
        mCurrentTime = (TextView) findViewById(R.id.current_song_time);
        mControlls = (LinearLayout) findViewById(R.id.play_controls);
        mPlayPause = (ImageButton) findViewById(R.id.play_pause);
        mPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPlayPause();
            }
        });
    }

    private void initPlaylist() {
        mSongs = new LinkedList<>();
        for (int i = 40; i > 0; i--) {
            mSongs.add(new Song(R.raw.first).setDuration(200).setArtist("Song N" + i));
        }
    }

    public void show(int timeout) {
        if (!mControllsVisible) {
            updateProgress();
            mControlls.setVisibility(View.VISIBLE);
            mControllsVisible = true;
        }
        updatePlayButton();
        mHandler.sendEmptyMessage(UPDATE_PROGRESS);

        Message msg = mHandler.obtainMessage(SHOW_CONTROLS);
        if (timeout != 0) {
            mHandler.removeMessages(SHOW_CONTROLS);
            mHandler.sendMessageDelayed(msg, timeout);
        }
    }

    public void hide() {
        if (mControllsVisible) {
            mHandler.removeMessages(UPDATE_PROGRESS);
            mControlls.setVisibility(View.GONE);
            mControllsVisible = false;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int pos;
            switch (msg.what) {
                case SHOW_CONTROLS:
                    hide();
                    break;
                case UPDATE_PROGRESS:
                    pos = updateProgress();
                    try {
                        if (!mUserDragging && mControllsVisible && mIMyAidlInterface.isPlaying()) {
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
            if (mIMyAidlInterface != null && mIMyAidlInterface.isPlaying()) {
                mIMyAidlInterface.pause();
            } else {
                mIMyAidlInterface.start();
            }
            updatePlayButton();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void updatePlayButton() {
        try {
            if (mIMyAidlInterface != null && mIMyAidlInterface.isPlaying()) {
                mPlayPause.setImageResource(R.drawable.ic_action_pause);
            } else {
                mPlayPause.setImageResource(R.drawable.ic_action_play);
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

    private class ListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mSongs.size();
        }

        @Override
        public Object getItem(int position) {
            return mSongs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mSongs.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.song_item, parent, false);
            }
            ((TextView) convertView.findViewById(R.id.song_name))
                    .setText(mSongs.get(position).getArtist());
            return convertView;
        }
    }

    private class ProgressListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser) {
                return;
            }
            try {
                int duration = mIMyAidlInterface.getDuration();
                Log.e("TAG", "duration = " + duration);
                long tempPosition = (progress * duration) / 1000L;
                Log.e("TAG", "tempPosition = " + tempPosition);
                mIMyAidlInterface.seekTo((int) tempPosition);
                mCurrentTime.setText(formatTime((int) tempPosition));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mUserDragging = true;
            show(120000);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mUserDragging = false;
            show(5000);
        }
    }
}
