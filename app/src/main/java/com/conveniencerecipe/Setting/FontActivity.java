package com.conveniencerecipe;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Created by ccei on 2016-08-09.
 */
public class FontActivity extends AppCompatActivity {
    AppCompatDelegate mDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getLayoutInflater().setFactory2(this);
        mDelegate = getDelegate();
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        View view = super.onCreateView(parent, name, context, attrs);
        if(view == null) {
            view = mDelegate.createView(parent, name, context, attrs);
        }
        view = setCustomFont(view, name, context, attrs);
        return view;
    }

    public View setCustomFont(View view, String name, Context context, AttributeSet attrs) {
        if(view == null) {
            if(name.equals("TextView")) {
                view = new TextView(context, attrs);
            }
        }
        if(view != null && view instanceof TextView) {
            TextView tv = (TextView)view;
            tv.setTypeface(TypefaceManager.getInstance().getTypeface(context, TypefaceManager.FONT_NAME));
        }
        return view;
    }
}
