package com.edgeuserapp.application;

import android.app.Application;
import com.edgeuserapp.utils.SPDataUtils;

public class MainApplication extends Application {
    private SPDataUtils spDataUtils;
    private boolean isLogin;
    private String serverUrl = "http://192.168.0.106:8092";
    public static final int TYPE_GET = 1;
    public static final int TYPE_POST_JSON = 2;

    @Override
    public void onCreate() {
        super.onCreate();
        spDataUtils.initSP(this);
        if(!spDataUtils.hasStringName("isLogin")) {
            setLoginStatus(false);
        }
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public boolean isLogin() {
        return spDataUtils.getBooleanValueByStringName("isLogin");
    }

    public void setLoginStatus(boolean loginStatus) {
        isLogin = loginStatus;
        spDataUtils.setStringNameBooleanValue("isLogin", loginStatus);
    }

    public void setUserLoginInfo(String username, String password, String token) {
        spDataUtils.setStringNameStringValue("username", username);
        spDataUtils.setStringNameStringValue("password", password);
        spDataUtils.setStringNameStringValue("token", token);
    }

    public String getUsername() {
        return spDataUtils.getStringValueByStringName("username");
    }

    public String getPassword() {
        return spDataUtils.getStringValueByStringName("password");
    }

    public String getToken() {
        return spDataUtils.getStringValueByStringName("token");
    }

    public boolean isRememberPassword() {
        return spDataUtils.getBooleanValueByStringName("isRememberPassword");
    }

    public void setRememberPassword(boolean rememberPassword) {
        spDataUtils.setStringNameBooleanValue("rememberPassword", rememberPassword);
    }

}
