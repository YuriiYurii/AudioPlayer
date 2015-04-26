package com.example.yuriitsap.audioplayer;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by yuriitsap on 22.04.15.
 */
public class RecyclerCursorAdapter
        extends RecyclerView.Adapter<RecyclerCursorAdapter.SongViewHolder> {

    private OnRowClickedCallBack mOnRowClickedCallBack;
    private List<Song> mPlaylist;
    private int mCurrentPosition = -1;

    public RecyclerCursorAdapter(List<Song> playlist,
            OnRowClickedCallBack onRowClickedCallBack) {
        mOnRowClickedCallBack = onRowClickedCallBack;
        mPlaylist = playlist;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SongViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_item, parent, false));
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        StringBuilder stringBuilder = new StringBuilder(mPlaylist.get(position).getTitle())
                .append("\n").append(mPlaylist.get(position).getArtist()).append("\n")
                .append("Duration : ").append(mPlaylist.get(position).getDuration());
        Spannable spannable = new SpannableStringBuilder(stringBuilder);
        int titleStart = stringBuilder.indexOf(mPlaylist.get(position).getTitle());
        int artistStart = stringBuilder.indexOf(mPlaylist.get(position).getArtist());
        int durationStart = stringBuilder.indexOf(
                "Duration");
        spannable.setSpan(new StyleSpan(Typeface.BOLD), titleStart, artistStart,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new RelativeSizeSpan(1.2f), titleStart, artistStart,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new RelativeSizeSpan(0.8f), artistStart, durationStart,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new StyleSpan(Typeface.ITALIC), artistStart, durationStart,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new RelativeSizeSpan(0.5f), durationStart, spannable.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.mSongDescription.setText(spannable);
        holder.mSongImage.setImageResource(mPlaylist.get(position).getImageId());
    }

    @Override
    public int getItemCount() {
        return mPlaylist.size();
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
            Song song = null;
            if (mCurrentPosition != getPosition()) {
                mCurrentPosition = getPosition();
                song = mPlaylist.get(mCurrentPosition);
            }
            mOnRowClickedCallBack.onHolderClicked(song);
        }
    }

    public interface OnRowClickedCallBack {

        void onHolderClicked(Song song);
    }

    public Song next() {
        mCurrentPosition = ++mCurrentPosition == mPlaylist.size() ? 0
                : mCurrentPosition;
        return mPlaylist.get(mCurrentPosition);

    }

    public Song previous() {
        mCurrentPosition = --mCurrentPosition == -1 ? mPlaylist.size() - 1 : mCurrentPosition;
        return mPlaylist.get(mCurrentPosition);

    }

    public Song getCurrentSong() {
        if (mCurrentPosition == -1) {
            return null;
        }
        return mPlaylist.get(mCurrentPosition);
    }
}
