package org.github.audiostreamer;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by renan on 02/03/16.
 */
public class AudioCallManager {
    private List LiveStreamings;
    private String TAG = "AudioCallManager";
    private Context mContext;
    AudioStreamer sendAudio;
    AudioStreamer receiveAudio;

    public AudioCallManager(Context context) {
        LiveStreamings = new ArrayList<AudioStreamer>();
        this.mContext = context;
        sendAudio = new AudioStreamer(mContext, "sendAudio");
        receiveAudio = new AudioStreamer(mContext, "receiveAudio");
    }

    public void startVOIPStreaming(int remoteRtpPort, String remoteIp, int localPort, int codec) {
        Log.i(TAG, "Starting streaming: " + remoteIp + "/" + remoteRtpPort);
        String senderPipeline;
        String receiverPipeline;

        LiveStreamings.add(sendAudio);
        LiveStreamings.add(receiveAudio);

        if (codec == 97) {
            senderPipeline = "openslessrc ! audioconvert ! audioresample ! speexenc ! rtpspeexpay pt=97 ! udpsink host=" + remoteIp + " port=" + remoteRtpPort;
            // sendAudio.sendAudio(remoteIp, remoteRtpPort+"" );
            sendAudio.startVOIPStreaming(senderPipeline);
            receiverPipeline = " ! udpsrc port=" + localPort + " caps=\"application/x-rtp, media=(string)audio,clock-rate=(int)8000,encoding-name=(string)SPEEX,encoding-params=(string)1,octet-align=(string)1\"  ! rtpspeexdepay ! speexdec ! audioconvert ! audioresample ! openslessink name=openslessink stream-type=voice";
            // receiveAudio.receveAudio(remoteRtpPort+"");
            receiveAudio.startVOIPStreaming(receiverPipeline);

        } else {
        }
    }


    public void stop() {
        if(sendAudio != null){
            sendAudio.stopStreaming();
        }
        if(receiveAudio != null){
            receiveAudio.stopStreaming();
        }
    }

    public void setSpeakersOn(boolean b) {
        receiveAudio.enableSpeakers(b);
    }
    public void mute(boolean mute) {
        if(mute){
            sendAudio.pause();
        }else{
            sendAudio.resume();

        }
    }

}
