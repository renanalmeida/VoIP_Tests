package org.github.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import org.github.audiostreamer.AudioManager;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private EditText remoteIpEditText;
    private EditText portEditText;
    private Button buttonCall;
    private Button buttonEndCall;
    private RadioButton rbSpeex;
    private RadioButton rbPCM;
    private AudioManager audioManager;
    private CheckBox cbSpeakesOn;
    private CheckBox cbMute;

    private int codecPayloadType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        audioManager = new AudioManager(getApplicationContext());
        remoteIpEditText = (EditText) findViewById(R.id.et_remote_ip);
        portEditText = (EditText) findViewById(R.id.et_remote_port);
        buttonCall = (Button) findViewById(R.id.bt_call);
        buttonEndCall = (Button) findViewById(R.id.bt_end_call);
        rbSpeex = (RadioButton) findViewById(R.id.rb_speex_codec);
        rbPCM = (RadioButton) findViewById(R.id.rb_pcm);
        cbSpeakesOn = (CheckBox) findViewById(R.id.cb_speakers_on);
        cbMute = (CheckBox) findViewById(R.id.cb_mute);
        rbSpeex.setChecked(true);
        cbSpeakesOn.setChecked(false);
        codecPayloadType = AudioManager.SPEEX;

        rbSpeex.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    codecPayloadType = AudioManager.SPEEX;
                }
            }
        });

        rbPCM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    codecPayloadType = AudioManager.PCMA;
                }
            }
        });

        cbMute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    audioManager.mute(true);
                } else {
                    audioManager.mute(false);
                }
            }
        });

        cbSpeakesOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    audioManager.setSpeakersOn(true);
                } else {
                    audioManager.setSpeakersOn(false);
                }
            }
        });

        buttonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "onClick, participantAdded:");
                        int port = new Integer(portEditText.getText().toString());
                        String ip = remoteIpEditText.getText().toString();
                        audioManager.startVOIPStreaming(port, ip, port, codecPayloadType);
                    }
                }).start();
            }
        });

        buttonEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioManager.stopStreaming();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(audioManager != null){
            audioManager.stopStreaming();
            audioManager.release();
        }
    }
}
