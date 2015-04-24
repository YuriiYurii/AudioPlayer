package com.example.yuriitsap.audioplayer;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
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
                .append(mPlaylist.get(position).getDuration());
        Log.e("TAG", "indexOf = " + stringBuilder.indexOf((String.valueOf(mPlaylist.get(position).getDuration()))));
        Spannable spannable = new SpannableStringBuilder(stringBuilder);
        StyleSpan titleStyle = new StyleSpan(Typeface.BOLD);
        StyleSpan artistStyle = new StyleSpan(Typeface.ITALIC);
        holder.mSongDescription.setText(stringBuilder.toString());
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
            mOnRowClickedCallBack.onHolderClicked(getPosition());
        }
    }

    public interface OnRowClickedCallBack {

        void onHolderClicked(int position);
    }
}
