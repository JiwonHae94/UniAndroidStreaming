package com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.polling;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiwon_hae on 2017. 10. 19..
 */

public class polling_register_volley extends StringRequest {
    final static private String URL = "http://13.125.170.236/streaming_application/redis/polling/streamer_establish_poll.php";
    private Map<String, String> paramters;

    public polling_register_volley(String username, String poll_title, String poll_options, Response.Listener<String> listener) {
        super(Request.Method.POST, URL, listener, null);
        paramters = new HashMap<>();
        paramters.put("streamer", username);
        paramters.put("poll_title", poll_title);
        paramters.put("poll_options", poll_options);
    }

    @Override
    public Map<String, String> getParams() {
        return paramters;
    }

}