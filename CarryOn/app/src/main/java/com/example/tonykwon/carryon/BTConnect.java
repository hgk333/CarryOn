package com.example.tonykwon.carryon;

//가장 처음 나오는 화면

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BTConnect extends FragmentActivity {

    //블루투스 연결버튼
    Button BTconnect;
    //블루투스 어뎁터
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    //블루투스 요청상수
    private static final int REQUEST_ENABLE_BT = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.btconnect);


        //블루투스가 등록이 안되있을 시
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "기기가 블루투스 지원 안함", Toast.LENGTH_SHORT).show();
        }

        //블루투스가 꺼져있는 상태, 켜기 요청
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        //블루투스 켜져있는 상태
        if (mBluetoothAdapter.isEnabled()) {
            //메인액티비티로 넘어간다
            Intent intent = new Intent(BTConnect.this, MainMenu.class);
            startActivity(intent);
            finish();
            //애니메이션효과 없이 넘어가기기            overridePendingTransition(0, 0);
        }

    }

    //블루투스 켜기 요청
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        switch(requestCode){
            case REQUEST_ENABLE_BT:
                if(resultCode == Activity.RESULT_OK){
                    Intent intent = new Intent(BTConnect.this, MainMenu.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    //블루투스 연결하기
                    Toast.makeText(getApplicationContext(), "블루투스를 연결해야만 합니다", Toast.LENGTH_LONG).show();

                    BTconnect= (Button) findViewById(R.id.btn_connect);
                    BTconnect.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);


                            if(mBluetoothAdapter.isEnabled()){
                                Intent intent = new Intent(BTConnect.this, MainMenu.class);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(0, 0);
                            }

                        }
                    });

                }
        }

    }
}
