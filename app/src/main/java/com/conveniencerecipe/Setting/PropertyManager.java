package com.conveniencerecipe;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * 편의점을 부탁해 PJ ID를 관리하는 Preference관리 클래스
 */
public class PropertyManager {
    private static PropertyManager instance;

    public static PropertyManager getInstance() {
        if (instance == null) {
            instance = new PropertyManager();
        }
        return instance;
    }

    SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;

    private PropertyManager() {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.RecipeContext());
        mEditor = mPrefs.edit();
    }
    /*
     서버로 넘길 ID 또는 토큰 값
     */
    private static final String USER__ID = "user_id";
    private static final String USER_NICK_NAME = "nick_name";
    private static final String ID = "id";
    private static final String Token = "token";
    private static final String PUSH = "push";

    public void setUUID(String id) {
        mEditor.putString(USER__ID, id);
        mEditor.commit();
    }


    public String getUUID() {
        return mPrefs.getString(USER__ID, "");
    }



    public void setPUSH(String push) {
        mEditor.putString(PUSH, push);
        mEditor.commit();
    }


    public String getPUSH() {
        return mPrefs.getString(PUSH, "");
    }



    public void setToken(String token) {
        mEditor.putString(Token, token);
        mEditor.commit();
    }


    public String getToken() {
        return mPrefs.getString(Token, "");
    }


    public void setUserNickName(String nickName) {
        mEditor.putString(USER_NICK_NAME, nickName);
    }
    public String getUserNickName() {
        return mPrefs.getString(USER_NICK_NAME, "");
    }


    public void setId(String getid) {
        mEditor.putString(ID, getid);
        mEditor.commit();
    }
    public String getId() {
        return mPrefs.getString(ID, "");
    }
}