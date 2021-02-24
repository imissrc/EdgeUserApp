package com.edgeuserapp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.edgeuserapp.collector.ActivityCollector;

public class BaseActivity extends AppCompatActivity {
    protected LoginOutBroadcastReceiver loginOutBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 创建活动时，将其加入管理器中
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 注册广播接收器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.gesoft.admin.loginout");
        loginOutBroadcastReceiver = new LoginOutBroadcastReceiver();
        registerReceiver(loginOutBroadcastReceiver, intentFilter);

    }


    @Override
    protected void onPause() {
        super.onPause();

        // 取消注册广播接收器
        unregisterReceiver(loginOutBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 销毁活动时，将其从管理器中移除
        ActivityCollector.removeActivity(this);

    }
    public class LoginOutBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ActivityCollector.finishAllActivities();
            Intent intent1 = new Intent(context, MainActivity.class);
            context.startActivity(intent1);
        }
    }
}
