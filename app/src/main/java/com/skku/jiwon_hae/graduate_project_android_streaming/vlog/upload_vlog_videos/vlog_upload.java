package com.skku.jiwon_hae.graduate_project_android_streaming.vlog.upload_vlog_videos;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiwon_hae on 2017. 11. 10..
 */

public class vlog_upload extends StringRequest {
    final static private String URL = "http://13.125.170.236/streaming_application/vlog/register/vlog_register.php";

    private Map<String, String> paramters;

    public vlog_upload(String registeration_no, String userName, String vlog, String tags, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        paramters = new HashMap<>();
        paramters.put("registration_no", registeration_no);
        paramters.put("userName", userName);
        paramters.put("vlog", vlog);
        paramters.put("tags", tags);
    }

    @Override
    public Map<String, String> getParams() {
        return paramters;
    }

}