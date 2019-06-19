package com.skku.jiwon_hae.graduate_project_android_streaming.video_call.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.skku.jiwon_hae.graduate_project_android_streaming.video_call.constants;
import com.skku.jiwon_hae.graduate_project_android_streaming.video_call.video_call_incoming_call;

//import com.example.jiwon_hae.streaming_application.video_call.video_call_con;

/**
 * Created by jiwon_hae on 2017. 11. 23..
 */

public class video_call_receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(action.equals("streaming_application.video_call.service.receiving_call")){
            Intent to_calling = new Intent(context ,video_call_incoming_call.class);
            to_calling.putExtra(constants.CALL_USER, intent.getStringExtra(constants.CALL_USER));
            to_calling.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(to_calling);
        }
    }
}
