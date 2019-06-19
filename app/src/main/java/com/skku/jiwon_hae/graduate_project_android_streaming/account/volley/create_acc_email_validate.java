package com.skku.jiwon_hae.graduate_project_android_streaming.account.volley;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiwon_hae on 2017. 9. 24..
 */

public class create_acc_email_validate extends StringRequest {
    final static private String URL = "http://ec2-13-125-170-236.ap-northeast-2.compute.amazonaws.com/streaming_application/account/validate_email.php";
    private Map<String, String> paramters;

    public create_acc_email_validate(String email, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        paramters = new HashMap<>();
        paramters.put("user_email", email);
    }

    @Override
    public Map<String, String> getParams() {
        return paramters;
    }

}
