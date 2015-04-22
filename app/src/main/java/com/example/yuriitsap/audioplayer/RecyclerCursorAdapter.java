package com.example.yuriitsap.audioplayer;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by yuriitsap on 22.04.15.
 */
public class RecyclerCursorAdapter
        extends RecyclerView.Adapter<RecyclerCursorAdapter.SongViewHolder> {

    private Cursor mCursor;

    public RecyclerCursorAdapter(Cursor cursor) {
        mCursor = cursor;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SongViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_item, parent, false));
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.mSongImage.setImageResource(R.drawable.placeholder);
        holder.mSongArtist.setText(mCursor.getString(1));
        holder.mSongTitle.setText(mCursor.getString(2));
        holder.mSongDuration.setText(String.valueOf(mCursor.getLong(3)));
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {

        public ImageView mSongImage;
        public TextView mSongArtist;
        public TextView mSongTitle;
        public TextView mSongDuration;

        public SongViewHolder(View itemView) {
            super(itemView);
            mSongImage = (ImageView) itemView.findViewById(R.id.song_image);
            mSongArtist = (TextView) itemView.findViewById(R.id.song_artist);
            mSongTitle = (TextView) itemView.findViewById(R.id.song_title);
            mSongDuration = (TextView) itemView.findViewById(R.id.song_duration);
        }
    }
}
