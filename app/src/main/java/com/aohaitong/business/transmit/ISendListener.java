package com.aohaitong.business.transmit;


public interface ISendListener {
    void sendSuccess();

    void sendFail(String reason);

}
