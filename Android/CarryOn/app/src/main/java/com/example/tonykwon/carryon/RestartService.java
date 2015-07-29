package com.example.tonykwon.carryon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by free on 2015-07-18.
 */
public class RestartService extends BroadcastReceiver{

    public static final String ACTION_RESTART_PERSISTENTSERVICE

            = "ACTION.Restart.NoticeBg";



    @Override
    public void onReceive(Context context, Intent intent){
        Log.d("ImmortalService", "RestartService called");
        if(intent.getAction().equals(ACTION_RESTART_PERSISTENTSERVICE)){
            Intent i = new Intent(context, NoticeBg.class);
            context.startService(i);
        }

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {



            Log.d("RestartService", "ACTION_BOOT_COMPLETED");



            Intent i = new Intent(context, NoticeBg.class);

            context.startService(i);

        }


    }

}
