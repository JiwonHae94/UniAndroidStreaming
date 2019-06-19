package com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.polling;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiwon_hae on 2017. 10. 20..
 */

public class polling_decision extends StringRequest{
    final static private String URL = "http://ec2-13-125-170-236.ap-northeast-2.compute.amazonaws.com/streaming_application/redis/polling/poll_decision.php";
    private Map<String, String> paramters;

    public polling_decision(String streamerName, String poll_options, Response.Listener<String> listener) {
        super(Request.Method.POST, URL, listener, null);
        paramters = new HashMap<>();
        paramters.put("streamer", streamerName);
        paramters.put("selection", poll_options);
    }

    @Override
    public Map<String, String> getParams() {
        return paramters;
    }
}
