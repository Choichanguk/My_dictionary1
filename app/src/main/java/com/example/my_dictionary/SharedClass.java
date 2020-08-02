package com.example.my_dictionary;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedClass {
    private static final String PREFERENCES_NAME = "유저 로그인 정보";

    private static SharedPreferences getPreferences(Context context){
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static void saveUserId(Context context, String ID){
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_id", ID);
        editor.apply();
    }

    public static void saveLoginStatus(Context context, boolean islogin){
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_login", islogin);
        editor.apply();
    }

    public static String loadUserId(Context context) {
        SharedPreferences sharedPreferences = getPreferences(context);
        String user_id = sharedPreferences.getString("user_id", null);

        if(user_id == null){
            user_id = "";
        }
        return user_id;
    }

    public static boolean loadLoginStatus(Context context) {
        SharedPreferences sharedPreferences = getPreferences(context);
        Boolean is_login = sharedPreferences.getBoolean("is_login", false);
        return is_login;
    }

    public static void saveUserIndex(Context context, String index){
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_index", index);
        editor.apply();
    }

    public static String loadUserIndex(Context context) {
        SharedPreferences sharedPreferences = getPreferences(context);
        String user_id = sharedPreferences.getString("user_index", null);

        if(user_id == null){
            user_id = "";
        }
        return user_id;
    }



}
