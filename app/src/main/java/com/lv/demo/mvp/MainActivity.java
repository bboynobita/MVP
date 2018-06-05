package com.lv.demo.mvp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lv.demo.mvp.presenter.MvpPresenter;
import com.lv.demo.mvp.view.MvpView;

public class MainActivity extends AppCompatActivity implements MvpView {

    private ProgressDialog progressDialog;
    private MvpPresenter presenter;
    private TextView textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textview = findViewById(R.id.textview);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("正在加载数据");
        presenter = new MvpPresenter(this);

    }

    /**
     * 点击事件
     * @param view
     */
    public void getDatas(View view) {
        presenter.getData("normal");
    }

    /**
     * 显示正在加载进度框
     */
    @Override
    public void showLoading() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    /**
     * 隐藏正在加载进度框
     */
    @Override
    public void hideLoading() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /**
     * 当数据请求成功后，调用此接口显示数据
     *
     * @param data 数据源
     */
    @Override
    public void showData(String data) {
        textview.setText(data);
    }

    /**
     * 当数据请求失败后，调用此接口提示
     *
     * @param msg 失败原因
     */
    @Override
    public void showFailureMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 当数据请求异常，调用此接口提示
     */
    @Override
    public void showErrorMessage() {
        Toast.makeText(this, "网络请求数据出现异常", Toast.LENGTH_SHORT).show();
    }


}
