package com.aohaitong.bean;

public class MsgEntity {
    private String msg;
    private int type;
    private Object object; //可用于各类ID传参

    public MsgEntity(String msg, int type) {
        this.msg = msg;
        this.type = type;
    }

    public MsgEntity() {
    }

    public MsgEntity(String msg, int type, Object object) {
        this.msg = msg;
        this.type = type;
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
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
