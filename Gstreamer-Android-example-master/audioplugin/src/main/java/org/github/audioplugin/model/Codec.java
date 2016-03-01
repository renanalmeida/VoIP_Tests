package org.github.audioplugin.model;

/**
 * Created by renan on 29/02/16.
 */
public class Codec {
    /**
     * The RTP payload type of the encoding.
     */
    public int type;

    /**
     * The encoding parameters to be used in the corresponding SDP attribute.
     */
    public String rtpmap;

    public Codec(int type, String rtpmap) {
        this.type = type;
        this.rtpmap = rtpmap;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getRtpmap() {
        return rtpmap;
    }

    public void setRtpmap(String rtpmap) {
        this.rtpmap = rtpmap;
    }

}
