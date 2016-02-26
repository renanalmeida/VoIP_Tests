package org.github.voip.audio.impl;

import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import org.github.voip.audio.IAudioPlayer;
import org.github.voip.audio.ICodec;
import org.github.voip.codec.Speex;

import java.io.BufferedInputStream;

/**
 * Created by renan on 19/02/16.
 */
public class AudioPlayer implements IAudioPlayer {

    private AudioTrack mAudioTrack;



    private ICodec decodeCodec;

    private final int sampleRateInHz = 8000;
    private final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private final int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private int playBufferSize;

    public AudioPlayer(ICodec decodeCodec) {
        playBufferSize = AudioTrack.getMinBufferSize(sampleRateInHz,
                channelConfig, audioFormat);
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRateInHz,
                channelConfig, audioFormat, playBufferSize,
                AudioTrack.MODE_STREAM);
        this.decodeCodec = decodeCodec;
    }

    @Override
    public void read(byte[] dataBytes) {
        Log.w(">", "data Bytes!");
        int size = dataBytes.length;
        short[] outData = new short[decodeCodec.getDecodedFrameSize()];
        int decodedSize = 0;
        if(decodeCodec != null){
            decodedSize =  decodeCodec.decodeArray(dataBytes, outData, size);
        }
        //TODO
        mAudioTrack.play();
        mAudioTrack.write(outData, 0, decodedSize);
        mAudioTrack.pause();
    }
    @Override
    public int getBufferSize(){
        return AudioTrack.getMinBufferSize(sampleRateInHz,
                channelConfig, audioFormat);
    }

    public void stop() {
        mAudioTrack.stop();
        mAudioTrack.release();
    }

    public ICodec getDecodeCodec() {
        return decodeCodec;
    }

    public void setDecodeCodec(ICodec decodeCodec) {
        this.decodeCodec = decodeCodec;
    }
}
