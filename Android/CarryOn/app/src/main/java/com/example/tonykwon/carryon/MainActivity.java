package com.example.tonykwon.carryon;


import android.app.AlertDialog;
import android.app.ListActivity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.kakao.kakaolink.AppActionBuilder;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;


import static java.lang.Integer.parseInt;


public class MainActivity extends BlunoLibrary {
    private Button buttonScan;
    private Button buttonSerialSend;
    private EditText serialSendText;
    private TextView serialReceivedText;

    private KakaoLink mKakaoLink;
    private KakaoTalkLinkMessageBuilder mKakaoTalkLinkMessageBuilder;
    private Button kakaoBtn;


    Button shareBtn;

    private Context mContext;

    private List<String> mDataList, mPics;

    private final int resId = 0;


    final int MAXIMUMWEIGHT = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onCreateProcess();                                                        //onCreate Process by BlunoLibrary


        //카카오링크 관련 초기화 및 연결, 메시지보내기 함수
        InitKakaoLink();
        kakaoBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SendKakaoMessage();
            }
        });



        // 밑에 나머진 형한테 받은 그대로...
        serialBegin(115200);                                                    //set the Uart Baudrate on BLE chip to 115200


        serialReceivedText = (TextView) findViewById(R.id.serialReveicedText);    //initial the EditText of the received data
        serialSendText = (EditText) findViewById(R.id.serialSendText);            //initial the EditText of the sending data

        buttonSerialSend = (Button) findViewById(R.id.buttonSerialSend);        //initial the button for sending the data
        buttonSerialSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                serialSend(serialSendText.getText().toString());                //send the data to the BLUNO
            }
        });

        buttonScan = (Button) findViewById(R.id.buttonScan);                    //initial the button for scanning the BLE device
        buttonScan.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                buttonScanOnClickProcess();                                        //Alert Dialog for selecting the BLE device
            }
        });

        Thread autoScanThread = new Thread(){
            @Override
            public void run(){
                super.run();
                autoScan();
            }

        };

        autoScanThread.start();



    }



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

    @Override
    public void onConectionStateChange(connectionStateEnum theConnectionState) {//Once connection state changes, this function will be called
        switch (theConnectionState) {                                            //Four connection state
            case isConnected:
                buttonScan.setText("Connected");
                break;
            case isConnecting:
                buttonScan.setText("Connecting");
                break;
            case isToScan:
                buttonScan.setText("Scan");

                break;
            case isScanning:
                buttonScan.setText("Scanning");
                break;
            case isDisconnecting:
                buttonScan.setText("isDisconnecting");

                break;
            default:
                break;
        }
    }

    @Override
    public void onSerialReceived(String theString) {                            //Once connection data received, this function will be called
//        // TODO Auto-generated method stub
//        serialReceivedText.append(theString);							//append the text into the EditText
//        //The Serial data from the BLUNO may be sub-packaged, so using a buffer to hold the String is a good choice.

        // TODO Auto-generated method stub
        //serialReceivedText.append(theString);							//append the text into the EditText
        //The Serial data from the BLUNO may be sub-packaged, so using a buffer to hold the String is a good choice.

        String StrWeight, IntWeight;
        int temp = 0;
        serialReceivedText.append(theString);


        char[] temp1 = theString.toCharArray();

        Log.e("asdasd", ""+theString);

        //weight = 15 에서 토큰으로 15로 잘라서 얻기 위해
        temp = ((temp1[8] - '0') * 10) + ((temp1[9] - '0'));


        Log.e("asdasd", ""+temp);

        if (temp > MAXIMUMWEIGHT)
            serialSend("100");
        else if (temp > MAXIMUMWEIGHT - 3)
            serialSend("101");
        else
            serialSend("102");
    }


    //카카오톡
    private void InitKakaoLink() {
        try {
            mKakaoLink = KakaoLink.getKakaoLink(this);
            mKakaoTalkLinkMessageBuilder = mKakaoLink.createKakaoTalkLinkMessageBuilder();
            kakaoBtn = (Button) findViewById(R.id.zero2);

        } catch (KakaoParameterException e) {
            e.printStackTrace();
        }
    }

    //카카오톡
    private void SendKakaoMessage() {
        try {
            mKakaoTalkLinkMessageBuilder.addText("신우철의 카카오링크");
            mKakaoTalkLinkMessageBuilder.addImage("http://dn.api1.kage.kakao.co.kr/14/dn/btqaWmFftyx/tBbQPH764Maw2R6IBhXd6K/o.jpg", 128, 128);
            mKakaoTalkLinkMessageBuilder.addAppButton("캐리온으로 이동", new AppActionBuilder().setUrl("http://www.naver.com").build());
            mKakaoLink.sendMessage(mKakaoTalkLinkMessageBuilder.build(), this);
        } catch (KakaoParameterException e) {
            e.printStackTrace();
        }
    }


}

