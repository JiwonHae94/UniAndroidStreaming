package com.skku.jiwon_hae.graduate_project_android_streaming.video_call.friends;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiwon_hae on 2017. 11. 27..
 */

public class findFriends extends StringRequest{
    final static private String URL = "http://ec2-13-125-170-236.ap-northeast-2.compute.amazonaws.com/streaming_application/account/find_friends.php";
    private Map<String, String> paramters;

    public findFriends(String currentUserEmail, String currentUserName, String searchQuery, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        paramters = new HashMap<>();
        paramters.put("searchQuery", searchQuery);
        paramters.put("currentUserName", currentUserName);
        paramters.put("currentUserEmail", currentUserEmail);
    }

    @Override
    public Map<String, String> getParams() {
        return paramters;
    }
}
