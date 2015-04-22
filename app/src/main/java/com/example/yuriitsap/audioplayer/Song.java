package com.example.yuriitsap.audioplayer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yuriitsap on 17.04.15.
 */
public class Song implements Parcelable {

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel source) {
            return new Song(source);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    private int mId;
    private String mArtist;
    private String mTitle;
    private int mDuration;

    public Song(int id) {
        mId = id;
    }

    public Song(Parcel source) {
        mId = source.readInt();
        mDuration = source.readInt();
        mArtist = source.readString();
        mTitle = source.readString();
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getArtist() {
        return mArtist;
    }

    public Song setArtist(String artist) {
        mArtist = artist;
        return this;
    }

    public String getTitle() {
        return mTitle;
    }

    public Song setTitle(String title) {
        mTitle = title;
        return this;
    }

    public int getDuration() {
        return mDuration;
    }

    public Song setDuration(int duration) {
        mDuration = duration;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeInt(mDuration);
        dest.writeString(mArtist);
        dest.writeString(mTitle);
    }
}
