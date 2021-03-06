package com.itgowo.http;

import android.os.Handler;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RequestClient implements Runnable {
    private URL url;
    private String reqestStr;
    private String requestMethod = "POST";
    private onCallbackListener listener;
    private boolean callbackOnMainThread;
    private Handler handler;
    private int timeout = 15000;
    private Map<String, String> headers = new HashMap<>();
    private HttpResponse response = new HttpResponse();

    public RequestClient(String url, String method, Map<String, String> headers, String reqestStr, Handler handler, int timeout, onCallbackListener listener) {
        this.listener = listener;
        this.reqestStr = reqestStr == null ? "" : reqestStr;
        this.requestMethod = method;
        response.setMethod(requestMethod);
        this.callbackOnMainThread = handler != null;
        this.handler = handler;
        if (headers != null) {
            this.headers.putAll(headers);
        }
        this.timeout = timeout;
        try {
            if (url != null) {
                if (!url.startsWith("http://") & !url.startsWith("https://")) {
                    url = "http://" + url;
                }
            }
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            onError(response.setSuccess(false), e);
        }
    }

    @Override
    public void run() {
        HttpURLConnection httpConn = null;
        try {
            httpConn = (HttpURLConnection) url.openConnection();
            //设置参数
            httpConn.setDoOutput(true);     //需要输出
            httpConn.setDoInput(true);      //需要输入
            httpConn.setUseCaches(false);   //不允许缓存
            httpConn.setRequestMethod(requestMethod);      //设置POST方式连接
            httpConn.setReadTimeout(timeout);

            //设置请求属性
            httpConn.setRequestProperty("Content-Type", "application/json");
            httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
            httpConn.setRequestProperty("Charset", "UTF-8");

            for (Map.Entry<String, String> header : this.headers.entrySet()) {
                httpConn.setRequestProperty(header.getKey(),header.getValue());
            }

            //连接,也可以不用明文connect，使用下面的httpConn.getOutputStream()会自动connect
            httpConn.connect();

            //建立输入流，向指向的URL传入参数
            BufferedWriter bos = new BufferedWriter(new OutputStreamWriter(httpConn.getOutputStream()));
            bos.write(reqestStr);
            bos.flush();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            InputStream inputStream = httpConn.getInputStream();
            byte[] bytes = new byte[1024];
            int count;
            while ((count = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, count);
            }
            response.setBody(outputStream.toByteArray());

            //获得响应状态
            final int resultCode = httpConn.getResponseCode();
            if (HttpURLConnection.HTTP_OK == resultCode) {
                onSuccess(response);
            } else {
                onError(response.setSuccess(false), new Exception("http code:" + resultCode));
            }
        } catch (IOException e) {
            onError(response.setSuccess(false), e);
        }
    }

    private void onSuccess(final HttpResponse response) {
        if (callbackOnMainThread) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onSuccess(response);
                }
            });
        } else {
            listener.onSuccess(response);
        }
    }

    private void onError(final HttpResponse response, final Exception e) {
        if (callbackOnMainThread) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onError(response, e);
                }
            });
        } else {
            listener.onError(response, e);
        }
    }
}
