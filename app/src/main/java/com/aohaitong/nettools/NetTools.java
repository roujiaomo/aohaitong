package com.aohaitong.nettools;


import com.aohaitong.nettools.inter.InternetRequest;
import com.aohaitong.nettools.inter.MyCallBack;

import java.util.Map;

public class NetTools implements InternetRequest {
    private static NetTools ourInstance;
    private final InternetRequest internetRequest;

    public static NetTools getInstance() {
        if (ourInstance == null) {
            synchronized (NetTools.class) {
                if (ourInstance == null) {
                    ourInstance = new NetTools();
                }
            }
        }
        return ourInstance;
    }

    private NetTools() {
        internetRequest = new OKTool();
    }


    @Override
    public <T> void startRequest(String url, Class<T> tClass, MyCallBack<T> myCallBack) {
        internetRequest.startRequest(url, tClass, myCallBack);
    }

    @Override
    public <T> void startPostRequest(String url, Class<T> tClass, Map<String, Object> postData, MyCallBack<T> myCallBack) {
        internetRequest.startPostRequest(url, tClass, postData, myCallBack);
    }

}
