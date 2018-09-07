package com.cyh.saopdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cyh.saop.annotation.DebugLog;
import com.cyh.saop.annotation.DiskCache;
import com.cyh.saop.annotation.IOThread;
import com.cyh.saop.annotation.Intercept;
import com.cyh.saop.annotation.MainThread;
import com.cyh.saop.annotation.MemoryCache;
import com.cyh.saop.annotation.Permission;
import com.cyh.saop.annotation.Safe;
import com.cyh.saop.annotation.SingleClick;
import com.cyh.saop.consts.PermissionConsts;
import com.cyh.saop.enums.ThreadType;
import com.cyh.saop.logger.XLogger;
import com.cyh.saop.util.AppExecutors;

import static com.cyh.saopdemo.App.TRY_CATCH_KEY;

/**
 * File: HomeActivity.java
 * Author: cyh
 * Date: 2018/9/6
 */
public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    TextView mTvText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mTvText = findViewById(R.id.tv_text);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.btn_single_click:
                handleOnClick(v);
                break;
            case R.id.btn_request_permission:
                handleRequestPermission(v);
                break;
            case R.id.btn_main_thread:
                AppExecutors.get().networkIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        doInMainThread(v);
                    }
                });
                break;
            case R.id.btn_io_thread:
                doInIOThread(v);
                break;
            case R.id.btn_try_catch:
                int result = getNumber();
                mTvText.setText("结果为:" + result);
                break;
            case R.id.btn_lambda:
                AppExecutors.get().networkIO().execute(() -> doInMainThread(v));
                break;
            default:
                break;
        }
    }


    @SingleClick
    @Permission({PermissionConsts.CALENDAR, PermissionConsts.CAMERA, PermissionConsts.LOCATION})
    private void handleRequestPermission(View v) {

    }

    @SingleClick(5000)
    @DebugLog(priority = Log.ERROR)
    @Intercept(3)
    public void handleOnClick(View v) {
        XLogger.e("点击响应！");
        Toast.makeText(HomeActivity.this,"点击响应！", Toast.LENGTH_SHORT).show();
        hello("HomeActivity", "handle OnClick 666666");
    }


    @DebugLog(priority = Log.ERROR)
    @Intercept({1,2,3})
//    @MemoryCache
    @DiskCache
    private String hello(String name, String cardId) {
        return "hello, " + name + "! Your CardId is " + cardId + ".";
    }


    @MainThread
    private void doInMainThread(View v) {
        mTvText.setText("工作在主线程");
    }

    @IOThread(ThreadType.Single)
    @MemoryCache("12345")
//    @DiskCache
    private String doInIOThread(View v) {
        return "io线程名:" + Thread.currentThread().getName();
    }


    @Safe(TRY_CATCH_KEY)
    private int getNumber() {
        return 100 / 0;
    }
}
