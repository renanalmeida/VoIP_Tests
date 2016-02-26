package org.github.voip.audio.impl;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import org.github.voip.codec.Speex;

import org.github.voip.audio.ICodec;
import org.github.voip.audio.IAudioRecorder;
import org.github.voip.net.IAudioSender;

import java.util.Arrays;

/**
 * Created by renan on 18/02/16.
 */
public class AudioRecorder implements IAudioRecorder, AudioRecord.OnRecordPositionUpdateListener {

    private ICodec encodeCodec;
    private boolean isRecording = false;
    private static final int sampleRateInHz = 8000;
    private static final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private static final int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private AudioRecord audioRecord;
    private int recBufferSize;
    private int recBufferSizeInShorts;
    private IAudioSender audioSender;
    private Context mContext;
    private boolean muted = false;
    private int periodInFrames = 160;


    public AudioRecorder(Context context, ICodec codec) {
        this.mContext = context;
        this.encodeCodec = codec;
    }

    @Override
    public void start() {
//       recBufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
//       recBufferSizeInShorts = recBufferSize / 2;

        recBufferSize = encodeCodec.getDecodedFrameSize();
        recBufferSizeInShorts =  encodeCodec.getDecodedFrameSize();

        audioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION, sampleRateInHz, channelConfig, audioFormat, recBufferSize);
        isRecording = true;
        AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        am.setMode(AudioManager.MODE_IN_COMMUNICATION);
        am.setSpeakerphoneOn(false);
        //  am.setMode(AudioManager.MODE_IN_CALL);
        audioRecord.setPositionNotificationPeriod(periodInFrames);
        audioRecord.setRecordPositionUpdateListener(this);
        audioRecord.startRecording();
    }


    public ICodec getEncodeCodec() {
        return encodeCodec;
    }

    public void setEncodeCodec(ICodec encodeCodec) {
        this.encodeCodec = encodeCodec;
    }

    @Override
    public void stop() {
        encodeCodec.close();
        audioRecord.stop();
        audioRecord.release();
    }

    @Override
    public void setAudioSender(IAudioSender audioSender) {
        this.audioSender = audioSender;
        this.audioSender.setPayload(encodeCodec.getPayloadTypeCode());
    }


    public void mute() {
        muted = !muted;
    }

    @Override
    public void onMarkerReached(AudioRecord recorder) {

    }

    @Override
    public void onPeriodicNotification(AudioRecord recorder) {
        short[] recBuffer = new short[recBufferSizeInShorts];
        int bufferReadResult = audioRecord.read(recBuffer, 0,
                recBufferSizeInShorts);
        if (bufferReadResult > 0 && encodeCodec != null && !muted) {
            byte[] encoded = new byte[encodeCodec.getEncodedFrameSize()];
            int encodedSize = encodeCodec.encodeArray(recBuffer, 0, encoded, bufferReadResult);
            audioSender.sendPacket(encoded);
        }
    }


}
