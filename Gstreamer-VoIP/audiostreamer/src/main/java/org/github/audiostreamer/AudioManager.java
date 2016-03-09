package org.github.audiostreamer;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by renan on 02/03/16.
 */
public class AudioManager {

    public static int SPEEX = 97;
    public static int PCMA = 8;
    final private int DEFAULT_SAMPLE_RATE = 16000;

    // Properties
    /**
     * Speex encoding mode.
     */
    public static String MODE = "mode";
    /**
     * Sampling rate.
     */
    public static String RATE = "rate";

    private Context mContext;
    private AudioStreamer audioSender;
    private AudioStreamer audioReceiver;
    private String senderPipeline;
    private String receiverPipeline;
    private String TAG = "AudioManager";

    public AudioManager(Context context) {
        this.mContext = context;
        audioSender = new AudioStreamer(mContext, "audioSender");
        audioReceiver = new AudioStreamer(mContext, "audioReceiver");
    }

    public void startVOIPStreaming(int remoteRtpPort, String remoteIp, int localPort, int codecPayloadType, Map<String, String> properties) {
        Log.d(TAG, "startVOIPStreaming() called with: " + "remoteRtpPort = [" + remoteRtpPort + "], remoteIp = [" + remoteIp + "], localPort = [" + localPort + "], codec = [" + codecPayloadType + "]");

        if (audioSender != null) audioSender.stop();
        if (audioReceiver != null) audioReceiver.stop();

        String audioSenderCaps = "audio/x-raw, channels=1, rate=" + getSampleRate(properties, DEFAULT_SAMPLE_RATE);

        if (codecPayloadType == SPEEX) {
            String audioReceiverCaps = " caps=\"application/x-rtp, media=(string)audio,clock-rate=(int)" + getSampleRate(properties, DEFAULT_SAMPLE_RATE) + ",encoding-name=(string)SPEEX,encoding-params=(string)1,octet-align=(string)1\"";

            String speexEnc = "speexenc " + getPropertyString(AudioManager.MODE, properties, "");

            senderPipeline = "openslessrc ! audioconvert noise-shaping=medium ! audioresample !" + audioSenderCaps + " ! " + speexEnc + " ! rtpspeexpay pt=97 ! udpsink host=" + remoteIp + " port=" + remoteRtpPort;
            audioSender.startVOIPStreaming(senderPipeline);
            receiverPipeline = "udpsrc port=" + localPort + audioReceiverCaps + " ! rtpspeexdepay ! speexdec ! audioconvert ! audioresample ! openslessink name=openslessink stream-type=voice";
            audioReceiver.startVOIPStreaming(receiverPipeline);

        } else if (codecPayloadType == PCMA) {
            String audioReceiverCaps = " caps=\"application/x-rtp, media=(string)audio,clock-rate=(int)" + getSampleRate(properties, DEFAULT_SAMPLE_RATE) + ",encoding-name=(string)PCMA\" ";
            senderPipeline = "openslessrc ! audioconvert noise-shaping=medium ! audioresample ! " + audioSenderCaps + " ! alawenc ! rtppcmapay pt=8 ! udpsink host=" + remoteIp + " port=" + remoteRtpPort;
            audioSender.startVOIPStreaming(senderPipeline);
            receiverPipeline = "udpsrc port=" + localPort + audioReceiverCaps + " ! rtppcmadepay ! alawdec ! audioconvert ! audioresample ! openslessink name=openslessink stream-type=voice";
            audioReceiver.startVOIPStreaming(receiverPipeline);
        }
    }

    public void startVOIPStreaming(int remoteRtpPort, String remoteIp, int localPort, int codecPayloadType) {
        startVOIPStreaming(remoteRtpPort, remoteIp, localPort, codecPayloadType, new HashMap<String, String>());
    }

    public String getPropertyString(String property, Map<String, String> properties, String defaultValue) {
        if (properties.get(property) != null) {
            return property + "=" + properties.get(property);
        } else if (defaultValue != null) {
            return property + "=" + defaultValue;
        } else {
            return "";
        }
    }

    public int getSampleRate(Map<String, String> properties, int defaultValue) {
        if (properties.get(RATE) != null) {
            return new Integer(properties.get(RATE));
        } else {
            return defaultValue;
        }
    }

    public void stopStreaming() {
        if (audioSender != null) {
            audioSender.stop();
        }
        if (audioReceiver != null) {
            audioReceiver.stop();
        }
    }

    public void release() {
        if (audioSender != null) {
            audioSender.finalize();
        } else if (audioReceiver != null) {
            audioReceiver.finalize();
        }
    }


    public void setSpeakersOn(boolean speakersOn) {
        if (audioReceiver != null) {
            if (speakersOn) {
                audioReceiver.stop();
                receiverPipeline = receiverPipeline.replace("voice", "media");
                audioReceiver.startVOIPStreaming(receiverPipeline);
            } else {
                audioReceiver.stop();
                receiverPipeline = receiverPipeline.replace("media", "voice");
                audioReceiver.startVOIPStreaming(receiverPipeline);
            }
        }
    }

    public void mute(boolean mute) {
        if (mute) {
            audioSender.stop();
        } else {
            audioSender.resume();
        }
    }
}
