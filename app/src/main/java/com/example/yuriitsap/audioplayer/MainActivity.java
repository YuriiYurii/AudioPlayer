package com.example.yuriitsap.audioplayer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private IMyAidlInterface mIMyAidlInterface;
    private MusicService mMusicService;
    private android.widget.ListView mListView;
    private ListViewAdapter mListViewAdapter;
    private ArrayList<Song> mSongs;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIMyAidlInterface = IMyAidlInterface.Stub.asInterface(service);
            try {
                mSongs = (ArrayList<Song>) mIMyAidlInterface.getPlaylist();
                mListViewAdapter.notifyDataSetChanged();
            } catch (RemoteException e) {
                Log.e("TAG", " Remote connection has been lost " + e);
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
        mListView = (android.widget.ListView) findViewById(R.id.playlist);
        mSongs = new ArrayList<>(0);
        mListViewAdapter = new ListViewAdapter();
        mListView.setAdapter(mListViewAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent playIntent = new Intent(this, MusicService.class);
        bindService(playIntent, mServiceConnection, BIND_AUTO_CREATE);
        startService(playIntent);
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
}
