package com.example.yuriitsap.audioplayer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yuriitsap on 17.04.15.
 */
public class Song implements Parcelable {

    private static final Creator<Song> CREATOR = new Creator<Song>() {
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
    private String mName;
    private long mDuration;

    public Song(int id) {
        mId = id;
    }

    public Song(Parcel source) {
        mId = source.readInt();
        mDuration = source.readLong();
        mName = source.readString();
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public long getDuration() {
        return mDuration;
    }

    public void setDuration(long duration) {
        mDuration = duration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeLong(mDuration);
        dest.writeString(mName);
    }
}
