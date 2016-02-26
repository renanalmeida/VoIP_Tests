package org.github.voip.net;

import java.net.DatagramSocket;

import jlibrtp.DataFrame;
import jlibrtp.Participant;
import jlibrtp.RTPAppIntf;
import jlibrtp.RTPSession;

/**
 * Created by renan on 17/02/16.
 */
public class AudioSender implements RTPAppIntf, IAudioSender{
    public RTPSession rtpSession = null;
    public AudioSender(){

        DatagramSocket rtpSocket = null;
        DatagramSocket rtcpSocket = null;

        try {
            rtpSocket = new DatagramSocket();
            rtcpSocket = new DatagramSocket();
        } catch (Exception e) {
            System.out.println("RTPSession failed to obtain port");
        }
        rtpSession = new RTPSession(rtpSocket,rtcpSocket);
        rtpSession.RTPSessionRegister(this, null, null);
    }
    @Override
    public void setPayload(int payloadType){
        rtpSession.payloadType(payloadType);
    }

    public int addParticipant(Participant p){
        return this.rtpSession.addParticipant(p);
    }

    public void sendPacket(byte[] dataBytes){
        rtpSession.sendData(dataBytes);
//        Participant p = null;
//        Enumeration<Participant> iter = this.rtpSession.getParticipants();
//        while(iter.hasMoreElements()) {
//            System.out.println("!!!!!!!!!!!!hasMoreElements ");
//
//            p = iter.nextElement();
//            String name = "name";
//            byte[] nameBytes = name.getBytes();
//
//            int ret =  this.rtpSession.sendRTCPAppPacket(p.getSSRC(), 0, nameBytes, dataBytes);
//            System.out.println("!!!!!!!!!!!! ADDED APPLICATION SPECIFIC " + ret);
//            continue;
//        }
//        if(p == null)
//            System.out.println("No participant with SSRC available :(");
    }

    @Override
    public void receiveData(DataFrame frame, Participant participant) {
        // We don't expect any data.
    }

    @Override
    public void userEvent(int type, Participant[] participant) {
        //Do nothing
    }

    @Override
    public int frameSize(int payloadType) {
        return 1;
    }
}
