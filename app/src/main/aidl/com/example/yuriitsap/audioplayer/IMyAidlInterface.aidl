// IMyAidlInterface.aidl
package com.example.yuriitsap.audioplayer;

// Declare any non-default types here with import statements
import com.example.yuriitsap.audioplayer.IAsyncCallback;
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
     boolean isLooping();
     void registerCallback(IAsyncCallback callback);
     void unRegisterCallback(IAsyncCallback callback);
}
