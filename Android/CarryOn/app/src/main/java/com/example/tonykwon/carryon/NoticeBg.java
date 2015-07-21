package com.example.tonykwon.carryon;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;



/**
 * 서비스 순서
 * onCreate() → onStartCommand() → Service Running → onDestroy()
 */


public class NoticeBg  extends Service implements Runnable {

    static int rssi;


    private static final String TAG = "PersistentService";



    //사운드 출력 선언
    SoundPool mSoundPool;


    private BluetoothAdapter BTAdapter = BluetoothAdapter.getDefaultAdapter();

    private String text;

    //시작 ID
    private int mStartId;
    //서비스에 대한 스레드에 연결 된 핸들러
    private Handler mHandler;
    //서비스 동작여부 flag
    private boolean mRunning;
    //타이머 설정 3초
    private static final int TIMER_PERIOD = 3 * 1000;
    private static final int COUNT = 10;
    private int mCounter;

    // 서비스 종료시 재부팅 딜레이 시간, activity의 활성 시간을 벌어야 한다.

    private static final int REBOOT_DELAY_TIMER = 5 * 1000;


// 주기 시간

    private static final int LOCATION_UPDATE_DELAY = 3 * 1000;


    @Override
    public IBinder onBind(Intent intent) {

        Log.d("PersistentService", "onBind()");


        return null;
    }

//
//    @Override
//
//   public int onStartCommand(Intent intent, int flags, int startId) {
//
//
//
//    }

//// 서비스가 시작될 때 마다 음악 재생을 시작합니다.
//
////
////        Bundle bun = new Bundle();
////        bun.putString("notiMessage", text);
////
////        Intent popupIntent = new Intent(getApplicationContext(), AlarmAlertDialog.class);
////
////        popupIntent.putExtras(bun);
////        PendingIntent pie= PendingIntent.getActivity(getApplicationContext(), 0, popupIntent, PendingIntent.FLAG_ONE_SHOT);
////        try {
////            pie.send();
////        } catch (PendingIntent.CanceledException e) {
////            Log.e("Error", "");
////        }
////
////        return super.onStartCommand(intent,flags,startId);
//
//    }

    @Override

    public void onCreate(){
        Log.e("MyService", "Service Created");

        unregisterRestartAlarm(); //등록된 알람은 제거
        super.onCreate();
        mRunning = false;



      //  BTAdapter.startDiscovery();
      //  registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }


//    private final BroadcastReceiver receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            final AudioManager audioManager =  (AudioManager)getSystemService(Context.AUDIO_SERVICE);
//            String action = intent.getAction();
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
//
//
//
//
//                String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
//
//                //TextView rssi_msg = (TextView) findViewById(R.id.wwwwww);
//
//                //rssi_msg.setText(rssi_msg.getText() + name + " => " + rssi + "dBm\n");
//                Log.d(TAG, "LeScanCallback() - device=" + BTAdapter + " (" + BTAdapter.getName() + ")" + ", rssi=" + rssi);
//
//
//                //모드 별로 조건 나눈건데 여기선 무의미
//                switch(audioManager.getRingerMode()) {
//
//                    case AudioManager.RINGER_MODE_VIBRATE:
//                        // 진동 모드
//
//                        if(rssi > -80 ) {
//                            Toast.makeText(getApplicationContext(), "지금거리는요만큼111", Toast.LENGTH_SHORT).show();
//                        }
//                        break;
//                    case AudioManager.RINGER_MODE_NORMAL:
//
//                        // 소리 모드
//                        if(rssi > -80 ) {
//                            Toast.makeText(getApplicationContext(), "지금거리는요만큼222", Toast.LENGTH_SHORT).show();
//                            mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
//                            int soundId = mSoundPool.load(NoticeBg.this, R.raw.soundthe, 1);
//                            mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
//                                @Override
//                                public void onLoadComplete(SoundPool soundPool, int soundId, int status) {
//                                    soundPool.play(soundId, 100, 100, 1, 0, 1.0f);
//                                }
//                            });
//
//
//                            Bundle bun = new Bundle();
//                            bun.putString("notiMessage", text);
//
//                            Intent popupIntent = new Intent(getApplicationContext(), NoticeAlertDialog.class);
//
//                            popupIntent.putExtras(bun);
//                            PendingIntent pie= PendingIntent.getActivity(getApplicationContext(), 0, popupIntent, PendingIntent.FLAG_ONE_SHOT);
//                            try {
//                                pie.send();
//                            } catch (PendingIntent.CanceledException e) {
//                                Log.e("Error","Error");
//                            }
//                        }
//                        break;
//
//                    case AudioManager.RINGER_MODE_SILENT:
//
//                        // 무음 모드
//                        if(rssi > -80 ) {
//                            Toast.makeText(getApplicationContext(), "지금거리는요만큼333", Toast.LENGTH_SHORT).show();
//                        }
//                        break;
//
//
//                }
//
//                //소리나는거 주석처리 해둠
////                if(rssi > -80 ){
////                    Toast.makeText(getApplicationContext(), "지금거리는요만큼", Toast.LENGTH_SHORT).show();
//////
//////                    mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
//////                   int soundId =  mSoundPool.load(Alarm.this, R.raw.soundthe, 1);
//////                    mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
//////                        @Override
//////                        public void onLoadComplete(SoundPool soundPool, int soundId, int status) {
//////                            soundPool.play(soundId,100,100,1,0,1.0f);
////                        }
////                    });
//            }
//            //    }
//        }
//    };



    @Override
    public void onStart(Intent intent, int startId){
        Log.e("MyService", "Service startId = " + startId);
        super.onStart(intent, startId);
        mStartId = startId;
        mCounter = COUNT;



        mHandler = new Handler();
        mHandler.postDelayed(this, LOCATION_UPDATE_DELAY);
        mRunning = true;
        //동작중이아니면 run 메소드를 일정 시간 후에 시작
//        if(!mRunning){
//            mHandler.postDelayed(new Runnable(){
//                public void run(){
//                    if(mRunning){
//                        //서비스 종료 요청이 들어온 경우 그냥 종료
//                        Log.e("MyService", "run after destroy");
//                        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//                        vibe.vibrate(500);
//
//                        Toast.makeText(getApplicationContext(), "지금거리는요만큼111", Toast.LENGTH_SHORT).show();
//
//                        return;
//                    }
//                    else if(--mCounter<=10){  //지정 횟수 실행시 종료
//                        Log.e("MyService", "stop Service id ="+ mStartId);
//                        stopSelf(mStartId);
//                    }
//                    else{
//                        //다음 작업 요구
//                        Log.e("MyService", "mCounter"+mCounter);
//                        mHandler.postDelayed(this, TIMER_PERIOD);
//                    }
//                }
//
//            }, TIMER_PERIOD);
//            mRunning = true;
//        }
    }

    @Override
    public void run(){
        Log.e(TAG, "run()");
        if(!mRunning) {
            Log.d("PersistentService", "run(), mIsRunning is false");

            Log.d("PersistentService", "run(), alarm service end");


            Toast.makeText(getApplicationContext(), "지금거리는요만큼55555", Toast.LENGTH_SHORT).show();
//            Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//                      vibe.vibrate(500);

            return;
        }
            else {



                Log.d("PersistentService", "run(), mIsRunning is true");

                Log.d("PersistentService", "run(), alarm repeat after five minutes");


            Toast.makeText(getApplicationContext(), "지금거리는요만큼66666", Toast.LENGTH_SHORT).show();

               Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibe.vibrate(500);

                mHandler.postDelayed(this, LOCATION_UPDATE_DELAY);

                mRunning = true;

            }


        }


    @Override

    public void onDestroy(){

        Log.d(TAG, "Service Destroy");
                registerRestartAlarm(); // 서비스가 죽을 때 알람 등록
        mRunning = false;
        super.onDestroy();
    }

    void registerRestartAlarm(){
        Log.d(TAG, "registerRestartAlarm");
        Intent intent = new Intent(NoticeBg.this, RestartService.class);
        intent.setAction(RestartService.ACTION_RESTART_PERSISTENTSERVICE);
        PendingIntent sender = PendingIntent.getBroadcast(NoticeBg.this, 0, intent,0); // 브로드캐스트할 인텐트
        long firstTime = SystemClock.elapsedRealtime(); //현재 시각
        firstTime +=  REBOOT_DELAY_TIMER; //10초 후 알람 이벤트 발생

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE); // 알람 서비스 등록
         am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, REBOOT_DELAY_TIMER, sender); //알람 반복복
    }

    void unregisterRestartAlarm(){
        Log.d(TAG, "unregisterRestartAlarm");
        Intent intent = new Intent(NoticeBg.this, RestartService.class);
        intent.setAction(RestartService.ACTION_RESTART_PERSISTENTSERVICE);
        PendingIntent sender = PendingIntent.getBroadcast(NoticeBg.this, 0, intent,0); // 브로드캐스트할 인텐트
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE); // 알람 서비스 등록
        am.cancel(sender);
    }



}
