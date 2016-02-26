package org.github.voip.audio;

/**
 * Created by renan on 17/02/16.
 */
public interface ICodec {

    String getName();

    int encodeArray(short lin[], int offset, byte encoded[], int siz);

    int open(int compression);

    int decodeArray(byte encoded[], short lin[], int size);

    int getEncodedFrameSize();

    int getDecodedFrameSize();

    int getPayloadTypeCode();

    void close();
}
