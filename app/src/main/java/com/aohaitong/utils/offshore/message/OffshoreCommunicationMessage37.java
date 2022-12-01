package com.aohaitong.utils.offshore.message;


/*
 *更新好友群组请求语句
 */
public class OffshoreCommunicationMessage37 extends OffshoreCommunicationAccountMessage {

    private static final long serialVersionUID = 5640891113191198629L;

    /*
     *mmsi
     */
    private Long mmsi;

    /*
     *备用
     */

    private Integer spare;

    /*
     *二进制数据
     */

    private String data;

    /**
     * 构造方法。
     */
    public OffshoreCommunicationMessage37() {
        super(37);
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getMmsi() {
        return mmsi;
    }

    public void setMmsi(Long mmsi) {
        this.mmsi = mmsi;
    }

    public Integer getSpare() {
        return spare;
    }

    public void setSpare(Integer spare) {
        this.spare = spare;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
