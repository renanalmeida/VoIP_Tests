package org.github.voip.codec;

import android.util.Log;

import org.github.voip.audio.ICodec;

public class Speex implements ICodec {

    /* quality 1 : 4kbps, 2 : 6kbps, 4 : 8kbps, 6 : 11kpbs, 8 : 15kbps */
    private static final int DEFAULT_COMPRESSION = 4;
    private static final int PAYLOAD_TYPE_CODE = 97;

    private Speex() {
        init();
    }

    private static Speex instance = null;

    public static Speex getInstance() {
        if (instance == null) {
            instance = new Speex();
            instance.init();
        }
        return instance;
    }

    @Override
    public String getName() {
        return "SpeeX";
    }

    @Override
    public int encodeArray(short lin[], int offset, byte encoded[], int size) {
        return encode(lin, offset, encoded, size);
    }

    @Override
    public int decodeArray(byte[] encoded, short[] lin, int size) {
        Log.d("Speex", "decodeArray() called with: " + "encoded = [" + encoded.length + "], lin = [" + lin.length + "], size = [" + size + "]");
        return decode(encoded, lin, size);
    }

    @Override
    public int getEncodedFrameSize() {
        return getEncFrameSize();
    }

    @Override
    public int getDecodedFrameSize() {
        return getDecFrameSize();
    }

    @Override
    public int getPayloadTypeCode() {
        return PAYLOAD_TYPE_CODE;
    }

    public static void release() {
        instance.close();
        instance = null;
    }

    public void init() {
        load();
        open(DEFAULT_COMPRESSION);
    }

    private void load() {
        try {
            System.loadLibrary("speex");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public native int open(int compression);

    public native int decode(byte encoded[], short lin[], int size);

    public native int encode(short lin[], int offset, byte encoded[], int size);

    public native int getEncFrameSize();

    public native int getDecFrameSize();

    public native void close();

}