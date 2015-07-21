package com.example.tonykwon.carryon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;


public class NoticeAlertDialog extends Activity{

    private String notiMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Bundle bun = getIntent().getExtras();
        notiMessage = bun.getString("notiMessage");


        setContentView(R.layout.noticealertdialog);

        TextView adMessage = (TextView)findViewById(R.id.message);
        adMessage.setText(notiMessage);

        Button adButton = (Button)findViewById(R.id.submit);

        adButton.setOnClickListener(new SubmitOnClickListener());

    }
    private class SubmitOnClickListener implements OnClickListener {

        public void onClick(View v) {
            finish();

        }
    }


}



