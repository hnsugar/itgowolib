package com.itgowo.http;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author lujianchao
 * 2018-10-15
 * Github：https://github.com/hnsugar
 * WebSite: http://itgowo.com
 * QQ:1264957104
 */
public class HttpClient {
    private static final String TAG = "itgowo-HttpClient";
    private static int timeout = 15000;
    private static Handler handler = new Handler(Looper.getMainLooper());
    private static ExecutorService executorService = new ThreadPoolExecutor(2, 15, 60 * 1000 * 3, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(), new ThreadFactory() {
        @Override
        public Thread newThread(@NonNull Runnable r) {
            Thread thread = new Thread(r);
            thread.setName(TAG);
            return thread;
        }
    });

    public static void setTimeout(int timeout1) {
        timeout = timeout1;
    }

    public static void RequestGet(String url, Map<String, String> headers, String requestJson, onCallbackListener listener) {
        Request(url, Method.GET, headers, requestJson, true, listener);
    }

    public static void RequestPOST(String url, Map<String, String> headers, String requestJson, onCallbackListener listener) {
        Request(url, Method.POST, headers, requestJson, true, listener);
    }

    public static void Request(String url, Method method, Map<String, String> headers, String requestJson, boolean callbackOnMainThread, onCallbackListener listener) {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        executorService.execute(new RequestClient(url, method.getMethod(), headers, requestJson, callbackOnMainThread ? handler : null, timeout, listener));
    }

    public static void uploadFiles(String url, List<String> uploadFiles, boolean callbackOnMainThread, onUploadFileCallbackListener listener) {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        executorService.execute(new RequestClientUnloadFile(url, uploadFiles, callbackOnMainThread ? handler : null, timeout, listener));
    }

    public static void downloadFile(String url, String fileDir, boolean callbackOnMainThread, onDownloadFileCallbackListener listener) {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        executorService.execute(new RequestClientDownloadFile(url, fileDir, callbackOnMainThread ? handler : null, timeout, listener));
    }

    public static HttpResponse RequestSync(String url, Method method, Map<String, String> headers, String requestJson) throws Exception {
        FutureTask futureTask = (FutureTask) executorService.submit(new RequestClientSync(url, method.getMethod(), headers, timeout, requestJson));
        return (HttpResponse) futureTask.get();
    }

}
