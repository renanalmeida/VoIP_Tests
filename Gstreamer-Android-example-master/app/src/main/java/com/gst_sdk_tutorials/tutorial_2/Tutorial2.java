package com.gst_sdk_tutorials.tutorial_2;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.freedesktop.gstreamer.GStreamer;


public class Tutorial2 extends Activity {
    private native void nativeInit();     // Initialize native code, build pipeline, etc

    private native void nativeFinalize(); // Destroy pipeline and shutdown native code

    private native void nativePlay();     // Set pipeline to PLAYING

    private native void nativeInitAudioSender(String remoteIp, String remotePort);

    private native void nativeInitAudioReceiver(String remotePort);

    private native void nativePause();    // Set pipeline to PAUSED

    private static native boolean nativeClassInit(); // Initialize native class: cache Method IDs for callbacks

    private long native_custom_data;      // Native code will use this to keep private data

    private boolean is_playing_desired;   // Whether the user asked to go to PLAYING

    private EditText remoteIpEditText;
    private EditText portEditText;
    private Button buttonSendAudio;
    private Button buttonjoinAudio;
    private Button buttonStop;

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize GStreamer and warn if it fails
        try {
            GStreamer.init(this);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        setContentView(R.layout.main);

        remoteIpEditText = (EditText) findViewById(R.id.et_remote_ip);
        portEditText = (EditText) findViewById(R.id.et_remote_port);
        buttonSendAudio = (Button) findViewById(R.id.bt_send_audio);
        buttonjoinAudio = (Button) findViewById(R.id.bt_join);
        buttonStop = (Button) findViewById(R.id.bt_stop);

        buttonjoinAudio.setOnClickListener(new OnClickListener() {
            String port = portEditText.getText().toString();

            public void onClick(View v) {
                is_playing_desired = true;
                nativeInitAudioReceiver(port);
            }
        });

        buttonSendAudio.setOnClickListener(new OnClickListener() {
            String port = portEditText.getText().toString();
            String ip = remoteIpEditText.getText().toString();

            public void onClick(View v) {
                is_playing_desired = true;
                nativeInitAudioSender(ip, port);
            }
        });

        buttonStop.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                is_playing_desired = false;
                nativePause();
            }
        });

        if (savedInstanceState != null) {
            is_playing_desired = savedInstanceState.getBoolean("playing");
            Log.i("GStreamer", "Activity created. Saved state is playing:" + is_playing_desired);
        } else {
            is_playing_desired = false;
            Log.i("GStreamer", "Activity created. There is no saved state, playing: false");
        }

        // Start with disabled buttons, until native code is initialized
//        this.findViewById(R.id.button_play).setEnabled(false);
//        this.findViewById(R.id.button_stop).setEnabled(false);

        nativeInit();
    }

    protected void onSaveInstanceState(Bundle outState) {
        Log.d("GStreamer", "Saving state, playing:" + is_playing_desired);
        outState.putBoolean("playing", is_playing_desired);
    }

    protected void onDestroy() {
        nativeFinalize();
        super.onDestroy();
    }

    // Called from native code. This sets the content of the TextView from the UI thread.
    private void setMessage(final String message) {
        final TextView tv = (TextView) this.findViewById(R.id.tv_message);
        runOnUiThread(new Runnable() {
            public void run() {
                tv.setText(message);
            }
        });
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
        // Re-enable buttons, now that GStreamer is initialized
        final Activity activity = this;
        runOnUiThread(new Runnable() {
            public void run() {
//                activity.findViewById(R.id.button_play).setEnabled(true);
//                activity.findViewById(R.id.button_stop).setEnabled(true);
            }
        });
    }

    static {
        System.loadLibrary("gstreamer_android");
        System.loadLibrary("tutorial-2");
        nativeClassInit();
    }

}