package com.example.yuriitsap.audioplayer;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.provider.UserDictionary;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Formatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {

    private IMyAidlInterface mIMyAidlInterface;
    private android.widget.ListView mListView;
    private ListViewAdapter mListViewAdapter;
    private List<Song> mSongs;
    private LinearLayout mControlls;
    private static final int UPDATE_PROGRESS = 1;
    private TextView mSongDescription;
    private TextView mDuration, mCurrentTime;
    private ImageButton mPlayPause;
    private int mCurrentSongPosition = -1;
    private StringBuilder mCurrentTimeFormatter = new StringBuilder();
    private Formatter mFormatter;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIMyAidlInterface = IMyAidlInterface.Stub.asInterface(service);
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
        ContentResolver musicResolver = getContentResolver();
        musicResolver.query(MediaStore.Audio,null,null,null,null);
        mFormatter = new Formatter(mCurrentTimeFormatter, Locale.getDefault());
        mListView = (android.widget.ListView) findViewById(R.id.playlist);
        mListViewAdapter = new ListViewAdapter();
        Intent playIntent = new Intent(this, MusicService.class);
        bindService(playIntent, mServiceConnection, BIND_AUTO_CREATE);
        startService(playIntent);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Runnable hideControlls = new Runnable() {
                @Override
                public void run() {
                    mControlls.setVisibility(View.GONE);
                }
            };

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                mControlls.setVisibility(View.VISIBLE);
                view.removeCallbacks(hideControlls);
                view.postDelayed(hideControlls, 5000);
                try {
                    if (mCurrentSongPosition == position) {
                        mIMyAidlInterface.pause();
                        return;
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

    private void initControls() {
        mSongDescription = (TextView) findViewById(R.id.song_description);
        mDuration = (TextView) findViewById(R.id.song_duration_time);
        mCurrentTime = (TextView) findViewById(R.id.current_song_time);
        mControlls = (LinearLayout) findViewById(R.id.play_controls);
        mPlayPause = (ImageButton) findViewById(R.id.play_pause);
        mPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlayButton();

            }
        });
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
                    .setText(mSongs.get(position).getName());
            return convertView;
        }
    }

    private void initPlaylist() {
        mSongs = new LinkedList<>();
        for (int i = 40; i > 0; i--) {
            mSongs.add(new Song(R.raw.first).setDuration(200).setName("Song N" + i));

        }
    }

    private void updatePlayButton() {
        try {
            if (mIMyAidlInterface != null && mIMyAidlInterface.isPlaying()) {
                mPlayPause.setImageResource(R.drawable.ic_action_play);
                mIMyAidlInterface.pause();
            } else {
                mPlayPause.setImageResource(R.drawable.ic_action_pause);
                mIMyAidlInterface.start();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void updateProgress() {
        try {
            if (mIMyAidlInterface != null && mIMyAidlInterface.isPlaying()) {
                mCurrentTime.setText(
                        formatTime(mIMyAidlInterface.getCurrentPosition()));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private String formatTime(int millis) {
        int totalTime = millis / 1000;
        int seconds = totalTime % 60;
        int minutes = (totalTime / 60) % 60;

        mCurrentTimeFormatter.setLength(0);
        return mFormatter.format("%02d:%02d", minutes, seconds).toString();
    }
}
