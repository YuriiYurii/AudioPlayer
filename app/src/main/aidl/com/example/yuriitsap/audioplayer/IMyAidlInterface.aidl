// IMyAidlInterface.aidl
package com.example.yuriitsap.audioplayer;

// Declare any non-default types here with import statements
import com.example.yuriitsap.audioplayer.Song;

interface IMyAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    List<Song> getPlaylist();

    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
}
