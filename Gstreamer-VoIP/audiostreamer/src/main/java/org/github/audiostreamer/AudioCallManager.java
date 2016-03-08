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
    private int SampleRate = 16000;

    public AudioCallManager(Context context) {
        this.mContext = context;
        audioSender = new AudioStreamer(mContext, "audioSender");
        audioReceiver = new AudioStreamer(mContext, "audioReceiver");
    }

    public void startVOIPStreaming(int remoteRtpPort, String remoteIp, int localPort, int codec) {
        Log.d(TAG, "startVOIPStreaming() called with: " + "remoteRtpPort = [" + remoteRtpPort + "], remoteIp = [" + remoteIp + "], localPort = [" + localPort + "], codec = [" + codec + "]");;

        if (audioSender != null) audioSender.stopStreaming();
        if (audioReceiver != null) audioReceiver.stopStreaming();

        String audioSenderCaps = "audio/x-raw, channels=1, rate=" + SampleRate;

        if (codec == 97) {
            String audioReceiverCaps = " caps=\"application/x-rtp, media=(string)audio,clock-rate=(int)" + SampleRate + ",encoding-name=(string)SPEEX,encoding-params=(string)1,octet-align=(string)1\"";
            senderPipeline = "openslessrc ! audioconvert noise-shaping=medium ! audioresample !" + audioSenderCaps + " ! speexenc ! rtpspeexpay pt=97 ! udpsink host=" + remoteIp + " port=" + remoteRtpPort;
            audioSender.startVOIPStreaming(senderPipeline);
            receiverPipeline = "udpsrc port=" + localPort + audioReceiverCaps + " ! rtpspeexdepay ! speexdec ! audioconvert ! audioresample ! openslessink name=openslessink stream-type=voice";
            audioReceiver.startVOIPStreaming(receiverPipeline);

        } else if (codec == 8) {
            String audioReceiverCaps = " caps=\"application/x-rtp, media=(string)audio,clock-rate=(int)" + SampleRate + ",encoding-name=(string)PCMA\" ";
            senderPipeline = "openslessrc ! audioconvert noise-shaping=medium ! audioresample ! " + audioSenderCaps + " ! alawenc ! rtppcmapay pt=8 ! udpsink host=" + remoteIp + " port=" + remoteRtpPort;
            audioSender.startVOIPStreaming(senderPipeline);
            receiverPipeline = "udpsrc port=" + localPort + audioReceiverCaps + " ! rtppcmadepay ! alawdec ! audioconvert ! audioresample ! openslessink name=openslessink stream-type=voice";
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

    public int getSampleRate() {
        return SampleRate;
    }

    public void setSampleRate(int sampleRate) {
        SampleRate = sampleRate;
    }


}
