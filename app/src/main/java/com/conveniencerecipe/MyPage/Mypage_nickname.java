package com.conveniencerecipe;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.util.Log.e;

/**
 * Created by ccei 34 on 2016-08-08.
 */
public class Mypage_nickname extends FontActivity{
    EditText nicknameEdit;
    TextView nicknameBtn;
    String nickname = null;
    boolean flag;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_nickname);

        nicknameEdit = (EditText)findViewById(R.id.nickname_edit);
        nicknameBtn = (TextView)findViewById(R.id.nickname_btn);
        nicknameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nickname = nicknameEdit.getText().toString();
                new AsyncNicknameJSON().execute();
            }
        });
    }

    public class AsyncNicknameJSON extends AsyncTask<String, Integer, String> {
        CustomProgressDialog dialog = new CustomProgressDialog(Mypage_nickname.this);
        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            try {
                //OKHttp3사용
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .url(String.format(NetworkDefineConstant.SERVER_URL_NICKNAME_DUPLICATED, nickname))
                        .build();
                //동기 방식
                response = toServer.newCall(request).execute();
                boolean flag = response.isSuccessful();

                ResponseBody responseBody = response.body();
                String retruedJson = responseBody.string();

                //응답 코드 200등등
                int responseCode = response.code();
                if (flag) {
                    return ParseDataParseHandler.getJSONNickname(new StringBuilder(retruedJson));
                }
            } catch (UnknownHostException une) {
                e("aaa", une.toString());
            } catch (UnsupportedEncodingException uee) {
                e("bbb", uee.toString());
            } catch (Exception e) {
                e("ccc", e.toString());
                e.printStackTrace();
            } finally {
                if (response != null) {
                    response.close();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            if(s.equals("Duplicated")) {
                Toast.makeText(Mypage_nickname.this, "이미 사용중인 닉네임입니다.", Toast.LENGTH_SHORT).show();
            } else {
                new Thread(new Runnable() {
                    public void run() {
                        Response response = null;
                        try {
                            //업로드는 타임 및 리드타임을 넉넉히 준다.
                            OkHttpClient toServer = new OkHttpClient.Builder()
                                    .connectTimeout(30, TimeUnit.SECONDS)
                                    .readTimeout(30, TimeUnit.SECONDS)
                                    .build();
                            String id = PropertyManager.getInstance().getId();
                            //요청 Body 세팅==> 그전 Query Parameter세팅과 같은 개념
                            RequestBody likeReqBody = new FormBody.Builder()
                                    .add("id", id)
                                    .add("nickname", nickname)
                                    .build();
                            //요청 세팅
                            Request request = new Request.Builder()
                                    .url(NetworkDefineConstant.SERVER_URL_NICKNAME)
                                    .post(likeReqBody) //반드시 post로
                                    .build();
                            //동기 방식
                            response = toServer.newCall(request).execute();

                            flag = response.isSuccessful();
                            //응답 코드 200등등
                            int responseCode = response.code();
                            if (flag) {
                                e("닉네임변경response결과", response.message()); //읃답에 대한 메세지(OK)
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
                MainActivity.navprofilenickname.setText(nickname);
                MypageActivity.navprofilenickname.setText(nickname);
                finish();
            }
        }
    }
}
