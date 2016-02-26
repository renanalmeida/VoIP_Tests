package org.github.voip.audio;


/**
 * Created by renan on 17/02/16.
 */
public interface IAudioRecorder {

    void start();
    void stop();
    void setAudioSender(org.github.voip.net.IAudioSender audioSender);
}
