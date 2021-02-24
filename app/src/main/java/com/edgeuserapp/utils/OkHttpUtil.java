package com.edgeuserapp.utils;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import okhttp3.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import com.alibaba.fastjson.JSONObject;
import com.edgeuserapp.application.MainApplication;

public class OkHttpUtil {

    //    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json;charset=utf-8");
    private static final String TAG = "OkHttp3";

    /**
     * 单例引用
     */
    private static volatile OkHttpUtil mInstance;

    private OkHttpClient mOkHttpClient;
    /**
     全局处理子线程和M主线程通信
     */
    private Handler okHttpHandler;

    public OkHttpUtil(Context context) {
        // 初始化OkHttpClient
        // 设置超时时间,读取超时时间,写入超时时间
        mOkHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        // 初始化Handler
        okHttpHandler = new Handler(context.getMainLooper());
    }

    /**
     * 获取单例引用
     * @return
     */
    public static OkHttpUtil getInstance(Context context) {
        OkHttpUtil instance = mInstance;
        if (instance == null) {
            synchronized (OkHttpUtil.class) {
                instance = mInstance;
                if (instance == null) {
                    instance = new OkHttpUtil(context.getApplicationContext());
                    mInstance = instance;
                }
            }
        }
        return instance;
    }


    public <T> Call getRequestAsyn(String SERVER_URL, String actionUrl, int requestType, HashMap<String, String> paramsMap, HashMap<String, String> header, ReqCallBack<T> callBack) {
        Call call = null;
        switch (requestType) {
            case MainApplication.TYPE_GET:
                call = requestGetByAsyn(SERVER_URL, actionUrl, paramsMap, header, callBack);
                break;
            default:
                break;
        }
        return call;
    }

    public <T> Call postRequestAsyn(String SERVER_URL, String actionUrl, int requestType, JSONObject jsonParams, ReqCallBack<T> callBack) {
        Call call = null;
        switch (requestType) {
            case MainApplication.TYPE_POST_JSON:
                call = requestPostByAsyn(SERVER_URL, actionUrl, jsonParams, callBack);
                break;
            default:
                break;
        }
        return call;
    }

    /**
     * okHttp get同步请求
     * @param actionUrl 接口地址
     * @param paramsMap 请求参数
     */
    private void requestGetBySyn(String serverUrl, String actionUrl, HashMap<String, String> paramsMap) {
        StringBuilder tempParams = new StringBuilder();
        try {
            //处理参数
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                //对参数进行URLEncoder
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                pos++;
            }
            //补全请求地址
            String requestUrl = String.format("%s/%s?%s", serverUrl, actionUrl, tempParams.toString());
            //创建一个请求
            Request request = addHeaders().url(requestUrl).build();
            //创建一个Call
            final Call call = mOkHttpClient.newCall(request);
            //执行请求
            final Response response = call.execute();
            response.body().string();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * okHttp post同步请求
     * @param actionUrl 接口地址
     * @param jsonParams 请求参数
     */
    private void requestPostBySyn(String serverUrl, String actionUrl, JSONObject jsonParams) {
        try {
            //生成参数
            String stringParams = jsonParams.toJSONString();
            //创建一个请求实体对象 RequestBody
            RequestBody body = RequestBody.create(stringParams, MEDIA_TYPE_JSON);
            //补全请求地址
            String requestUrl = String.format("%s/%s", serverUrl, actionUrl);
            //创建一个请求
            final Request request = addHeaders().url(requestUrl).post(body).build();
            //创建一个Call
            final Call call = mOkHttpClient.newCall(request);
            //执行请求
            Response response = call.execute();
            //请求执行成功
            if (response.isSuccessful()) {
                //获取返回数据 可以是String，bytes ,byteStream
                Log.i(TAG, "response ----->" + response.body().string());
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * okHttp get异步请求
     * @param actionUrl 接口地址
     * @param paramsMap 请求参数
     * @param callBack  请求返回数据回调
     * @param <T>       数据泛型
     * @return
     */
    private <T> Call requestGetByAsyn(String serverUrl, String actionUrl, HashMap<String, String> paramsMap, HashMap<String,String>header,final ReqCallBack<T> callBack) {
        StringBuilder tempParams = new StringBuilder();
        try {
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                pos++;
            }
            Headers setHeader = SetHeaders(header);
            String requestUrl = String.format("%s/%s?%s", serverUrl, actionUrl, tempParams.toString());
            //创建一个request对象
            final Request request = addHeaders().url(requestUrl).headers(setHeader).build();
            // newCall一个call对象
            final Call call = mOkHttpClient.newCall(request);
            // 异步执行请求，callback回调函数
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    failedCallBack("访问失败", callBack);
                    Log.e(TAG, e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String string = response.body().string();
                        Log.i(TAG, "response ----->" + string);
                        successCallBack((T) string, callBack);
                    } else {
                        failedCallBack(response.body().string(), callBack);
                    }
                }
            });
            return call;
        } catch (Exception e) {
            Log.e(TAG,paramsMap.toString()+"====="+ e.toString());
        }
        return null;
    }

    private <T> Call requestPostByAsyn(String serverUrl, String actionUrl, JSONObject jsonParams, final ReqCallBack<T> callBack) {
        try {
            String stringParams = jsonParams.toJSONString();
            RequestBody body = RequestBody.create(stringParams, MEDIA_TYPE_JSON);
            String requestUrl = String.format("%s/%s", serverUrl, actionUrl);
            final Request request = addHeaders().url(requestUrl).post(body).build();
            final Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    failedCallBack("访问失败", callBack);
                    Log.e(TAG, e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String string = response.body().string();
                        Log.i(TAG, "response ----->" + string);
                        successCallBack((T) string, callBack);
                    } else {
                        failedCallBack(response.body().string(), callBack);
                    }
                }
            });
            return call;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

    public interface ReqCallBack<T> {
        /**
         * 响应成功
         */
        void onReqSuccess(T result);

        /**
         * 响应失败
         */
        void onReqFailed(String errorMsg);
    }

    /**
     * 统一为请求添加头信息
     * @return
     */
    private Request.Builder addHeaders() {
        Request.Builder builder = new Request.Builder()
                .addHeader("Connection", "keep-alive")
                .addHeader("platform", "2")
                .addHeader("phoneModel", Build.MODEL)
                .addHeader("systemVersion", Build.VERSION.RELEASE)
                .addHeader("appVersion", "3.2.0");
        return builder;
    }
    /**
     * setheaders 从hashmap中获取header
     * @param headersParams header参数
     * @return 建立的header
     */
    public static Headers SetHeaders(HashMap<String, String> headersParams) {
        Headers headers = null;
        Headers.Builder headersbuilder = new Headers.Builder();
        if (!headersParams.isEmpty()) {
            Iterator<String> iterator = headersParams.keySet().iterator();
            String key = "";
            while (iterator.hasNext()) {
                key = iterator.next().toString();
                headersbuilder.add(key, headersParams.get(key));
            }
        }
        headers = headersbuilder.build();
        return headers;
    }
    /**
     * 统一处理成功信息
     * @param result
     * @param callBack
     * @param <T>
     */
    private <T> void successCallBack(final T result, final ReqCallBack<T> callBack) {
        okHttpHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onReqSuccess(result);
                }
            }
        });
    }

    /**
     * 统一处理失败信息
     * @param errorMsg
     * @param callBack
     * @param <T>
     */
    private <T> void failedCallBack(final String errorMsg, final ReqCallBack<T> callBack) {
        okHttpHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onReqFailed(errorMsg);
                }
            }
        });
    }

}
