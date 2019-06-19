package com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.streamer.endStreaming;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiwon_hae on 2018. 5. 10..
 */

public class endStreaming extends StringRequest{
    final static private String URL = "http://13.125.170.236/streaming_application/stream/streamer_go_offLine.php";

    private Map<String, String> paramters;

    public endStreaming(String userName, Response.Listener<String> listener) {
        super(Request.Method.POST, URL, listener, null);
        paramters = new HashMap<>();
        paramters.put("streamerId", userName);
    }

    @Override
    public Map<String, String> getParams() {
            return paramters;
        }

}
