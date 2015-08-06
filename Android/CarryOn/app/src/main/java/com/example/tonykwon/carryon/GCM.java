package com.example.tonykwon.carryon;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class GCM extends AsyncTask {
    SharedPreferences pref_regId;
    Context mContext;
    DefaultHttpClient client;
    String PROJECT_ID = "193784586866";
    String GCM_REGISTER_URL = "http://www.test.com/gcm_register_regid.php";
    String TAG = "GCM";

    public GCM(Context context) {
        mContext = context;
    }



    protected Void doInBackground(Void... params) {
        GCMRegistrar.checkDevice(mContext);
        GCMRegistrar.checkManifest(mContext);

        // sharedPreferences에서 regId 가져오기
        pref_regId = mContext.getSharedPreferences("regId", Activity.MODE_PRIVATE);
        String regId = pref_regId.getString("regId", "");

        // sharedPreferences에 regId가 없으면
        if(regId.equals("")){
            // 등록된 regId 가져오기
            regId = GCMRegistrar.getRegistrationId(mContext);

            // 기기 등록이 안되어 있으면
            if(regId.equals("")){
                // 기기 등록  */
                GCMRegistrar.register(mContext, PROJECT_ID);
            }else{
                // android_id 가져오기 (기기마다 Unique한 값)
                String android_id = "" + android.provider.Settings.Secure.getString(mContext.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

                // regid를  DB에 저장
                String result = this.registerDB(android_id, regId);
                Log.e(TAG, result);
                int succ = new Integer(result);

                // db에 regId 넣는게 성공하면 sharedpreferences에 저장
                if(succ > 0){
                    // sharedPreferences에 저장
                    SharedPreferences.Editor editor_regId = pref_regId.edit();
                    editor_regId.putString("regId", regId);
                    editor_regId.commit();
                }
            }
        }

        return null;
    }

    // DB에 regId 저정하기
    private String registerDB(String android_id, String regId){
        List nameValuePairs = new ArrayList(2);
        nameValuePairs.add(new BasicNameValuePair("android_id", android_id));
        nameValuePairs.add(new BasicNameValuePair("regId", regId));
        String result = "";
        try {
            result = goHttpPost(GCM_REGISTER_URL, nameValuePairs);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return (result.equals("") || result == null) ? "-3" : result; // httppost result가 아무것도 없으면 -3 에러.
    }

    // http post 전송
    private String goHttpPost(String host, List nameValuePairs) throws ClientProtocolException, IOException {
        String result = null;

        client = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(host);

        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        /* 헤더정보 셋팅 => 해당 앱에서 넘어온 데이터만 체크하기 위해서 헤더정보 체크를 서버에서 해줄겁니다. */
        client.addRequestInterceptor(new HttpRequestInterceptor() {
            @Override
            public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
                // TODO Auto-generated method stub
                request.addHeader("from", "hhh");
                request.setHeader("User-Agent", "kkk");

            }
        });
        ResponseHandler responseHandler = new BasicResponseHandler();
        result = (String) client.execute(httppost, responseHandler);

        return result;
    }


    @Override
    protected Object doInBackground(Object[] params) {
        return null;
    }
}
