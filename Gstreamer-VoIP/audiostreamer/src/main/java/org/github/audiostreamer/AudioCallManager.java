package org.github.audiostreamer;

import android.content.Context;
import android.util.Log;

/**
 * Created by renan on 02/03/16.
 */
public class AudioCallManager {
    private String TAG = "AudioCallManager";
    private Context mContext;
    private AudioStreamer audioSender;
    private AudioStreamer audioReceiver;
    private String senderPipeline;
    private String receiverPipeline;

    public AudioCallManager(Context context) {
        this.mContext = context;
        audioSender = new AudioStreamer(mContext, "audioSender");
        audioReceiver = new AudioStreamer(mContext, "audioReceiver");
    }

    public void startVOIPStreaming(int remoteRtpPort, String remoteIp, int localPort, int codec) {
        Log.i(TAG, "Starting streaming: " + remoteIp + "/" + remoteRtpPort);

        if(audioSender != null) audioSender.stopStreaming();
        if(audioReceiver != null) audioReceiver.stopStreaming();

        if (codec == 97) {
            senderPipeline = "openslessrc ! audioconvert ! audioresample ! speexenc ! rtpspeexpay pt=97 ! udpsink host=" + remoteIp + " port=" + remoteRtpPort;
            // audioSender.audioSender(remoteIp, remoteRtpPort+"" );
            audioSender.startVOIPStreaming(senderPipeline);
            receiverPipeline = "udpsrc port=" + localPort + " caps=\"application/x-rtp, media=(string)audio,clock-rate=(int)8000,encoding-name=(string)SPEEX,encoding-params=(string)1,octet-align=(string)1\"  ! rtpspeexdepay ! speexdec ! audioconvert ! audioresample ! openslessink name=openslessink stream-type=voice";
            // audioReceiver.receveAudio(remoteRtpPort+"");
            audioReceiver.startVOIPStreaming(receiverPipeline);

        } else if (codec == 8) {
                senderPipeline = "openslessrc ! audioconvert ! audioresample ! alawenc ! rtppcmapay pt=8 ! udpsink host=" + remoteIp + " port=" + remoteRtpPort;
                // audioSender.audioSender(remoteIp, remoteRtpPort+"" );
                audioSender.startVOIPStreaming(senderPipeline);
                receiverPipeline = "udpsrc port=" + localPort + " caps=\"application/x-rtp, media=(string)audio,clock-rate=(int)8000,encoding-name=(string)PCMA\"  ! rtppcmadepay ! alawdec ! audioconvert ! audioresample ! openslessink name=openslessink stream-type=voice";
                audioReceiver.startVOIPStreaming(receiverPipeline);
        }
    }

    public void stop() {
        if (audioSender != null) {
            audioSender.stopStreaming();
        }
        if (audioReceiver != null) {
            audioReceiver.stopStreaming();
        }
    }

    public void setSpeakersOn(boolean speakersOn) {
        if (audioReceiver != null) {
            if (speakersOn) {
                audioReceiver.stopStreaming();
                receiverPipeline = receiverPipeline.replace("voice", "media");
                audioReceiver.startVOIPStreaming(receiverPipeline);
            } else {
                audioReceiver.stopStreaming();
                receiverPipeline = receiverPipeline.replace("media", "voice");
                audioReceiver.startVOIPStreaming(receiverPipeline);
            }
        }
    }

    public void mute(boolean mute) {
        if (mute) {
            audioSender.pause();
        } else {
            audioSender.resume();
        }
    }

}
