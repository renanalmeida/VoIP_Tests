package org.github.voip.audio;

/**
 * Created by renan on 19/02/16.
 */
public interface IAudioPlayer {

    void read(byte[] dataBytes);

    int getBufferSize();
}
