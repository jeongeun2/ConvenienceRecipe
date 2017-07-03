package com.conveniencerecipe;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.util.Log.e;

/**
 * Created by ccei 34 on 2016-08-12.
 */
public class LoginActivity extends AppCompatActivity {
    String resultMessage;
    EditText nicknamesetting;
    String nickname;
    ImageView login;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

       /* FirebaseMessaging.getInstance().subscribeToTopic("news");
        FirebaseInstanceId.getInstance().getToken();*/

        login = (ImageView) findViewById(R.id.start_login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nickname = nicknamesetting.getText().toString().trim();
                new AsyncNicknameJSON().execute();
            }
        });

        login.setOnTouchListener(new View.OnTouchListener() { //버튼 터치시 이벤트
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) // 버튼을 누르고 있을 때
                    login.setImageResource(R.drawable.bt_login_start);
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){ //버튼에서 손을 떼었을 때
                    login.setImageResource(R.drawable.bt_login_start_2);
                }
                return false;
            }
        });

        nicknamesetting = (EditText) findViewById(R.id.nicknamesetting);
        nicknamesetting.requestFocus();

        PropertyManager.getInstance().setPUSH("Y");
    }

    protected void onStop() {
        super.onStop();
    }

    class MemberInsert extends AsyncTask<String,Void,Boolean>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean){
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String Token=PropertyManager.getInstance().getToken();

            Response response = null;
            boolean isHttp = false;
            boolean result = false;
            String nickName = params[0];
            String uuid = params[1];
            try {
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();
                RequestBody signupBody = new FormBody.Builder()
                        .add("nickname", nickName)
                        .add("deviceId", uuid)
                        .add("token", Token)
                        .build();
                Request request = new Request.Builder()
                        .url(NetworkDefineConstant.SERVER_URL_SIGNUP)
                        .post(signupBody) //반드시 post로
                        .build();
                response = toServer.newCall(request).execute();


                Log.e("토큰",Token);

                isHttp = response.isSuccessful();

                if (isHttp) {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    resultMessage = jsonObject.getString("id");
                    if(jsonObject.getString("msg").equalsIgnoreCase("success")){
                        if(resultMessage !=null && resultMessage.length() > 1){
                            PropertyManager.getInstance().setId(resultMessage);
                        }
                    }
                    result = true;
                }else{
                    e("가입결과", response.message()); //읃답에 대한 메세지(OK)
                }
                return result;
            } catch (Exception e) {
                e("signup", e.toString(),e);
            } finally {
                if (response != null) {
                    response.close();
                }
            }
            return result;
        }
    }

    public class AsyncNicknameJSON extends AsyncTask<String, Integer, String> {
        CustomProgressDialog dialog = new CustomProgressDialog(LoginActivity.this);
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
                Toast.makeText(LoginActivity.this, "이미 사용중인 닉네임입니다.", Toast.LENGTH_SHORT).show();
            } else {
                if(nickname != null && nickname.length() > 1){
                    String uuid = UUID.randomUUID().toString().replace('-', 'a');
                    PropertyManager.getInstance().setUUID(uuid);
                    PropertyManager.getInstance().setUserNickName(nickname);
                    new MemberInsert().execute(nickname,uuid);
                }else if(nickname.length()>10){
                    Toast.makeText(getApplicationContext(), "10자 이하로 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}