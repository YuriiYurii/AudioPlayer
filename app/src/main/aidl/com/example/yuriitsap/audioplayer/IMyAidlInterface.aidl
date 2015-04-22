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
     int getCurrentPosition();
     void pause();
     int getDuration();
     void seekTo(int position);
     boolean isPlaying();
}
