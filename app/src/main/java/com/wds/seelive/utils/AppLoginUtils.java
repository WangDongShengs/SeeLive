package com.wds.seelive.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

public class AppLoginUtils {
    public static boolean isValidUserCount(String count){
        //判断手机号是否正确   如果长度==11就返回true
        if(!TextUtils.isEmpty(count)){
            if(count.length() == 11){
                return true;
            }
        }
        return false;
    }
    //判断密码长度大于等于6 大于就返回true
    public static boolean isValidUserPassword(String password){
        if(!TextUtils.isEmpty(password)){
            if(password.length() >= 6){
                return true;
            }
        }
        return false;
    }
    public static boolean isOnInternet(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
