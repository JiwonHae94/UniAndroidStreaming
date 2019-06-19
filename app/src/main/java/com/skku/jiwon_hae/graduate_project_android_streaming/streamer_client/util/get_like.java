package com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.util;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiwon_hae on 2017. 11. 29..
 */

public class get_like extends StringRequest{
    final static private String URL = "http://13.125.170.236/streaming_application/get_like.php";
    private Map<String, String> paramters;

    public get_like(String streamerName, Response.Listener<String> listener) {
        super(Request.Method.POST, URL, listener, null);
        paramters = new HashMap<>();
        paramters.put("streamerId", streamerName);
    }

    @Override
    public Map<String, String> getParams() {
        return paramters;
    }
}
