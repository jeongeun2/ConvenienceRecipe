package com.conveniencerecipe;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import java.net.URLDecoder;
import java.util.Map;


/**
 * Created by ccei 34 on 2016-08-20.
 */
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{
    private static final String TAG = "FirebaseMsgService";
    String from_userId,nickname,sort,sort_id,preNotice;
    String noticemessage="",message1,message2,message3,message4,message5;
    PendingIntent pendingIntent;

    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        //실제 푸쉬로 넘어온 데이터
        Map<String, String> receiveData = remoteMessage.getData();

        preNotice = PropertyManager.getInstance().getPUSH();

        from_userId = receiveData.get("data1");
        nickname = receiveData.get("data2");
        sort = receiveData.get("data3");
        sort_id = receiveData.get("data4");

        Log.e("푸쉬테스트",preNotice);
        Log.e("from_userId",from_userId);
        Log.e("nickname",nickname);
        Log.e("sort",sort);
        Log.e("sort_id",sort_id);


        if(receiveData.get("data3").equals("1")){
            noticemessage = receiveData.get("data2") + "님이 회원님의 레시피에 댓글을 남겼습니다.";
            message1 = noticemessage;
        }else if(receiveData.get("data3").equals("2")){
            noticemessage = receiveData.get("data2") + "님이 회원님의 레시피를 좋아합니다.";
            message2 = noticemessage;
        }else if(receiveData.get("data3").equals("3")){
            noticemessage = receiveData.get("data2") + "님이 회원님의 게시물에 댓글을 남겼습니다.";
            message3 = noticemessage;
        }else if(receiveData.get("data3").equals("4")){
            noticemessage = receiveData.get("data2") + "님이 회원님을 팔로우 했습니다.";
            message4 = noticemessage;
        }else if(receiveData.get("data3").equals("5")){
            noticemessage = receiveData.get("data2") + "님이 레시피를 스크랩했습니다.";
            message5 = noticemessage;
        }

        Log.e("message",noticemessage);

        try {
            //한글은 반드시 디코딩 해준다.
            if(preNotice.equals("Y")) {
                sendPushNotification(URLDecoder.decode(noticemessage, "UTF-8"));
            }

        } catch (Exception e) {

        }

    }



    private void sendPushNotification(String message) {
        System.out.println("received message : " + message);

        if(message.equals(message1)){
            Intent intent = new Intent(this, RecipeCommentActivity.class);
            intent.putExtra("recipeID",sort_id);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        }else if(message.equals(message2)){
            Intent intent = new Intent(this, RecipeActivity.class);
            intent.putExtra("recipeID",sort_id);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        }else if(message.equals(message3)){
            Intent intent = new Intent(this, QnaDetailActivity.class);
            intent.putExtra("postingId",sort_id);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        }else if(message.equals(message4)){
            Intent intent = new Intent(this, UserPageActivity.class);
            intent.putExtra("userId",from_userId);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        }else if(message.equals(message5)){
            Intent intent = new Intent(this, RecipeActivity.class);
            intent.putExtra("recipeID",sort_id);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        }



        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.pushsmall).setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                .setContentTitle("편의점을 부탁해")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri).setLights(000000255,500,2000)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakelock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        wakelock.acquire(5000);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}