package com.conveniencerecipe;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

public class CustomProgressDialog extends Dialog{
    public CustomProgressDialog(Context context) {
        super(context , android.R.style.Theme_Translucent_NoTitleBar);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_progress); // 다이얼로그에 박을 레이아웃
    }
}
