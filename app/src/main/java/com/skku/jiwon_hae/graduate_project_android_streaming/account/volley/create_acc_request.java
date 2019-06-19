package com.skku.jiwon_hae.graduate_project_android_streaming.account.volley;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiwon_hae on 2017. 9. 24..
 */

public class create_acc_request extends StringRequest {
    final static private String URL = "http://ec2-13-125-170-236.ap-northeast-2.compute.amazonaws.com/streaming_application/account/create_account.php";
    private Map<String, String> paramters;

    public create_acc_request(String email, String password, String userName, String type, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        paramters = new HashMap<>();
        paramters.put("user_email", email);
        paramters.put("user_password", password);
        paramters.put("user_name", userName);
        paramters.put("type", type);
    }

    @Override
    public Map<String, String> getParams() {
        return paramters;
    }
}
