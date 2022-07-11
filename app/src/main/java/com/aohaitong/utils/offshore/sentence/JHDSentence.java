package com.aohaitong.utils.offshore.sentence;

import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage;


public class JHDSentence extends BaseSentence {
    private static final long serialVersionUID = 7765619510640086776L;

    /*
     *目的MMSI
     */

    private Long mmsi;

    /**
     * 电文内容：多个属性的组成，参照【近海通信语句设计】文档
     */

    private OffshoreCommunicationMessage OffshoreCommunicationMessage;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getMmsi() {
        return mmsi;
    }

    public void setMmsi(Long mmsi) {
        this.mmsi = mmsi;
    }

    public com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage getOffshoreCommunicationMessage() {
        return OffshoreCommunicationMessage;
    }

    public void setOffshoreCommunicationMessage(com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage offshoreCommunicationMessage) {
        OffshoreCommunicationMessage = offshoreCommunicationMessage;
    }
}
