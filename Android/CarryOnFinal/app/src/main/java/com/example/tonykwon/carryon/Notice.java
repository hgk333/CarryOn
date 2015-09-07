package com.example.tonykwon.carryon;

//수하물알림 클래스

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

public class Notice extends BlunoLibrary {

    //블루투스를 얻는다
    private BluetoothAdapter BTAdapter = BluetoothAdapter.getDefaultAdapter();
    //알림시 Asynctask를 실행하지 않기 위한 flag
    boolean IsTaskingflag = false;
    //소리 진동 설정 이미지
    ImageView ivSoundButton;
    //다이얼로그
    AlertDialog alert;
    //task 객
    BackgroundTask task;
    //수하물 찾기/중지를 택할 수 있도록 하는 flag
    boolean btnflag = false;
    //소리 관련 설정을 위한 flag
    boolean soundflag;
    String TAG;
    //뒤로가기 화살
    ImageView back;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice);
        onCreateProcess();
        serialBegin(115200); //아두이노랑 연결??



        //뒤로가기 버튼 이벤트
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Notice.this, MainMenu.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);

            }
        });

        //사운드 출력 오디오매니저 선언
        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

//
//        Toast.makeText(getApplicationContext(), "수하물알림 뷰", Toast.LENGTH_LONG).show();

        //경고 다이얼로그
        new AlertDialog.Builder(this).setMessage("거리와 장애물에 따라 신호가 달라질 수 있습니다").setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e(TAG, "확인 클릭");
                dialog.dismiss();
            }
        }).show();

        //기기 소리/진동 상태에 따라 초기 이미지 설정
        ivSoundButton = (ImageView) this.findViewById(R.id.find_sound);
        AudioManager checkAudio = (AudioManager) this.getSystemService(this.AUDIO_SERVICE);
        switch (checkAudio.getRingerMode()) {
            case AudioManager.RINGER_MODE_VIBRATE:
            case AudioManager.RINGER_MODE_SILENT:
                //진동 혹은 무음
                ivSoundButton.setImageResource(R.drawable.find_button_sound);
                soundflag = false;
                break;

            case AudioManager.RINGER_MODE_NORMAL:
                //소리

                ivSoundButton.setImageResource(R.drawable.find_button_vibration);
                soundflag = true;
                break;

        }

        //'수하물찾는중' 이미지뷰, 초기엔 보이지 않게 한 후, 수하물 찾기 클릭 시 보이도록
        final ImageView findingText = (ImageView) findViewById(R.id.finding_text);
        findingText.setVisibility(View.INVISIBLE);

        //'터치하여수하물찾기' 텍스트
        final ImageView findButton = (ImageView) findViewById(R.id.find_text_button);

        findButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                //처음 버튼을 누르는 경우
                if (!btnflag) {
                    // 새로운 Task 객체를 만들고 실행
                    task = new BackgroundTask();
                    task.execute(100);
                    btnflag = true;

                    //'터치하여수하물찾기' 텍스트를 '수하물찾기중지' 텍스트로 변경
                    findButton.setImageResource(R.drawable.find_text_find_stop);
                    //'수하물찾는중..' 텍스트를 보이게 함
                    findingText.setVisibility(View.VISIBLE);
                }
                // 중지하고자 하는 경우
                else if (btnflag) {

                    IsTaskingflag = false;
                    task.cancel(true);  //task 객체를 취소시킨다.
                    BTAdapter.cancelDiscovery(); //블루투스 디바이스 검색 중
                    btnflag = false;
                    //'수하물찾기중지' 텍스트를 '터치하여수하물찾기' 텍스트로 변경
                    findButton.setImageResource(R.drawable.find_text_find);
                    //'수하물찾는중..' 텍스트를 안보이게 함
                    findingText.setVisibility(View.INVISIBLE);
                }
            }
        });

        // 소리/진동 설정
        ivSoundButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //진동으로 변경하는 경우
                if (!soundflag) {
                    ivSoundButton.setImageResource(R.drawable.find_button_vibration);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    soundflag = true;
                }
                //사운드로 변경하는 경우
                else if (soundflag) {
                    ivSoundButton.setImageResource(R.drawable.find_button_sound);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    soundflag = false;
                }
            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

    //수하물 도착 알림
    private void displayAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("수하물이 도착하였습니다.").setCancelable(
                false).setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        dialog.dismiss();
                        IsTaskingflag = true;
                    }
                });
        alert = builder.create();
        alert.show();
        //AsyncTask 동작을 멈추게 함
        task.cancel(true);
        BTAdapter.cancelDiscovery();

    }

    private void Noti() {

        //알림 기능을 위한 매니저 선언
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        //알림 시 나는 사운드
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.URI_COLUMN_INDEX);

        //진동모드 일 경우
        if (!soundflag) {
            builder.setSmallIcon(R.drawable.icon)
                    .setContentTitle("CarryOn")
                    .setContentText("수하물이 도착하였습니다")
                    .setAutoCancel(true).setVibrate(new long[]{0, 2000});
        }
        //소리모드 일 경우
        else if (soundflag) {
            builder.setSmallIcon(R.drawable.icon)
                    .setContentTitle("CarryOn")
                    .setContentText("수하물이 도착하였습니다")
                    .setAutoCancel(true).setSound(alert);
        }

        //알림을 위한 PendingIntent
        Intent intent = new Intent(this, Notice.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pIntent);
        manager.notify(1, builder.build());

    }
    //블루투스 디바이스 검색 성공시 받을 리시
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //블루투스 신호세기 rssi
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                //블루투스 디바이스 이름
                String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);

                try {
                    if (name.equals("BlunoV1.8")) {
                        Log.d(TAG, "rssi : " + name);
//                        if(!IsTaskingflag && rssi > -100){
//                            circle.setImageResource(R.drawable.circle_black);
//                        }
                        if (!IsTaskingflag && rssi > -100) {
                            displayAlert();
                            Noti();
                            IsTaskingflag = true;
                            btnflag = true;
                            BTAdapter.cancelDiscovery();
                            unregisterReceiver(receiver);
                        }
                    }
                }
                catch (Exception ex) {

                    Log.e("Exception", "");

                }
            }
        }
    };

    @Override
    public void onConectionStateChange(connectionStateEnum theConnectionState) {
    }

    @Override
    public void onSerialReceived(String theString) {
    }



    // Task 객체를 정의
    class BackgroundTask extends AsyncTask<Integer, Integer, Integer> {

        //생명주기 첫번째
        protected void onPreExecute() {}


        protected Integer doInBackground(Integer... values) {
            while (isCancelled() == false) {

                //flag = true가 되면 빠져나온다
                boolean flag = false;

                //리시버 등
                registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
                BluetoothAdapter BTAdapter = BluetoothAdapter.getDefaultAdapter();

                if (flag) {
                    break;
                } else {
                }
                try {
                    //디바이스 검색
                    BTAdapter.startDiscovery();
                   // unregisterReceiver(receiver);
                    //쓰레드가 1초동안 슬립, 1초마다 실행
                    Thread.sleep(1000);

                } catch (Exception ex) {

                    Log.e("Exception", "");

                }

            }
            return 0;

        }

        protected void onProgressUpdate(Integer... values) {

        }


        protected void onPostExecute(Integer result) {


        }


        protected void onCancelled() {


        }

    } //BackgroundTask

    protected void onResume() {
        super.onResume();
        System.out.println("BlUNOActivity onResume");
        onResumeProcess();                                                        //onResume Process by BlunoLibrary
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        onActivityResultProcess(requestCode, resultCode, data);                    //onActivityResult Process by BlunoLibrary
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        onPauseProcess();                                                        //onPause Process by BlunoLibrary
    }

    protected void onStop() {
        super.onStop();
        onStopProcess();                                                        //onStop Process by BlunoLibrary
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onDestroyProcess();                                                        //onDestroy Process by BlunoLibrary
    }


}

