package com.aohaitong.nettools.inter;

import java.util.Map;

public interface InternetRequest {
    <T> void startRequest(String url, Class<T> tClass, MyCallBack<T> myCallBack);

    <T> void startPostRequest(String url, Class<T> tClass, Map<String, Object> postData, MyCallBack<T> myCallBack);
}
