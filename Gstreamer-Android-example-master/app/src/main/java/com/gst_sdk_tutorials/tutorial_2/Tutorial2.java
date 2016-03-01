package com.gst_sdk_tutorials.tutorial_2;

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


public class Tutorial2 {

    private static final String TAG = "Tutorial";

    private native void nativeInit();     // Initialize native code, build pipeline, etc

    private native void nativeFinalize(); // Destroy pipeline and shutdown native code

    private native void nativePlay();     // Set pipeline to PLAYING

    private native void nativePause();    // Set pipeline to PAUSED

    private static native boolean nativeClassInit(); // Initialize native class: cache Method IDs for callbacks

    private native void nativeInitAudioSender(String remoteIp, String remotePort);

    private native void nativeInitPipeline(String pipiline);

    private native void nativeInitWithSDP(String sdp);

    private native void nativeInitAudioReceiver(String port);

    private long native_custom_data;      // Native code will use this to keep private data

    private boolean is_playing_desired;   // Whether the user asked to go to PLAYING


    public Tutorial2(Context context) {
        // Initialize GStreamer and warn if it fails
        try {
            GStreamer.init(context);
        } catch (Exception e) {
            return;
        }
        nativeInit();
    }

    public void sendAudio(String remoteIp, String remotePort){
        nativeInitAudioSender(remoteIp, remotePort);
    }

    protected void destroy() {
        nativeFinalize();

    }

    // Called from native code. This sets the content of the TextView from the UI thread.
    private void setMessage(final String message) {
        Log.w(TAG, "setMessage:" + message);
    }

    // Called from native code. Native code calls this once it has created its pipeline and
    // the main loop is running, so it is ready to accept commands.
    private void onGStreamerInitialized() {
        Log.i("GStreamer", "Gst initialized. Restoring state, playing:" + is_playing_desired);
        // Restore previous playing state
        if (is_playing_desired) {
            nativePlay();
        } else {
            nativePause();
        }
    }

    public void startVOIPStreaming(int remoteRtpPort, String remoteIp, int localPort, int codec) {
        Log.i(TAG, "Starting streaming: " + remoteIp + "/" + remoteRtpPort);
        String senderPipeline;
        String receiverPipeline;
        if (codec == 97) {
            receiverPipeline = " udpsrc port=" + localPort + " caps=\"application/x-rtp, media=(string)audio,clock-rate=(int)8000,encoding-name=(string)SPEEX,encoding-params=(string)1,octet-align=(string)1\"  ! rtpspeexdepay ! speexdec ! audioconvert ! audioresample ! autoaudiosink ! openslessrc ! audioconvert ! audioresample ! speexenc ! rtpspeexpay pt=97 ! udpsink host=" + remoteIp + " port=" + remoteRtpPort;
            nativeInitPipeline(receiverPipeline);
            senderPipeline = "openslessrc ! audioconvert ! audioresample ! speexenc ! rtpspeexpay pt=97 ! udpsink host=" + remoteIp + " port=" + remoteRtpPort;
            nativeInitPipeline(senderPipeline);
        } else {
        }
    }

    static {
        System.loadLibrary("gstreamer_android");
        System.loadLibrary("tutorial-2");
        nativeClassInit();
    }

    public void stopStreaming() {

    }
}