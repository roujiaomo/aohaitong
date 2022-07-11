package com.aohaitong.nettools;


import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.aohaitong.constant.CommonConstant;
import com.aohaitong.nettools.inter.InternetRequest;
import com.aohaitong.nettools.inter.MyCallBack;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OKTool implements InternetRequest {
    private final Gson gson;
    private final OkHttpClient okHttpClient;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final MediaType json = MediaType.parse("application/json;charset=utf-8");

    public OKTool() {


        if (Build.VERSION.SDK_INT < 29) {
            okHttpClient = new OkHttpClient.Builder()
                    .hostnameVerifier(new TrustAllHostnameVerifier())
                    .sslSocketFactory(createSSLSocketFactory()).connectTimeout(4, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true).build();
        } else {
            okHttpClient = new OkHttpClient.Builder()
                    .hostnameVerifier(new TrustAllHostnameVerifier())
                    .retryOnConnectionFailure(true).build();
        }
        gson = new Gson();
    }

    //自定义SS验证相关类
    private static class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }
        return ssfFactory;
    }


    @Override
    public <T> void startRequest(String url, final Class<T> tClass, final MyCallBack<T> tMyCallBack) {
        final Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull final IOException e) {
                handler.post(() -> tMyCallBack.error(e));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //将成功请求到到数据解析成对应的bean或数据类型
                try {
                    String back = response.body() != null ? response.body().string() : null;
                    final T result = gson.fromJson(back, tClass);
                    handler.post(() -> tMyCallBack.success(result));
                } catch (Exception e) {

                }
            }
        });
    }

    @Override
    public <T> void startPostRequest(String url, final Class<T> tClass, Map<String, Object> postData, final MyCallBack<T> myCallBack) {
        Log.d(CommonConstant.LOGCAT_TAG, gson.toJson(postData));
        RequestBody requestBody = RequestBody.create(json, gson.toJson(postData));
        Request request = new Request.Builder().post(requestBody).addHeader("Content-Type", "application/json")
                .addHeader("Connection", "keep-Alive").url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull final IOException e) {
                Log.d(CommonConstant.LOGCAT_TAG, url + "\n" + e.getMessage());
                handler.post(() -> myCallBack.error(e));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //将成功请求到到数据解析成对应的bean或数据类型
                if (response.code() == 200) {
                    String back = response.body() != null ? response.body().string() : null;
                    final T result = gson.fromJson(back, tClass);
                    Log.d(CommonConstant.LOGCAT_TAG, url + "\n" + back);
                    handler.post(() -> myCallBack.success(result));
                } else {
                    Log.d(CommonConstant.LOGCAT_TAG, url + "\n" + response.code());
                    handler.post(() -> myCallBack.error(new Throwable("code" + response.code())));
                }
            }
        });
    }
}
