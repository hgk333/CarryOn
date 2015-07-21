package com.example.tonykwon.carryon;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainMenu extends FragmentActivity {

    TextView toWeight;
    TextView toNotice;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu);



        Toast.makeText(getApplicationContext(), "메인메뉴 뷰", Toast.LENGTH_LONG).show();

        toWeight = (TextView) findViewById(R.id.weight);
        toWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenu.this, MainActivity.class);
                startActivity(intent);

                overridePendingTransition(0, 0);
            }
        });

        toNotice = (TextView) findViewById(R.id.alarm);
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
