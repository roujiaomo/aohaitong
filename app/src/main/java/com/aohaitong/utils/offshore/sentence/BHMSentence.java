package com.aohaitong.utils.offshore.sentence;


public class BHMSentence extends BaseSentence {

    private static final long serialVersionUID = -4560550864269013316L;

    private String confirmIdentifier;

    private Long mmsi;

    private Integer sentencesNumber;

    private String data;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getConfirmIdentifier() {
        return confirmIdentifier;
    }

    public void setConfirmIdentifier(String confirmIdentifier) {
        this.confirmIdentifier = confirmIdentifier;
    }

    public Long getMmsi() {
        return mmsi;
    }

    public void setMmsi(Long mmsi) {
        this.mmsi = mmsi;
    }

    public Integer getSentencesNumber() {
        return sentencesNumber;
    }

    public void setSentencesNumber(Integer sentencesNumber) {
        this.sentencesNumber = sentencesNumber;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
