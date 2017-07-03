package com.conveniencerecipe;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Created by ccei on 2016-08-19.
 */
public class QnaDetailDialog extends Dialog {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.6f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.qna_dialog);

        setLayout();
    }

    public QnaDetailDialog(Context context) {
        // Dialog 배경을 투명 처리 해준다.
        super(context , android.R.style.Theme_Translucent_NoTitleBar);
    }

    private ImageView modifyBtn;
    private ImageView deleteBtn;

    boolean flag;
    private void setLayout(){
        modifyBtn = (ImageView)findViewById(R.id.modify_btn);
        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                Intent intent = new Intent(MyApplication.RecipeContext(), QnaWriteActivity.class);
                intent.putExtra("modify", true);
                getContext().startActivity(intent);
            }
        });

        deleteBtn = (ImageView)findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CancelDeleteQna cancelDeleteQna = new CancelDeleteQna(getContext());
                cancelDeleteQna.show();
                dismiss();
            }
        });
    }
}