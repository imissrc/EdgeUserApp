package com.edgeuserapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 *  使用SharePreferences实现数据的存取的工具类
 */
public class SPDataUtils {
    private static final String spFileName = "edgeUserAppData";  // 文件名称
    private SharedPreferences sharedPreferences;
    /**
     * 保存用户信息
     * */
    public void initSP(Context context) {
        // Context.MODE_PRIVATE: 指定该SharedPreferences数据只能被本应用程序读、写
        sharedPreferences = context.getSharedPreferences(spFileName, Context.MODE_PRIVATE);
    }

    public boolean setStringNameStringValue(String name, String value) {
        boolean flag = false;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name, value);
        editor.apply();
        flag = true;
        return flag;
    }

    public boolean setStringNameBooleanValue(String name, boolean value) {
        boolean flag = false;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(name, value);
        editor.apply();
        flag = true;
        return flag;
    }

    public String getStringValueByStringName(String name) {
        return sharedPreferences.getString(name, null);
    }

    public boolean getBooleanValueByStringName(String name) {
        return sharedPreferences.getBoolean(name, false);
    }

    public boolean hasStringName(String name) {
        return sharedPreferences.contains(name);
    }

}
