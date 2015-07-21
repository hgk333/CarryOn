package com.example.tonykwon.carryon;


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


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;


public class Notice extends Activity {


    TextView backToMain;
    private BluetoothAdapter BTAdapter = BluetoothAdapter.getDefaultAdapter();

    String TAG;

    BroadcastReceiver receiver;

    Intent intentMyService;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private Button mRegistrationButton;
    private ProgressBar mRegistrationProgressBar;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView mInformationTextView;


    /**
     * Instance ID를 이용하여 디바이스 토큰을 가져오는 RegistrationIntentService를 실행한다.
     */
    public void getInstanceIdToken() {
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    /**
     * LocalBroadcast 리시버를 정의한다. 토큰을 획득하기 위한 READY, GENERATING, COMPLETE 액션에 따라 UI에 변화를 준다.
     */
    public void registBroadcastReceiver(){
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();


                if(action.equals(QuickstartPreferences.REGISTRATION_READY)){
                    // 액션이 READY일 경우
                    mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                    mInformationTextView.setVisibility(View.GONE);
                } else if(action.equals(QuickstartPreferences.REGISTRATION_GENERATING)){
                    // 액션이 GENERATING일 경우
                    mRegistrationProgressBar.setVisibility(ProgressBar.VISIBLE);
                    mInformationTextView.setVisibility(View.VISIBLE);
                    mInformationTextView.setText(getString(R.string.registering_message_generating));
                } else if(action.equals(QuickstartPreferences.REGISTRATION_COMPLETE)){
                    // 액션이 COMPLETE일 경우
                    mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                    mRegistrationButton.setText(getString(R.string.registering_message_complete));
                    mRegistrationButton.setEnabled(false);
                    String token = intent.getStringExtra("token");
                    mInformationTextView.setText(token);
                }

            }
        };
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice);


        registBroadcastReceiver();

        // 토큰을 보여줄 TextView를 정의
        mInformationTextView = (TextView) findViewById(R.id.informationTextView);
        mInformationTextView.setVisibility(View.GONE);
        // 토큰을 가져오는 동안 인디케이터를 보여줄 ProgressBar를 정의
        mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
        mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
        // 토큰을 가져오는 Button을 정의
        mRegistrationButton = (Button) findViewById(R.id.registrationButton);
        mRegistrationButton.setOnClickListener(new View.OnClickListener() {
            /**
             * 버튼을 클릭하면 토큰을 가져오는 getInstanceIdToken() 메소드를 실행한다.
             * @param view
             */
            @Override
            public void onClick(View view) {
                getInstanceIdToken();
            }
        });



        // immortal service 등록

        intentMyService = new Intent(this, NoticeBg.class);



        // 리시버 등록

        receiver = new RestartService();
        try

        {

            // xml에서 정의해도 됨?

            // 이것이 정확히 무슨 기능을 하지는지?

            IntentFilter mainFilter = new IntentFilter("com.example.tonykwon.carryon");



//            Intent Intent = new Intent(Notice.this, NoticeBg.class);
//
//            startService(Intent);
//
//            Log.e(TAG, "서비스실행됬다");
//            // 리시버 저장

            registerReceiver(receiver, mainFilter);



            // 서비스 시작

            startService(intentMyService);



        } catch (Exception e) {



            Log.d("MpMainActivity", e.getMessage()+"");

            e.printStackTrace();

        }



        //사운드 출력 오디오매니저 선언
        final AudioManager audioManager =  (AudioManager)getSystemService(Context.AUDIO_SERVICE);


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


        // registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

    }


    /**
     * 앱이 실행되어 화면에 나타날때 LocalBoardcastManager에 액션을 정의하여 등록한다.
     */
    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_READY));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_GENERATING));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));

    }

    /**
     * 앱이 화면에서 사라지면 등록된 LocalBoardcast를 모두 삭제한다.
     */
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }


    /**
     * Google Play Service를 사용할 수 있는 환경이지를 체크한다.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onDestroy(){
        // 리시버 삭세를 하지 않으면 에러

        Log.d("MpMainActivity", "Service Destroy");

        unregisterReceiver(receiver);




    }

}
