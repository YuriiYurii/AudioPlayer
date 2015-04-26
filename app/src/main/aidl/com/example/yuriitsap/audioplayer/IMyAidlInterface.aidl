// IMyAidlInterface.aidl
package com.example.yuriitsap.audioplayer;

// Declare any non-default types here with import statements
import com.example.yuriitsap.audioplayer.IAsyncCallback;
import com.example.yuriitsap.audioplayer.Song;
interface IMyAidlInterface {

     void play(in Song song);
     void start();
     void pause();
     boolean isPlaying();
     boolean isLooping();
     void seekTo(int position);
     int getCurrentPosition();
     int getDuration();
     void registerCallback(IAsyncCallback callback);
     void unRegisterCallback(IAsyncCallback callback);
}
