package com.skku.jiwon_hae.graduate_project_android_streaming.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.skku.jiwon_hae.graduate_project_android_streaming.R;
import com.skku.jiwon_hae.graduate_project_android_streaming.account.volley.create_acc_login;
import com.skku.jiwon_hae.graduate_project_android_streaming.main;
import com.skku.jiwon_hae.graduate_project_android_streaming.utils.useSharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jiwon_hae on 2017. 9. 24..
 */

public class create_account_login extends AppCompatActivity {

    private EditText login_email;
    private EditText login_password;
    private Button login;
    private Button create_account;
    private useSharedPreference useDB = new useSharedPreference(this);
    private String userInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_acc_login);

        getSupportActionBar().hide();

        login_email = (EditText)findViewById(R.id.login_email);
        login_password = (EditText)findViewById(R.id.login_password);

        useDB = new useSharedPreference(this);
        userInformation = useDB.get_user_information();

        try {
            JSONObject jsonObject = new JSONObject(userInformation);
            String userName = jsonObject.get("userName").toString();
            String user_email = jsonObject.get("user_id").toString();

            if(!userName.isEmpty() && !user_email.isEmpty()){
                Intent to_main = new Intent(create_account_login.this, main.class);
                startActivity(to_main);
                finish();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        login = (Button)findViewById(R.id.create_acc_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = login_email.getText().toString();
                String password = login_password.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("login");

                            if(!success.equals("login_failed") && (success.length() > 0)) {
                                Intent to_main = new Intent(create_account_login.this, main.class);
                                to_main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                Toast.makeText(create_account_login.this, "로그인 성공", Toast.LENGTH_SHORT).show();

                                useDB.save_login_information(email , success);
                                startActivity(to_main);

                            }else{
                                login_password.setText("");
                                Toast.makeText(create_account_login.this, "아이디와 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show();
                            }

                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                create_acc_login Validate = new create_acc_login(email, password, responseListener);
                RequestQueue queue = Volley.newRequestQueue(create_account_login.this);
                queue.add(Validate);
            }
        });

        create_account= (Button)findViewById(R.id.create_account);
        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(create_account_login.this, create_account.class));
            }
        });
    }


}