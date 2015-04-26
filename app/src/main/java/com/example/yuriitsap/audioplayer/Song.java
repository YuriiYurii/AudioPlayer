package com.example.yuriitsap.audioplayer;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yuriitsap on 17.04.15.
 */
@DatabaseTable(tableName = "playlist")
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

    public Song() {
    }

    @DatabaseField(generatedId = true, columnName = "_id")
    private int mId;
    @DatabaseField(columnName = "image_id")
    private int mImageId;
    @DatabaseField(columnName = "artist")
    private String mArtist;
    @DatabaseField(columnName = "title")
    private String mTitle;
    @DatabaseField(columnName = "duration")
    private int mDuration;
    @DatabaseField(columnName = "image_path")
    private String mUri;

    public Song(int id) {
        mId = id;
    }

    public Song(Parcel source) {
        mId = source.readInt();
        mImageId = source.readInt();
        mDuration = source.readInt();
        mArtist = source.readString();
        mTitle = source.readString();
        mUri = source.readString();
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

    public int getImageId() {
        return mImageId;
    }

    public Song setImageId(int imageId) {
        mImageId = imageId;
        return this;
    }

    public String getUri() {
        return mUri;
    }

    public Song setUri(String uri) {
        mUri = uri;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeInt(mImageId);
        dest.writeInt(mDuration);
        dest.writeString(mArtist);
        dest.writeString(mTitle);
        dest.writeString(mUri);
    }
}
