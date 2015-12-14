package com.example.larry.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 067231 on 2015/12/14.
 */
public class ConfigStore {

    //****************************************************************
    // 判断应用是否初次加载，读取SharedPreferences中的guide_activity字段
    //****************************************************************
    private static final String SHAREDPREFERENCES_NAME = "my_pref";
    private static final String KEY_GUIDE_ACTIVITY = "guide_activity";
    public static boolean isFirstEnter(Context context, String className){
        if(context==null || className==null||"".equalsIgnoreCase(className))return false;
        String mResultStr = context.getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE)
                .getString(KEY_GUIDE_ACTIVITY, "");//取得所有类名 如 com.my.MainActivity
        if(mResultStr.equalsIgnoreCase("false"))
            return false;
        else
            return true;
    }

    public static void writeFirstEnter(Context context, String className){
        SharedPreferences sp = context.getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_GUIDE_ACTIVITY, "true");
        editor.commit();
    }

}
