package com.skku.jiwon_hae.graduate_project_android_streaming.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import static com.skku.jiwon_hae.graduate_project_android_streaming.video_call.service.video_call_service.mPubNub;

/**
 * Created by jiwon_hae on 2017. 11. 27..
 */

public class useSharedPreference {
    private Context mContext;

    public useSharedPreference(Context context){
        this.mContext = context;
    }

    // 값 저장하기
    public void save_login_information(String userId , String userName) throws JSONException {
        SharedPreferences pref = mContext.getSharedPreferences("streaming_app", mContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user_id", userId);
        jsonObject.put("userName", userName);
        editor.putString("userInformation", jsonObject.toString());
        editor.commit();
    }

    // 값(Key Data) 삭제하기
    public void logout_from_app(){
        SharedPreferences pref = mContext.getSharedPreferences("streaming_app", mContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("userInformation");
        editor.commit();

        if(mPubNub != null){
            mPubNub.unsubscribeAll();
        }
    }

    // 값 불러오기
    public String get_user_information(){
        SharedPreferences pref = mContext.getSharedPreferences("streaming_app", mContext.MODE_PRIVATE);
        return pref.getString("userInformation", "");
    }

}
