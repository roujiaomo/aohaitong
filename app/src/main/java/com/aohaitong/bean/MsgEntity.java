package com.aohaitong.bean;

public class MsgEntity {
    private String msg;
    private int type;

    public MsgEntity(String msg, int type) {
        this.msg = msg;
        this.type = type;
    }

    public MsgEntity() {
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
