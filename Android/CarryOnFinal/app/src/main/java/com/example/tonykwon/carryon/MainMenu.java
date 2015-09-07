package com.example.tonykwon.carryon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainMenu extends Activity {

    //메뉴 두가지
    ImageView toWeight;
    ImageView toNotice;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu);


        Toast.makeText(getApplicationContext(), "메인메뉴 뷰", Toast.LENGTH_LONG).show();

        //이미지뷰 클릭 이벤트
        toWeight = (ImageView) findViewById(R.id.ivWeight);
        toWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenu.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        toNotice = (ImageView) findViewById(R.id.ivAlarm);
        toNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenu.this, Notice.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });

    }


}
