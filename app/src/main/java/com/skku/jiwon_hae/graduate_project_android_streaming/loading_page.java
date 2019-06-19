package com.skku.jiwon_hae.graduate_project_android_streaming;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.skku.jiwon_hae.graduate_project_android_streaming.account.create_account;
import com.skku.jiwon_hae.graduate_project_android_streaming.account.create_account_login;
import com.skku.jiwon_hae.graduate_project_android_streaming.utils.useSharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

public class loading_page extends AppCompatActivity {


    private useSharedPreference useDB;
    private String userInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_page);
        getSupportActionBar().hide();

        useDB = new useSharedPreference(this);
        userInformation = useDB.get_user_information();

        if(!userInformation.isEmpty()){
            try {
                JSONObject jsonObject = new JSONObject(userInformation);

                if(jsonObject.has("userName")) {
                    Intent to_main = new Intent(this, main.class);
                    startActivity(to_main);

                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            Intent to_login = new Intent(this, create_account.class);
            startActivity(to_login);

            finish();
        }
    }
}
