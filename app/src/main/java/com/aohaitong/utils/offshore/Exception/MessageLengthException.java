package com.aohaitong.utils.offshore.Exception;

/**
 * 消息长度异常
 */
public class MessageLengthException extends Exception {
    private static final long serialVersionUID = 1L;

    public MessageLengthException() {
    }

    public MessageLengthException(String msg) {
        super(msg);
    }
}
