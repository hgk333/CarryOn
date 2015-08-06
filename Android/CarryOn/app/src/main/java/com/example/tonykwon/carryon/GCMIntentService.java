package com.example.tonykwon.carryon;

//GCM을 이용하기 위한 클래스

import android.app.Notification;

import android.app.NotificationManager;

import android.app.PendingIntent;

import android.content.Context;

import android.content.Intent;

import android.util.Log;
import android.widget.Toast;


import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {



    private static void generateNotification(Context context, String message) {


        //알림이 올 때 아이콘
        int icon = R.drawable.ic_launcher;

        //현재 시간에 맞춰 알림
        long when = System.currentTimeMillis();




        //알림 매니저 등록
        NotificationManager notificationManager = (NotificationManager) context

                .getSystemService(Context.NOTIFICATION_SERVICE);


        //알림 선언
        Notification notification = new Notification(icon, message, when);


        //알림 시 제목
        String title = context.getString(R.string.app_name);


        //알림 클릭 시 어떤 액티비티로 넘어갈건지, 여기서는 수하물 알림 클래스로 넘어가도록 함
        Intent notificationIntent = new Intent(context, Notice.class);


        //액티비티 스택을 제어, 싱글 탑은 액티비티 재활용, 클리어탑은 런치하고자하는 액티비티가
        //이미 스택상에 존재하면, 해당 액티비티 위 다른 액티비티들을 모두 종료시켜줌
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP

                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        //패딩인텐드는 특정 컴포넌트가 인텐트를 생성한 후, 해당 인텐트를 바로 사용하는 대신
        //다른 컴포넌트가 해당 인텐트를 사용 할 수 있도록 할 때 사용.
        //겟엑티비티를 사용함으로서 패딩인텐트를 만든다.
        PendingIntent intent = PendingIntent.getActivity(context, 0,

                notificationIntent, 0);







        notification.setLatestEventInfo(context, title, message, intent);



        notification.flags |= Notification.FLAG_AUTO_CANCEL;



        notificationManager.notify(0, notification);



    }



    @Override
    protected void onError(Context arg0, String arg1) {

    }


    //메시지를 다루는 부분
    @Override
    protected void onMessage(Context context, Intent intent) {



      //  String msg = intent.getStringExtra("msg");

        String msg = "알림이 도착했습니다";
        Log.e("getmessage", "getmessage:" + msg);

        generateNotification(context,msg);



    }






    //regId 띄워주기
    @Override
    protected void onRegistered(Context context, String reg_id) {

        Log.e("Key is : ", reg_id);

        Toast.makeText(getApplicationContext(), reg_id, Toast.LENGTH_LONG);
    }


    @Override
    protected void onUnregistered(Context arg0, String arg1) {

        Log.e("키를 제거합니다.","제거되었습니다.");

    }



}