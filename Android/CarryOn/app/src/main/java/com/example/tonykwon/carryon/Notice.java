package com.example.tonykwon.carryon;

//수하물알림 클래스

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.google.android.gcm.GCMRegistrar;

public class Notice extends Activity {

    //무게측정 액티비티로 넘어갈 수 있게 하는 버튼
    TextView backToMain;
    //블루투스 등록
    private BluetoothAdapter BTAdapter = BluetoothAdapter.getDefaultAdapter();

    String TAG;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice);


        registerGcm();


        //사운드 출력 오디오매니저 선언
        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);


        Toast.makeText(getApplicationContext(), "수하물알림 뷰", Toast.LENGTH_LONG).show();


        //경고 다이얼로그
        new AlertDialog.Builder(this).setMessage("거리와 장애물에 따라 신호가 달라질 수 있습니다").setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                Log.e(TAG, "확인 클릭");

                dialog.dismiss();


            }
        }).show();


        //   RSSI 버튼 이벤트 : 블루투스 찾기
        Button button = (Button) findViewById(R.id.rssi);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //     BTAdapter.startDiscovery();


            }
        });

        //메인메뉴로 돌아가기
        backToMain = (TextView) findViewById(R.id.weight);
        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Notice.this, MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            }
        });


        //핸드폰 소리, 진동 설정 클릭이벤트
        final ToggleButton TB1 = (ToggleButton) this.findViewById(R.id.toggleButton1);
        TB1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (TB1.isChecked()) {
                    TB1.setTextColor(Color.BLACK);

                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    Log.d(TAG, "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

                } else {
                    TB1.setTextColor(Color.RED);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    Log.d(TAG, "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");


                }
            }
        });
        final ToggleButton TB2 = (ToggleButton) this.findViewById(R.id.toggleButton2);
        TB2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (TB2.isChecked()) {
                    TB2.setTextColor(Color.BLACK);

                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                } else {
                    TB2.setTextColor(Color.RED);

                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                }
            }
        });


    }


//    public void onDestroy(){
//        // 리시버 삭세를 하지 않으면 에러
//        Log.d("MpMainActivity", "Service Destroy");
//
//    }


    public void registerGcm() {

        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);

        final String regId = GCMRegistrar.getRegistrationId(this);


        if (regId.equals("")) {
            GCMRegistrar.register(this, "193784586866");

        } else {
            Log.e("id", regId);
        }




    }
//
//    public void getGCMRegId() {
//        String PROJECT_ID = "193784586866";
//
//        Log.d("HSE_LOG", "단말기 ID를 등록합니다");
//        GCMRegistrar.checkDevice(this);
//        GCMRegistrar.checkManifest(this);
//        final String MobileDeviceID = GCMRegistrar.getRegistrationId(this);
//        if ("".equals(MobileDeviceID)) {
//            GCMRegistrar.register(this, PROJECT_ID);
//        } else {
//            //이미 등록 ID를 구해왔음
//            Log.v("HSE_LOG", "Already REGISTERED");
//            GCMRegistrar.unregister(this);
//            GCMRegistrar.register(this, PROJECT_ID);
//        }
//    }

}
