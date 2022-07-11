package com.aohaitong.utils.offshore.Exception;

/**
 * 异或校验异常
 */
public class MessageVerificationException extends Exception {
    private static final long serialVersionUID = 1L;

    public MessageVerificationException() {
    }

    public MessageVerificationException(String msg) {
        super(msg);
    }
}
