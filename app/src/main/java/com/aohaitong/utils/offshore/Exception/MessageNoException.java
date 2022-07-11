package com.aohaitong.utils.offshore.Exception;

/**
 * 消息号异常
 */
public class MessageNoException extends Exception {
    private static final long serialVersionUID = 165354859479613128L;

    public MessageNoException() {
    }

    public MessageNoException(String msg) {
        super(msg);
    }
}
