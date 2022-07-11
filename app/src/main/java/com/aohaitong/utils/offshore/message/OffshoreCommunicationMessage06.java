package com.aohaitong.utils.offshore.message;


/*
 *更新账号密码语句
 */
public class OffshoreCommunicationMessage06 extends OffshoreCommunicationAccountMessage {

    private static final long serialVersionUID = 6837441223779653954L;

    /*
     *备用
     */

    private Integer spare;

    /*
     *源密码
     */

    private String oldPassword;

    /*
     *二进制数据
     */

    private String data;

    /**
     * 构造方法。
     */
    public OffshoreCommunicationMessage06() {
        super(6);
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getSpare() {
        return spare;
    }

    public void setSpare(Integer spare) {
        this.spare = spare;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
