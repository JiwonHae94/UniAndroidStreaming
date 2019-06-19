package com.skku.jiwon_hae.graduate_project_android_streaming.video_call.friends;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiwon_hae on 2017. 11. 28..
 */

public class get_all_users_friend extends StringRequest {
    final static private String URL = "http://ec2-13-125-170-236.ap-northeast-2.compute.amazonaws.com/streaming_application/account/get_all_friends.php";
    private Map<String, String> paramters;

    public get_all_users_friend(String userName, Response.Listener<String> listener) {
        super(Request.Method.POST, URL, listener, null);
        paramters = new HashMap<>();
        paramters.put("currentUser", userName);
    }

    @Override
    public Map<String, String> getParams() {
        return paramters;
    }
}
