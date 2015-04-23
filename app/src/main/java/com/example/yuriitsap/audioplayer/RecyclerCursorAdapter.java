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
    private OnRowClickedCallBack mOnRowClickedCallBack;

    public RecyclerCursorAdapter(Cursor cursor, OnRowClickedCallBack onRowClickedCallBack) {
        mCursor = cursor;
        mOnRowClickedCallBack = onRowClickedCallBack;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SongViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_item, parent, false));
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.mSongDescription.setText(
                mCursor.getString(1) + "\n" + mCursor.getString(2) + "\n" + String
                        .valueOf(mCursor.getLong(3)));
        holder.mSongImage.setImageResource(mCursor.getInt(4));
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        public ImageView mSongImage;
        public TextView mSongDescription;

        public SongViewHolder(View itemView) {
            super(itemView);

            mSongImage = (ImageView) itemView.findViewById(R.id.song_image);
            mSongDescription = (TextView) itemView.findViewById(R.id.song_description);
            itemView.setOnClickListener(this);
            itemView.setActivated(true);
        }

        @Override
        public void onClick(View v) {
            mOnRowClickedCallBack.onHolderClicked(getPosition());
        }
    }

    public interface OnRowClickedCallBack {

        void onHolderClicked(int position);
    }
}
