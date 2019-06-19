package com.skku.jiwon_hae.graduate_project_android_streaming.account.volley;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiwon_hae on 2017. 9. 24..
 */

public class create_acc_username_validate extends StringRequest{
    final static private String URL = "http://13.125.170.236/streaming_application/account/validate_username.php";
    private Map<String, String> paramters;

    public create_acc_username_validate(String username, Response.Listener<String> listener) {
        super(Request.Method.POST, URL, listener, null);
        paramters = new HashMap<>();
        paramters.put("userName", username);
    }

    @Override
    public Map<String, String> getParams() {
        return paramters;
    }
}
