package com.conveniencerecipe;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.util.Log.e;

/**
 * Created by ccei on 2016-08-28.
 */
public class CancelDeleteRecipe extends Dialog {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.6f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.cancel_recipe_dialog);

        setLayout();
    }

    public CancelDeleteRecipe(Context context) {
        // Dialog 배경을 투명 처리 해준다.
        super(context , android.R.style.Theme_Translucent_NoTitleBar);
    }

    private ImageView cancelImage;
    private ImageView yesBtn;
    private ImageView noBtn;

    boolean flag;
    private void setLayout(){
        cancelImage = (ImageView)findViewById(R.id.cancel_image);
        cancelImage.setImageResource(R.drawable.delete_recipe);

        yesBtn = (ImageView)findViewById(R.id.yes_btn);
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
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
                                    .url(String.format(NetworkDefineConstant.SERVER_URL_RCIPE_DELETE, RecipeActivity.recipeId))
                                    .delete(fileUploadBody) //반드시 post로
                                    .build();
                            //동기 방식
                            response = toServer.newCall(request).execute();

                            flag = response.isSuccessful();
                            //응답 코드 200등등
                            int responseCode = response.code();
                            if (flag) {
                                e("레시피삭제response결과", response.message()); //읃답에 대한 메세지(OK)
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
                RecipeActivity.recipeActivity.finish();
            }
        });

        noBtn = (ImageView)findViewById(R.id.no_btn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}
