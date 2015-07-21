package com.example.tonykwon.carryon;


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

    Button BTconnect;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private static final int REQUEST_ENABLE_BT = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.btconnect);


        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "기기가 블루투스 지원 안함", Toast.LENGTH_SHORT).show();
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        if (mBluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BTConnect.this, MainMenu.class);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        }

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data){

        switch(requestCode){
            case REQUEST_ENABLE_BT:
                if(resultCode == Activity.RESULT_OK){
                    Intent intent = new Intent(BTConnect.this, MainMenu.class);
                    startActivity(intent);
                    finish();
                }
                else{
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
