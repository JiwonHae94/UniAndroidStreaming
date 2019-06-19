package com.skku.jiwon_hae.graduate_project_android_streaming.account;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.skku.jiwon_hae.graduate_project_android_streaming.R;
import com.skku.jiwon_hae.graduate_project_android_streaming.account.volley.create_acc_email_validate;
import com.skku.jiwon_hae.graduate_project_android_streaming.account.volley.create_acc_request;
import com.skku.jiwon_hae.graduate_project_android_streaming.account.volley.create_acc_username_validate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jiwon_hae on 2017. 9. 24..
 */

public class create_account_email extends AppCompatActivity {

    private EditText create_acc_password;
    private EditText create_acc_password_recheck;

    boolean readyToCheck = false;
    boolean readyToRecheck = false;

    private EditText create_acc_email_acc;
    private EditText create_acc_email_add;
    private EditText create_acc_userName;
    private Spinner create_acc_email_list;

    private Button next;

    boolean final_check_email = false;
    boolean final_check_username = false;
    boolean final_check_pw = false;
    boolean final_check_re_pw = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_email);

        create_acc_email_acc = (EditText)findViewById(R.id.create_acc_email);
        create_acc_email_add = (EditText)findViewById(R.id.create_acc_email_add);
        create_acc_email_list = (Spinner)findViewById(R.id.create_acc_spinner);

        create_acc_userName = (EditText)findViewById(R.id.create_acc_userName);

        create_acc_password = (EditText)findViewById(R.id.create_acc_password);
        create_acc_password_recheck = (EditText)findViewById(R.id.create_acc_recheckPW);

        setCreateAccEmailList(create_acc_email_list, create_acc_email_add, create_acc_email_acc);
        checkUsername(create_acc_userName);
        checkPassword(create_acc_password, create_acc_password_recheck);

        next = (Button)findViewById(R.id.create_acc_next);
        next.setClickable(false);

        if(final_check_email && final_check_pw && final_check_re_pw && final_check_username){
            next.setClickable(true);
        }else{
            next.setClickable(false);
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!final_check_email){
                    Toast.makeText(create_account_email.this, "이메일을 확인해주세요", Toast.LENGTH_SHORT).show();
                }else if(!final_check_username){
                    Toast.makeText(create_account_email.this, "유저이름을 확인해주세요", Toast.LENGTH_SHORT).show();
                }else if(!final_check_pw || !final_check_re_pw){
                    Toast.makeText(create_account_email.this, "비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show();

                }else{
                    Response.Listener<String> responseListener = new Response.Listener<String>(){
                        @Override
                        public void onResponse(String response) {
                            try{
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");

                                if(success){
                                    Intent to_main = new Intent(create_account_email.this, create_account_login.class);
                                    to_main.putExtra("intent_direction", "login");

                                    startActivity(to_main);

                                    Toast.makeText(create_account_email.this, "가입해주셔서 감사합니다", Toast.LENGTH_SHORT).show();

                                }else{
                                    Toast.makeText(create_account_email.this, "잠시후에 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                                }

                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    create_acc_request Validate = new create_acc_request(create_acc_email_acc.getText().toString()+"@"+create_acc_email_add.getText().toString(),create_acc_password_recheck.getText().toString(), create_acc_userName.getText().toString(), "email", responseListener);
                    RequestQueue queue = Volley.newRequestQueue(create_account_email.this);
                    queue.add(Validate);
                }


            }
        });
    }

    public void checkPassword(final EditText password, final EditText passwordCheck){
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(v.hasFocus()){ readyToCheck = true;}

                if(readyToCheck && !v.hasFocus()){
                    if(!password.getText().toString().equals("")){
                        if(checkPassword(password.getText().toString())){
                            final_check_pw = true;
                            password.setTextColor(Color.BLACK);
                        }else{
                            final_check_pw = false;
                            password.setTextColor(Color.RED);
                        }

                        if(!password.getText().toString().equals(passwordCheck) && !passwordCheck.getText().toString().equals("")){
                            passwordCheck.setTextColor(Color.RED);
                        }
                    }
                }
            }
        });

        if(final_check_pw){
            password.addTextChangedListener(new TextWatcher() {
                String pw_init = password.getText().toString();

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    final_check_re_pw = false;
                    passwordCheck.setTextColor(Color.RED);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(pw_init.equals(password.getText().toString())){
                        final_check_re_pw = true;
                        passwordCheck.setTextColor(Color.BLACK);
                    }else{
                        final_check_re_pw = false;
                        passwordCheck.setTextColor(Color.RED);
                    }
                }
            });
        }

        passwordCheck.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(v.hasFocus()){ readyToRecheck = true; }

                if(readyToRecheck && !v.hasFocus()){
                    if(!password.getText().toString().equals("") && !passwordCheck.getText().toString().equals("")){
                        if(passwordCheck.getText().toString().equals(password.getText().toString())){
                            if(final_check_pw = true){
                                final_check_re_pw = true;
                                passwordCheck.setTextColor(Color.BLACK);
                            }else{
                                passwordCheck.setTextColor(Color.RED);
                            }

                        }else{
                            final_check_re_pw = false;
                            passwordCheck.setTextColor(Color.RED);
                        }
                    }
                }
            }
        });
    }

    public void checkUsername(final EditText username){
        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!v.hasFocus()){
                    Response.Listener<String> responseListener = new Response.Listener<String>(){
                        @Override
                        public void onResponse(String response) {
                            try{
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");

                                if(success){
                                    username.setTextColor(Color.BLACK);
                                    final_check_username = true;
                                    Toast.makeText(create_account_email.this, "사용할 수 있는 유저이름입니다", Toast.LENGTH_SHORT).show();

                                }else{
                                    username.setTextColor(Color.RED);
                                    Toast.makeText(create_account_email.this, "이미 존재하는 유저이름입니다", Toast.LENGTH_SHORT).show();
                                }

                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    create_acc_username_validate Validate = new create_acc_username_validate(username.getText().toString(), responseListener);
                    RequestQueue queue = Volley.newRequestQueue(create_account_email.this);
                    queue.add(Validate);
                }
            }
        });
    }

    public void setCreateAccEmailList(Spinner emailList, final EditText emailWriter, final EditText email_acc){
        ArrayAdapter choose_email_adapter = ArrayAdapter.createFromResource(create_account_email.this, R.array.email_choice,R.layout.create_account_spinner);
        choose_email_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        create_acc_email_list.setAdapter(choose_email_adapter);

        create_acc_email_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    emailWriter.setText("");
                    emailWriter.setHint("직접입력");

                    emailWriter.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if(!v.hasFocus()){
                                if(!emailWriter.getText().toString().equals("")){
                                    if(!checkEmail(email_acc.getText().toString() +"@" + emailWriter.getText().toString())){
                                        emailWriter.setTextColor(Color.RED);
                                        Toast.makeText(create_account_email.this, "올바른 이메일 주소를 입력해주세요", Toast.LENGTH_SHORT).show();

                                    }else{
                                        emailWriter.setTextColor(Color.BLACK);

                                        Response.Listener<String> responseListener = new Response.Listener<String>(){
                                            @Override
                                            public void onResponse(String response) {
                                                try{
                                                    JSONObject jsonObject = new JSONObject(response);
                                                    boolean success = jsonObject.getBoolean("success");

                                                    if(success){
                                                        emailWriter.setTextColor(Color.BLACK);
                                                        final_check_email= true;

                                                        Toast.makeText(create_account_email.this, "사용하셔도 되는 이메일입니다", Toast.LENGTH_SHORT).show();
                                                    }else{
                                                        emailWriter.setTextColor(Color.RED);
                                                        Toast.makeText(create_account_email.this, "이미 사용중인 이메일입니다", Toast.LENGTH_SHORT).show();
                                                    }

                                                }catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        };

                                        create_acc_email_validate Validate = new create_acc_email_validate(email_acc.getText().toString() +"@" + emailWriter.getText().toString(), responseListener);
                                        RequestQueue queue = Volley.newRequestQueue(create_account_email.this);
                                        queue.add(Validate);
                                    }
                                }
                            }
                        }
                    });

                }else if(position==1){
                    emailWriter.setText("naver.com");
                }else if(position==2){
                    emailWriter.setText("daum.com");
                }else if(position==3){
                    emailWriter.setText("hanmail.net");
                }else if(position==4){
                    emailWriter.setText("gmail.com");
                }else if(position==5) {
                    emailWriter.setText("yahoo.com");
                }

                if(!emailWriter.getText().toString().equals("")){
                    if(!checkEmail(email_acc.getText().toString() +"@" + emailWriter.getText().toString())){
                        emailWriter.setTextColor(Color.RED);
                        Toast.makeText(create_account_email.this, "올바른 이메일 주소를 입력해주세요", Toast.LENGTH_LONG).show();

                    }else{
                        Response.Listener<String> responseListener = new Response.Listener<String>(){
                            @Override
                            public void onResponse(String response) {
                                try{
                                    JSONObject jsonObject = new JSONObject(response);
                                    Log.e("test", jsonObject.toString());
                                    boolean success = jsonObject.getBoolean("success");

                                    if(success){
                                        emailWriter.setTextColor(Color.BLACK);
                                        final_check_email= true;

                                        Toast.makeText(create_account_email.this, "사용하셔도 되는 이메일입니다", Toast.LENGTH_SHORT).show();
                                    }else{
                                        emailWriter.setTextColor(Color.RED);
                                        Toast.makeText(create_account_email.this, "이미 사용중인 이메일입니다", Toast.LENGTH_SHORT).show();
                                    }

                                }catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };

                        create_acc_email_validate Validate = new create_acc_email_validate(email_acc.getText().toString() +"@" + emailWriter.getText().toString(), responseListener);
                        RequestQueue queue = Volley.newRequestQueue(create_account_email.this);
                        queue.add(Validate);
                    }
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public boolean checkPassword(String pw){
        String regex = "([a-zA-Z0-9].*[!,@,#,$,%,^,&,*,?,_,~])|([!,@,#,$,%,^,&,*,?,_,~]).{8,20}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(pw);
        boolean pw_format_true = m.matches();
        return pw_format_true;
    }

    public boolean checkEmail(String email){
        String regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        boolean email_format_true = m.matches();
        return email_format_true;
    }
}
