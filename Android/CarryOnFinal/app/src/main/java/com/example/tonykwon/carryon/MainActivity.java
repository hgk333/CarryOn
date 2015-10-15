package com.example.tonykwon.carryon;

//무게측정 액티비티

//무게측정 액티비티

import android.app.AlertDialog;
import android.app.ListActivity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
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
import android.widget.ImageButton;
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
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Vector;
import org.w3c.dom.Text;
import static java.lang.Integer.parseInt;
import java.text.DecimalFormat;

public class MainActivity extends BlunoLibrary {

    //기본 무게 설정
    double weight = 0;
    //항공사 규정 이미지
    ImageView buttonAirport;

    //우철이 담당 시작
    String AIRPORT;
    String FINISH;
    String caseNumber = "ONE";

    Vector<String> vc_airport = new Vector();
    Vector<String> vc_finish = new Vector();
    //우철이 담당 끝

    //접속할주소
    private final String urlPath = "http://ec2-52-69-218-238.ap-northeast-1.compute.amazonaws.com/php_mysql_connect.php";

    //보여줄 무게
    private TextView resultData;
    //무게측정
    ImageView scaleText;
    //기준무게 설정 이미지뷰
    private ImageView buttonStandard;
    private TextView tvStandardWeight;
        //카카오링크 관련 변수 3개
    private KakaoLink mKakaoLink;
    private KakaoTalkLinkMessageBuilder mKakaoTalkLinkMessageBuilder;
    private ImageView kakaoBtn;

    //기준 무게
    double MAXIMUMWEIGHT = 0;
    ImageView back;
    ImageView circle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //디바이스 관련 메소
        onCreateProcess();                                                        //onCreate Process by BlunoLibrary



        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainMenu.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            }
        });
        circle = (ImageView)findViewById(R.id.find_circle);


        //카카오링크 관련 초기화 및 연결, 메시지보내기 함수
        InitKakaoLink();
        kakaoBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SendKakaoMessage();
            }
        });


        // 밑에 나머진 형한테 받은 그대로...
        serialBegin(115200);                                                    //set the Uart Baudrate on BLE chip to 115200

//        buttonScan = (Button) findViewById(R.id.buttonScan);                    //initial the button for scanning the BLE device
//        buttonScan.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//
//                buttonScanOnClickProcess();                                     //Alert Dialog for selecting the BLE device
//
//            }
//        });
        scaleText = (ImageView) findViewById(R.id.scale_text);
        scaleText.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                buttonScanOnClickProcess();
            }
        });

        tvStandardWeight = (TextView)findViewById(R.id.tvStandardWeight);
        tvStandardWeight.setText(MAXIMUMWEIGHT+"Kg");
        buttonStandard= (ImageView)findViewById(R.id.btStandardWeight);
        buttonStandard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder setStandardWeight = new AlertDialog.Builder(MainActivity.this);
                setStandardWeight.setTitle("기준무게 수동설정");
                setStandardWeight.setMessage("숫자만 입력 가능합니다.");

                final EditText inputWeight = new EditText(MainActivity.this);
                setStandardWeight.setView(inputWeight);
                inputWeight.setInputType(InputType.TYPE_CLASS_NUMBER);

                setStandardWeight.setPositiveButton("입력", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inputValue = inputWeight.getText().toString();
                        if(inputValue.equals("")){
                            inputValue = "0";
                        }
                        MAXIMUMWEIGHT = Double.parseDouble(inputValue);

                        tvStandardWeight.setVisibility(View.VISIBLE);
                        tvStandardWeight.setText(MAXIMUMWEIGHT+"");
                        buttonStandard.setImageResource(R.drawable.scale_standard_button_after);

                    }
                });
                setStandardWeight.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                setStandardWeight.show();
            }
        });
//
//        //자동연결 쓰레드
//        Thread autoScanThread = new Thread(){
//            @Override
//            public void run(){
//                super.run();
//                autoScan();
//            }
//
//        };
//
//        autoScanThread.start();


        buttonAirport = (ImageView) findViewById(R.id.btAirport);
        buttonAirport.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                try {
                    String result = new HttpTask().execute().get();
                    SetResultFromDB(result);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                alertBuilder.setTitle("항공사를 선택하세요.");

                // List Adapter 생성
                final ArrayAdapter<String> adapter =
                        new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_singlechoice);

                for (int i = 0; i < vc_airport.size(); i++) {
                    adapter.add(vc_airport.get(i));
                }

                // 버튼 생성
                alertBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                // Adapter 셋팅
                alertBuilder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // AlertDialog 안에 있는 AlertDialog
                        AIRPORT = adapter.getItem(id);
                        caseNumber = "TWO";

                        try {
                            String result = new HttpTask().execute().get();
                            SetResultFromDB(result);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        AlertDialog.Builder alertBuilder2 = new AlertDialog.Builder(MainActivity.this);
                        alertBuilder2.setTitle("도착지를 선택하세요.");

                        // List Adapter 생성
                        final ArrayAdapter<String> adapter2 =
                                new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_singlechoice);

                        for (int i = 0; i < vc_finish.size(); i++) {
                            adapter2.add(vc_finish.get(i));
                        }

                        // 버튼 생성
                        alertBuilder2.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        // Adapter 셋팅
                        alertBuilder2.setAdapter(adapter2, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                FINISH = adapter2.getItem(id);
                                caseNumber = "THREE";

                                try {
                                    String result = new HttpTask().execute().get();
                                    SetResultFromDB(result);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                caseNumber = "ONE";
                            }
                        });
                        alertBuilder2.show();
                    }
                });
                alertBuilder.show();
            }
        });

        resultData = (TextView) findViewById(R.id.result_Data);
        resultData.setText("0.0Kg");
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

    //상태에 따른 버튼 메시지
    @Override
    public void onConectionStateChange(connectionStateEnum theConnectionState) {//Once connection state changes, this function will be called
        switch (theConnectionState) {                                            //Four connection state
            case isConnected:

                scaleText.setImageResource(R.drawable.scale_scale_text);
                Log.d("State : isConnected","");
                break;
            case isConnecting:
                scaleText.setImageResource(R.drawable.scale_connet_text);
                Log.d("State : isConnecting","");
                break;
            case isToScan:

                scaleText.setImageResource(R.drawable.scale_connet_text);
                Log.d("State : isToScan","");
                break;
            case isScanning:

                scaleText.setImageResource(R.drawable.scale_connet_text);
                Log.d("State : isScanning","");
                break;
            case isDisconnecting:

                scaleText.setImageResource(R.drawable.scale_connet_text);
                Log.d("State : isDisconnecting","");
                break;
            default:
                break;
        }
    }


    @Override
    public void onSerialReceived(String theString) {                            //Once connection data received, this function will be called
//        // TODO Auto-generated method stub
//        serialReceivedText.append(theString);                     //append the text into the EditText
//        //The Serial data from the BLUNO may be sub-packaged, so using a buffer to hold the String is a good choice.
        Log.d("STRING_VALUE : ", ""+theString);
        String stringWeight;



        //serialReceivedText.append(theString);

        int checkString = theString.indexOf('=');

        stringWeight=theString.substring(checkString + 1);
        weight= Double.parseDouble(stringWeight);

        scaleText.setVisibility(View.GONE);
        resultData.setVisibility(View.VISIBLE);
        resultData.setText(weight+"Kg");


        if(MAXIMUMWEIGHT == 0) {
            serialSend("103\0");

            circle.setImageResource(R.drawable.scale_circle);
        }
        else if (weight > MAXIMUMWEIGHT) {
            serialSend("100\0"); //red

            circle.setImageResource(R.drawable.scale_circle_excess);

        }
        else if (weight > MAXIMUMWEIGHT - 3) {
            serialSend("101\0"); //yellow

            circle.setImageResource(R.drawable.scale_circle_danger);
        }
        else {
            serialSend("102\0"); // green

            circle.setImageResource(R.drawable.scale_circle_under);
        }
    }

    //카톡링크 초기화
    private void InitKakaoLink() {
        try {
            mKakaoLink = KakaoLink.getKakaoLink(this);
            mKakaoTalkLinkMessageBuilder = mKakaoLink.createKakaoTalkLinkMessageBuilder();
            kakaoBtn = (ImageView) findViewById(R.id.zero2);

        } catch (KakaoParameterException e) {
            e.printStackTrace();
        }
    }

    //카톡링크 메시지보내기
    private void SendKakaoMessage() {
        try {
            double subWeight;
            String pattern = "##.#";
            DecimalFormat dformat = new DecimalFormat(pattern);

            if(weight>MAXIMUMWEIGHT){
                subWeight = weight-MAXIMUMWEIGHT;
                mKakaoTalkLinkMessageBuilder.addText("무게 '"+ dformat.format(subWeight) + "kg'이 초과되었어요. 도와주세요!!");
            }
            else{
                subWeight = MAXIMUMWEIGHT - weight;
                mKakaoTalkLinkMessageBuilder.addText("무게 '"+ dformat.format(subWeight) + "kg'이 남아 여유가 있어요.");
            }

            //mKakaoTalkLinkMessageBuilder.addImage("http://dn.api1.kage.kakao.co.kr/14/dn/btqaWmFftyx/tBbQPH764Maw2R6IBhXd6K/o.jpg", 128, 128);
            //mKakaoTalkLinkMessageBuilder.addAppButton("캐리온으로 이동", new AppActionBuilder().setUrl("http://www.naver.com").build());
            mKakaoLink.sendMessage(mKakaoTalkLinkMessageBuilder.build(), this);
        } catch (KakaoParameterException e) {
            e.printStackTrace();
        }
    }





    // 서버의 디비와 통신.
    class HttpTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            try{
                HttpPost request = new HttpPost(urlPath);
                Vector<NameValuePair> nameValue = new Vector<NameValuePair>();

                switch(caseNumber) {
                    //전달할 인자들
                    case "ONE":
                        nameValue.add(new BasicNameValuePair("caseNumber", caseNumber));

                        break;
                    case "TWO":
                        nameValue.add(new BasicNameValuePair("caseNumber", caseNumber));

                        nameValue.add(new BasicNameValuePair("airport_name", AIRPORT));
                        nameValue.add(new BasicNameValuePair("start", "kim-po"));

                        break;
                    case "THREE":
                        nameValue.add(new BasicNameValuePair("caseNumber", caseNumber));

                        nameValue.add(new BasicNameValuePair("airport_name", AIRPORT));
                        nameValue.add(new BasicNameValuePair("start", "kim-po"));
                        nameValue.add(new BasicNameValuePair("finish", FINISH));

                        break;
                }

                //웹 접속 - utf-8 방식으로
                HttpEntity enty = new UrlEncodedFormEntity(nameValue, "UTF-8");
                request.setEntity(enty);

                HttpClient client = new DefaultHttpClient();
                HttpResponse res = client.execute(request);

                //웹 서버에서 값받기
                HttpEntity entityResponse = res.getEntity();
                InputStream im = entityResponse.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(im, "UTF-8"));

                String total = "";
                String tmp = "";

                //버퍼에있는거 전부 더해주기
                //readLine -> 파일내용을 줄 단위로 읽기
                while ((tmp = reader.readLine()) != null) {
                    if (tmp != null) {
                        total += tmp;
                    }
                }

                im.close();

                return total;
            }catch(UnsupportedEncodingException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }
            //오류시 null 반환
            return null;
        }

        //asyncTask 3번째 인자와 일치 매개변수값 -> doInBackground 리턴값이 전달됨
        //AsynoTask 는 preExcute - doInBackground - postExecute 순으로 자동으로 실행됩니다.
        //ui는 여기서 변경
        protected void onPostExecute(String value){
            super.onPostExecute(value);
        }
    }

    void SetResultFromDB(String result){
        try{
            JSONArray jArray = new JSONArray(result);

            switch(caseNumber) {
                case "ONE":
                    String[] jsonName1 = {"AirportName"};

                    JSONObject json1 = null;
                    vc_airport.clear();

                    for(int i=0; i<jArray.length(); i++){
                        json1 = jArray.getJSONObject(i);
                        if(json1 != null){
                            for(int j=0; j< jsonName1.length; j++){
                                vc_airport.add(json1.getString(jsonName1[j]));
                            }
                        }
                    }
                    break;
                case "TWO":
                    String[] jsonName2 = {"Finish"};

                    JSONObject json2 = null;
                    vc_finish.clear();

                    for(int i=0; i<jArray.length(); i++){
                        json2 = jArray.getJSONObject(i);
                        if(json2 != null){
                            for(int j=0; j< jsonName2.length; j++){
                                vc_finish.add(json2.getString(jsonName2[j]));
                            }
                        }
                    }

                    break;
                case "THREE":
                    String[] jsonName3 = {"Weight"};

                    JSONObject json3 = null;

                    for(int i=0; i<jArray.length(); i++){
                        json3 = jArray.getJSONObject(i);
                        if(json3 != null){
                            String weightFormDB;
                            weightFormDB = json3.getString(jsonName3[0]);
                            Log.i("WEIGHT", weightFormDB);
                            MAXIMUMWEIGHT = Double.parseDouble(weightFormDB);

                            tvStandardWeight.setVisibility(View.VISIBLE);
                            tvStandardWeight.setText(MAXIMUMWEIGHT+"");

                            buttonStandard.setImageResource(R.drawable.scale_standard_button_after);
                        }
                    }

                    break;
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }





}


