// IMyAidlInterface.aidl
package com.example.yuriitsap.audioplayer;

// Declare any non-default types here with import statements

interface IMyAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
     void play(String uri);
     void start();
     void pause();
     boolean isPlaying();
     void seekTo(int position);
     int getCurrentPosition();
     int getDuration();
}
