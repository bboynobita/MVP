package com.lv.demo.mvp.presenter;

import com.lv.demo.mvp.model.MvpCallback;
import com.lv.demo.mvp.model.MvpModel;
import com.lv.demo.mvp.view.MvpView;

public class MvpPresenter {
    // View接口
    private MvpView mView;

    public MvpPresenter(MvpView view) {
        this.mView = view;
    }
    /**
     * 获取网络数据
     *
     * @param params 参数
     */
    public void getData(String params) {
        //显示正在加载进度条
        mView.showLoading();
        // 调用Model请求数据
        MvpModel.getNetData(params, new MvpCallback() {
            @Override
            public void onSuccess(String data) {
                //调用view接口显示数据
                mView.showData(data);
            }

            @Override
            public void onFailure(String msg) {
                //调用view接口提示失败信息
                mView.showFailureMessage(msg);
            }

            @Override
            public void onError() {
                //调用view接口提示请求异常
                mView.showErrorMessage();
            }

            @Override
            public void onComplete() {
                // 隐藏正在加载进度条
                mView.hideLoading();
            }
        });
    }
}