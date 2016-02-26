package org.github.voip.net;

import org.github.voip.codec.Speex;

import org.github.voip.audio.IAudioPlayer;

import java.net.DatagramSocket;

import jlibrtp.DataFrame;
import jlibrtp.Participant;
import jlibrtp.RTPAppIntf;
import jlibrtp.RTPSession;

/**
 * Created by renan on 17/02/16.
 */
public class AudioReceiver implements RTPAppIntf {

    private IAudioPlayer audioPlayer;
    private RTPSession rtpSession;

    public AudioReceiver(int rtpPort, int rtcpPort) {
        DatagramSocket rtpSocket = null;
        DatagramSocket rtcpSocket = null;

        try {
            rtpSocket = new DatagramSocket(rtpPort);
            rtcpSocket = new DatagramSocket(rtcpPort);
        } catch (Exception e) {
            System.out.println("RTPSession failed to obtain port");
        }


        rtpSession = new RTPSession(rtpSocket, rtcpSocket);
        rtpSession.naivePktReception(true);
        rtpSession.RTPSessionRegister(this, null, null);

        //Participant p = new Participant("127.0.0.1", 6001, 6002);
        //rtpSession.addParticipant(p);
    }

    public IAudioPlayer getAudioPlayer() {
        return audioPlayer;
    }

    public void setAudioPlayer(IAudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
    }

    @Override
    public void receiveData(DataFrame frame, Participant participant) {
        byte[] dataArray = frame.getConcatenatedData();
        if (audioPlayer != null) {
            audioPlayer.read(dataArray);
        }
    }



    @Override
    public void userEvent(int type, Participant[] participant) {

    }

    @Override
    public int frameSize(int payloadType) {
        return 0;
    }
}
