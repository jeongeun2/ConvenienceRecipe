package com.conveniencerecipe;

/**
 * Created by ccei on 2016-08-16.
 */
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

public class QnaCommentDialog extends Dialog {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.6f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.comment_dialog);

        setLayout();
    }

    String content, commentId;

    public QnaCommentDialog(Context context, String content, String commentId) {
        // Dialog 배경을 투명 처리 해준다.
        super(context , android.R.style.Theme_Translucent_NoTitleBar);
        this.content = content;
        this.commentId = commentId;
    }

    private ImageView modifyBtn;
    private ImageView deleteBtn;
    private ImageView copyBtn;

    boolean flag;
    private void setLayout(){
        modifyBtn = (ImageView)findViewById(R.id.modify_btn);
        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                QnaDetailActivity.commentEdit.setText(content);
                QnaDetailActivity.commentInputBtn.setText("수정");
                QnaDetailActivity.modifyCommentId = commentId;
                QnaDetailActivity.commentEdit.requestFocus();
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        QnaDetailActivity.imm.showSoftInput(QnaDetailActivity.commentEdit, InputMethodManager.SHOW_FORCED);
                    }
                }, 100);
            }
        });

        deleteBtn = (ImageView)findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CancelCommentDelete commentDelete = new CancelCommentDelete(getContext(), commentId);
                commentDelete.show();
            }
        });

        copyBtn = (ImageView)findViewById(R.id.copy_btn);
        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) MyApplication.RecipeContext().getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setText(content);
                Toast.makeText(getContext(), "클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }
}
