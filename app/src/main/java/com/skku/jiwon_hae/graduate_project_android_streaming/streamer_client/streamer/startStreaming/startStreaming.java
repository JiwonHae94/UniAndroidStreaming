package com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.streamer.startStreaming;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiwon_hae on 2017. 10. 11..
 */

public class startStreaming extends StringRequest {
    final static private String URL = "http://13.125.170.236/streaming_application/redis/streaming/streamer_go_onLive.php";
    private Map<String, String> paramters;

    public startStreaming(String userName, String stream_title, String tags, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        paramters = new HashMap<>();
        paramters.put("streamerId", userName);
        paramters.put("title", stream_title);
        paramters.put("tags", tags);
    }

    @Override
    public Map<String, String> getParams() {
        return paramters;
    }
}
