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

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.util.Log.e;

public class RecipeCommentDialog extends Dialog {
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

    public RecipeCommentDialog(Context context, String content, String commentId) {
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
                RecipeCommentActivity.commentEdit.requestFocus();
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        RecipeCommentActivity.commentEdit.setText(content);
                        RecipeCommentActivity.commentInputBtn.setText("수정");
                        RecipeCommentActivity.modifyCommentId = commentId;
                        RecipeCommentActivity.imm.showSoftInput(RecipeCommentActivity.commentEdit, InputMethodManager.SHOW_FORCED);
                    }
                }, 100);
            }
        });

        deleteBtn = (ImageView)findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    public void run() {
                        Response response = null;
                        try {
                            //업로드는 타임 및 리드타임을 넉넉히 준다.
                            OkHttpClient toServer = new OkHttpClient.Builder()
                                    .connectTimeout(30, TimeUnit.SECONDS)
                                    .readTimeout(30, TimeUnit.SECONDS)
                                    .build();
                            //요청 Body 세팅==> 그전 Query Parameter세팅과 같은 개념
                            RequestBody fileUploadBody = new FormBody.Builder()
                                    .add("msg", "success")
                                    .build();
                            //요청 세팅
                            Request request = new Request.Builder()
                                    .url(String.format(NetworkDefineConstant.SERVER_URL_COMMENT, commentId))
                                    .delete(fileUploadBody) //반드시 post로
                                    .build();
                            //동기 방식
                            response = toServer.newCall(request).execute();

                            flag = response.isSuccessful();
                            //응답 코드 200등등
                            int responseCode = response.code();
                            if (flag) {
                                e("좋아요취소response결과", response.message()); //읃답에 대한 메세지(OK)
                                e("response응답바디", response.body().string()); //json으로 변신
                            }
                        } catch (UnknownHostException une) {
                            e("aaa", une.toString());
                        } catch (UnsupportedEncodingException uee) {
                            e("bbb", uee.toString());
                        } catch (Exception e) {
                            e("ccc", e.toString());
                        } finally {
                            if(response != null) {
                                response.close();
                            }
                        }
                    }
                }).start();
                dismiss();
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
