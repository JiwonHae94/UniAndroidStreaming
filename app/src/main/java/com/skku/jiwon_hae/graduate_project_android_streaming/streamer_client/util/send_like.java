package com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.util;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiwon_hae on 2017. 11. 29..
 */

public class send_like extends StringRequest{
    final static private String URL = "http://13.125.170.236/streaming_application/like.php";
    private Map<String, String> paramters;

    public send_like(String userName, String like, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        paramters = new HashMap<>();
        paramters.put("streamerId", userName);
        paramters.put("like", like);
    }

    @Override
    public Map<String, String> getParams() {
        return paramters;
    }
}
