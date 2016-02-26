package org.github.voip.codec;


import org.github.voip.audio.ICodec;
import org.github.voip.utils.Util;

/**
 * Created by renan on 19/02/16.
 * ICodec for test, copied from: http://stackoverflow.com/questions/23273866/issue-encoding-and-decoding-an-audio-recording-to-g711-pcmu-ulaw-format
 */


public class PCM implements ICodec{

    @Override
    public String getName() {
        return "PCM";
    }

    @Override
    public int encodeArray(short[] lin, int offset, byte[] encoded, int siz) {
        encoded = Util.toByte(lin);
        return encoded.length;
    }

    @Override
    public int open(int compression) {
        return 0;
    }

    @Override
    public int decodeArray(byte[] encoded, short[] lin, int size) {
        lin = Util.toShort(encoded);
        return lin.length;
    }

    @Override
    public int getEncodedFrameSize() {
        return 160;
    }

    @Override
    public int getDecodedFrameSize() {
        return 160;
    }

    @Override
    public int getPayloadTypeCode() {
        return 0;
    }

    @Override
    public void close() {

    }
}
