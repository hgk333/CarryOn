package com.example.tonykwon.carryon;

//GCM�� �̿��ϱ� ���� Ŭ����

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


        //�˸��� �� �� ������
        int icon = R.drawable.ic_launcher;

        //���� �ð��� ���� �˸�
        long when = System.currentTimeMillis();




        //�˸� �Ŵ��� ���
        NotificationManager notificationManager = (NotificationManager) context

                .getSystemService(Context.NOTIFICATION_SERVICE);


        //�˸� ����
        Notification notification = new Notification(icon, message, when);


        //�˸� �� ����
        String title = context.getString(R.string.app_name);


        //�˸� Ŭ�� �� � ��Ƽ��Ƽ�� �Ѿ����, ���⼭�� ���Ϲ� �˸� Ŭ������ �Ѿ���� ��
        Intent notificationIntent = new Intent(context, Notice.class);


        //��Ƽ��Ƽ ������ ����, �̱� ž�� ��Ƽ��Ƽ ��Ȱ��, Ŭ����ž�� ��ġ�ϰ����ϴ� ��Ƽ��Ƽ��
        //�̹� ���û� �����ϸ�, �ش� ��Ƽ��Ƽ �� �ٸ� ��Ƽ��Ƽ���� ��� ���������
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP

                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        //�е����ٵ�� Ư�� ������Ʈ�� ����Ʈ�� ������ ��, �ش� ����Ʈ�� �ٷ� ����ϴ� ���
        //�ٸ� ������Ʈ�� �ش� ����Ʈ�� ��� �� �� �ֵ��� �� �� ���.
        //�ٿ�Ƽ��Ƽ�� ��������μ� �е�����Ʈ�� �����.
        PendingIntent intent = PendingIntent.getActivity(context, 0,

                notificationIntent, 0);







        notification.setLatestEventInfo(context, title, message, intent);



        notification.flags |= Notification.FLAG_AUTO_CANCEL;



        notificationManager.notify(0, notification);



    }



    @Override
    protected void onError(Context arg0, String arg1) {

    }


    //�޽����� �ٷ�� �κ�
    @Override
    protected void onMessage(Context context, Intent intent) {



        String msg = intent.getStringExtra("msg");

        Log.e("getmessage", "getmessage:" + msg);

        generateNotification(context,msg);



    }






    //regId ����ֱ�
    @Override
    protected void onRegistered(Context context, String reg_id) {

        Log.e("Key is : ", reg_id);

        Toast.makeText(getApplicationContext(), reg_id, Toast.LENGTH_LONG);
    }


    @Override
    protected void onUnregistered(Context arg0, String arg1) {

        Log.e("Ű�� �����մϴ�.","���ŵǾ����ϴ�.");

    }



}