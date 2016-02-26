package org.github.sipuadavoip;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import org.github.voip.audio.ICodec;
import org.github.voip.codec.PCM;
import org.github.voip.codec.Speex;

import org.github.voip.net.AudioReceiver;
import org.github.voip.net.IAudioSender;
import org.github.voip.audio.impl.AudioPlayer;
import org.github.voip.audio.impl.AudioRecorder;
import org.github.voip.net.AudioSender;

import jlibrtp.Participant;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private EditText remoteIpEditText;
    private EditText portEditText;
    private Button buttonSendAudio;
    private Button buttonjoinAudio;
    private Button buttonStop;
    private RadioButton rbSpeex;
    private RadioButton rbPCM;
    private AudioRecorder mAudioRecorder;
    private IAudioSender mAudioSender;
    private ICodec mCodec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        remoteIpEditText = (EditText) findViewById(R.id.et_remote_ip);
        portEditText = (EditText) findViewById(R.id.et_remote_port);
        buttonSendAudio = (Button) findViewById(R.id.bt_send_audio);
        buttonjoinAudio = (Button) findViewById(R.id.bt_join);
        buttonStop = (Button) findViewById(R.id.bt_stop);
        rbSpeex = (RadioButton) findViewById(R.id.rb_speex_codec);
        rbPCM = (RadioButton) findViewById(R.id.rb_pcm);

         mCodec = Speex.getInstance();
        rbSpeex.setChecked(true);

        rbSpeex.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCodec.close();
                    mCodec = Speex.getInstance();
                }
            }
        });
        rbPCM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCodec.close();
                    mCodec = new PCM();
                }
            }
        });

        buttonSendAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int port = new Integer(portEditText.getText().toString());
                        Participant p = new Participant(remoteIpEditText.getText().toString(), port, port + 1);
                        mAudioSender = new AudioSender();
                        int participantAdded = ((AudioSender) mAudioSender).addParticipant(p);
                        Log.i(TAG, "onClick, participantAdded:" + participantAdded);
                        mAudioRecorder = new AudioRecorder(getApplicationContext(), mCodec);
                        mAudioRecorder.start();
                        mAudioRecorder.setAudioSender(mAudioSender);
                    }
                }).start();

            }
        });

        buttonjoinAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int port = new Integer(portEditText.getText().toString());
                AudioReceiver audioReceiver = new AudioReceiver(port, port + 1);
                AudioPlayer audioPlayer = new AudioPlayer(mCodec);
                audioReceiver.setAudioPlayer(audioPlayer);
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAudioRecorder != null) {
                    mAudioRecorder.stop();
                }
            }
        });

    }
}
