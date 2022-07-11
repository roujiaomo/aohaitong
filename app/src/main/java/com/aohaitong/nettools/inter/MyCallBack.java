package com.aohaitong.nettools.inter;

public interface MyCallBack<T> {
    void success(T respomse);

    void error(Throwable throwable);
}
