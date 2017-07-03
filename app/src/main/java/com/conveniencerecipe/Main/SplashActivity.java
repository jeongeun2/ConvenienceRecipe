package com.conveniencerecipe;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.util.Log.e;

/**
 * Created by ccei 34 on 2016-07-28.
 */
public class SplashActivity extends Activity {
    Handler hd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        PropertyManager propertyManager = PropertyManager.getInstance();
        final String userId = propertyManager.getUUID();

        hd = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(!TextUtils.isEmpty(userId) && userId.length() > 10){
                    new LoginAsynck().execute(userId);
                }else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        hd.sendEmptyMessageDelayed(0, 1000);
    }
    class LoginAsynck extends AsyncTask<String,Void,Boolean>{
        @Override
        protected Boolean doInBackground(String... strings) {
            Response loginresponse = null;
            boolean flagConn = false;
            boolean flagJson = false;
            String userId = strings[0];
            try {
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();
                RequestBody loginBody = new FormBody.Builder()
                        .add("deviceId",userId ) //기본 쿼리
                        .build();
                Request request = new Request.Builder()
                        .url(NetworkDefineConstant.SERVER_URL_LOGIN)
                        .post(loginBody) //반드시 post로
                        .build();

                loginresponse = toServer.newCall(request).execute();

                flagConn = loginresponse.isSuccessful();

                if (flagConn) {
                    JSONObject jsonObject = new JSONObject(loginresponse.body().string());
                    String resultMessage = jsonObject.getString("msg");
                    if(resultMessage.equalsIgnoreCase("login success")){
                        flagJson = true;
                    }else{
                        flagJson = false;
                    }
                }
                return flagJson;
            } catch (Exception e) {
                e("LoginAsynck", e.toString(),e);
            } finally {
                if (loginresponse != null) {
                    loginresponse.close();
                }
            }
            return flagJson;
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean){
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }
    }
}