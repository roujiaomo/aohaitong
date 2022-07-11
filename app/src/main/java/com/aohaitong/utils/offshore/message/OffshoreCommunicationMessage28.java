package com.aohaitong.utils.offshore.message;


/*
 * 广播语句
 */
public class OffshoreCommunicationMessage28 extends OffshoreCommunicationMessage {

    private static final long serialVersionUID = -6743566341263420240L;

    /*
     *业务类型
     */

    private Integer businessType;

    /*
     *二进制数据
     */

    private String data;

    /**
     * 构造方法。
     */
    public OffshoreCommunicationMessage28() {
        super(28);
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
