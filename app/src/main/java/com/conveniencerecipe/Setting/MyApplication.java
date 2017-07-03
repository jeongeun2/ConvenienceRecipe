package com.conveniencerecipe;

import android.app.Application;
import android.content.Context;

/**
 * Created by ccei on 2016-07-19.
 */
public class MyApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static  Context RecipeContext(){
        return mContext;
    }

}
