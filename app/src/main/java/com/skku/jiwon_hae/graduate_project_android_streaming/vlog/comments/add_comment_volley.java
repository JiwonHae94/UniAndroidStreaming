package com.skku.jiwon_hae.graduate_project_android_streaming.vlog.comments;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiwon_hae on 2017. 11. 14..
 */
public class add_comment_volley extends StringRequest {
    final static private String URL = "http://13.125.170.236/streaming_application/vlog/comment/vlog_comment_register.php";
    private Map<String, String> paramters;

    public add_comment_volley(String vlog_registration, String commenter, String comments, String timeStamp, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        paramters = new HashMap<>();
        paramters.put("vlog_registration", vlog_registration);
        paramters.put("commenter", commenter);
        paramters.put("comment", comments);
        paramters.put("timeStamp", timeStamp);
    }

    @Override
    public Map<String, String> getParams() {
        return paramters;
    }
}
