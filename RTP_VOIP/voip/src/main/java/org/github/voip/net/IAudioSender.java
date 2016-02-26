package org.github.voip.net;

/**
 * Created by renan on 18/02/16.
 */
public interface IAudioSender {
    void sendPacket(byte[] dataBytes);
    void setPayload(int payload);
}
