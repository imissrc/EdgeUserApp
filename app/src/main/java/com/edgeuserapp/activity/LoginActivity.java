package com.edgeuserapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.edgeuserapp.R;
import com.edgeuserapp.application.MainApplication;
import com.edgeuserapp.utils.OkHttpUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


import java.util.HashMap;

public class LoginActivity extends BaseActivity {

    private MainApplication mainApplication;
    private TextView registerTv;
    private Button loginBtn;
    private EditText usernameEt, passwordEt;
    private ImageView usernameClear, passwordClear;
    private CheckBox rememberPassword;
    private boolean isRememberPassword;
    private static final String TAG = "LoginActivity";
    private String username;
    private String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mainApplication = (MainApplication) getApplication();

        init();
    }

    public void init() {
        usernameEt = findViewById(R.id.et_username);
        passwordEt = findViewById(R.id.et_password);
        usernameClear = findViewById(R.id.iv_usernameClear);
        passwordClear = findViewById(R.id.iv_passwordClear);
        loginBtn = findViewById(R.id.btn_login);
        registerTv = findViewById(R.id.tv_register);
        rememberPassword = findViewById(R.id.cb_checkbox);

        if (mainApplication.getUsername() != null) {
            usernameEt.setText(mainApplication.getUsername());
            if (mainApplication.isRememberPassword()) {
                isRememberPassword = true;
                rememberPassword.setChecked(true);
                passwordEt.setText(mainApplication.getPassword());
            }
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameEt.getText().toString().trim();
                password = passwordEt.getText().toString().trim();
                if (TextUtils.isEmpty(username)) {
                    showMessage("用户名不能为空");
                } else if (TextUtils.isEmpty(password)) {
                    showMessage("密码不能为空");
                }
                loginRequest();
            }
        });

        registerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 指定接下来要启动的class
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, 1);
            }
        });

//        rememberPassword.setOnClickListener(new CompoundButton.OnCheckedChangeListener(){
//
//        });
    }

    private void loginRequest() {
        HashMap<String, String> header = new HashMap<>();

        JSONObject jsonParams = new JSONObject();
        jsonParams.put("adminId", username);
        jsonParams.put("password", password);
        OkHttpUtil.getInstance(getBaseContext()).postRequestAsyn(mainApplication.getServerUrl(), "admin/login", mainApplication.TYPE_POST_JSON, jsonParams, new OkHttpUtil.ReqCallBack<String>() {
            @Override
            public void onReqSuccess(String result) {
                Log.i("登录成功", username);
                if (JSON.parseObject(result).getBoolean("success")) {
                    JSONObject response = JSON.parseObject(result).getJSONObject("result");
                    String token = response.getString("token");
                    mainApplication.setLoginStatus(true);
                    mainApplication.setUserLoginInfo(username, password, token);

                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onReqFailed(String errorMessage) {
                Log.e(TAG, errorMessage);
                Toast.makeText(getBaseContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 显示信息的方法
    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
