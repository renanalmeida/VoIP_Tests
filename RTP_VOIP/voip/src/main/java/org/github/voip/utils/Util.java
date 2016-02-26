package org.github.voip.utils;

/**
 * Created by renan on 24/02/16.
 */
public class Util {
    public static byte[] toByte(short[] inputData) {
        int len = inputData.length * 2;
        byte[] ret = new byte[len];
        for (int i = 0; i < len; i += 2) {
            ret[i] = (byte) (inputData[i / 2] & 0xff);
            ret[i + 1] = (byte) ((inputData[i / 2] >> 8) & 0xff);
        }
        return ret;
    }

    public static short[] toShort(byte[] inputData) {
        int len = inputData.length / 2;
        short[] ret = new short[len];

        for (int i = 0; i < len; i++) {
            ret[i] = (short) ((inputData[i * 2 + 1] << 8) & 0xffff | (inputData[i * 2] & 0x00ff));
        }
        return ret;
    }
}
