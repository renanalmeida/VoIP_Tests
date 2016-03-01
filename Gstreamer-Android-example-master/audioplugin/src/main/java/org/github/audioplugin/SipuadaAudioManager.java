package org.github.audioplugin;

import android.content.Context;
import android.util.Log;

import com.gst_sdk_tutorials.tutorial_2.Tutorial2;

import org.github.audioplugin.model.Codec;
import org.github.audioplugin.utils.Util;


public class SipuadaAudioManager {
    private Context mContext;
    private Tutorial2 mAudioStreaming;

    private static final String TAG = "SipuadaAudioManager";

    public SipuadaAudioManager(Context context, String localAddress) {
        mContext = context;
        mAudioStreaming = new Tutorial2(mContext);
    }

    public void setupAudioStream() {

    }

    public Codec[] getCodecs() {
        Codec speex = new Codec(97,"rtpmap:97 Speex/8000");
        Codec pcma = new Codec(8,"rtpmap:8 PCMA/8000");
        Codec[] availableCodecs = {speex,pcma};
        return availableCodecs;
    }

    public int getAudioStreamPort() {
        int testLimit = 50;
        int cont = 0;
        int port = 16400;
        while (cont < testLimit ){
            if(Util.available(port)){
                break;
            }
            else port = port + 2;
        }
        return port;
    }

    // Start sending/receiving media
    public void startStreaming(int remoteRtpPort, String remoteIp, int localRtpPort,Codec codec) {
        Log.i(TAG, "Starting streaming: " + remoteIp + "/" + remoteRtpPort);
        mAudioStreaming.startVOIPStreaming( remoteRtpPort,  remoteIp,  localRtpPort, codec.getType());
    }

    public void stopStreaming() {
        // workaround: android RTP facilities seem to induce around 500ms delay in the incoming media stream.
        // Let's delay the media tear-down to avoid media truncation for now
        mAudioStreaming.stopStreaming();
    }

}
