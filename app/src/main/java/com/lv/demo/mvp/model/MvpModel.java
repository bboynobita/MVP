package com.lv.demo.mvp.model;

import android.os.Handler;

public class MvpModel {
    /**
     * 获取网络接口数据
     *
     *
     * @param param    请求参数
     * @param callback 数据回调接口
     */
    public static void getNetData(final String param, final MvpCallback callback) {
        // 利用postDelayed方法模拟网络请求数据的耗时操作
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess("模拟网络请求得到的数据");
                callback.onComplete();
            }
        }, 2000);
    }
}