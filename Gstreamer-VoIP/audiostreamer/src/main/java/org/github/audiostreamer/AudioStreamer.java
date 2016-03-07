package org.github.audiostreamer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.freedesktop.gstreamer.GStreamer;


public class AudioStreamer {

    private static final String TAG = "AudioStreamer";

    private native void nativeInit();     // Initialize native code, build pipeline, etc

    private native void nativeFinalize(); // Destroy pipeline and shutdown native code

    private native void nativePlay();     // Set pipeline to PLAYING

    private native void nativePause();    // Set pipeline to PAUSED

    private static native boolean nativeClassInit(); // Initialize native class: cache Method IDs for callbacks

    private native void nativeInitAudioSender(String remoteIp, String remotePort);

    private native void nativeInitPipeline(String pipeline);

    private native void nativeInitAudioReceiver(String port);

    private native void nativeEnableSpeakers();

    private long native_custom_data;      // Native code will use this to keep private data

    private boolean is_playing_desired;   // Whether the user asked to go to PLAYING

    private String mName;

    public AudioStreamer(Context context, String name) {
        this.mName = name;
        // Initialize GStreamer and warn if it fails
        try {
            GStreamer.init(context);
        } catch (Exception e) {
            return;
        }
    }

    public void sendAudio(String remoteIp, String remotePort){
        is_playing_desired = true;
        nativeInitAudioSender(remoteIp, remotePort);
    }

    public void receveAudio( String remotePort) {
        is_playing_desired = true;
        nativeInitAudioReceiver(remotePort);
    }


    protected void destroy() {
        nativeFinalize();

    }

    // Called from native code. This sets the content of the TextView from the UI thread.
    private void setMessage(final String message) {
        Log.w(TAG, mName + " :" + message);
    }

    // Called from native code. Native code calls this once it has created its pipeline and
    // the main loop is running, so it is ready to accept commands.
    private void onGStreamerInitialized() {
        Log.i("GStreamer "+mName, "Gst initialized. Restoring state, playing:" + is_playing_desired);
        // Restore previous playing state
        if (is_playing_desired) {
            nativePlay();
        } else {
            nativePause();
        }
    }

    public void startVOIPStreaming( String pipeline) {
        is_playing_desired = true;
        nativeInitPipeline(pipeline);
    }

    static {
        System.loadLibrary("gstreamer_android");
        System.loadLibrary("audiostreamer");
        nativeClassInit();
    }

    public void stopStreaming() {
        nativePause();
        nativeFinalize();
    }

    public void enableSpeakers(boolean b) {
        nativeEnableSpeakers();
    }

    public void pause() {
        nativePause();
    }

    public void resume() {
        nativePlay();
    }
}