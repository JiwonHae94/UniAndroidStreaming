package com.skku.jiwon_hae.graduate_project_android_streaming.video_call.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.skku.jiwon_hae.graduate_project_android_streaming.utils.useSharedPreference;
import com.skku.jiwon_hae.graduate_project_android_streaming.video_call.constants;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jiwon_hae on 2017. 11. 23..
 */

public class video_call_service extends Service {
    private String stdByChannel;
    public static Pubnub mPubNub;

    //UserInformation
    private useSharedPreference useDB;
    private String userInformation;
    private String user_email;
    private String userName;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        useDB = new useSharedPreference(this);
        userInformation = useDB.get_user_information();

        try {
            JSONObject jsonObject = new JSONObject(userInformation);
            userName = jsonObject.get("userName").toString();
            user_email = jsonObject.get("user_id").toString();

            this.stdByChannel = this.userName;
            initPubNub();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return START_STICKY;
    }

    public void initPubNub(){
        mPubNub  = new Pubnub(constants.PUB_KEY, constants.SUB_KEY);
        mPubNub.setUUID(userName);
        subscribeStdBy();
    }

    private void subscribeStdBy(){
        try {
            mPubNub.subscribe(this.stdByChannel, new Callback() {
                @Override
                public void successCallback(String channel, Object message) {

                    if (!(message instanceof JSONObject)) return; // Ignore if not JSONObject
                    JSONObject jsonMsg = (JSONObject) message;

                    try {
                        if (!jsonMsg.has(constants.JSON_CALL_USER))return; //Ignore Signaling messages.

                        String user = jsonMsg.getString(constants.JSON_CALL_USER);
                        dispatchIncomingCall(user);

                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void connectCallback(String channel, Object message) {
                    Log.e("MA-iPN", "CONNECTED: " + message.toString());
                    setUserStatus(constants.STATUS_AVAILABLE);
                }

                @Override
                public void errorCallback(String channel, PubnubError error) {
                    Log.e("MA-iPN","ERROR: " + error.toString());
                }
            });
        } catch (PubnubException e){
            e.printStackTrace();
        }
    }

    private void dispatchIncomingCall(String userId){
        Log.e("Incoming call from: ", userId);
        Intent receiving_call = new Intent("streaming_application.video_call.service.receiving_call");
        receiving_call.putExtra(constants.CALL_USER, userId);
        sendBroadcast(receiving_call);
    }

    private void setUserStatus(String status){
        try {
            JSONObject state = new JSONObject();
            state.put(constants.JSON_STATUS, status);

            mPubNub.setState(stdByChannel, this.userName, state, new Callback() {
                @Override
                public void successCallback(String channel, Object message) {
                    Log.e("MA-sUS","State Set: " + message.toString());
                }
            });
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
}