package com.skku.jiwon_hae.graduate_project_android_streaming.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.skku.jiwon_hae.graduate_project_android_streaming.R;
import com.skku.jiwon_hae.graduate_project_android_streaming.account.volley.create_acc_request;
import com.skku.jiwon_hae.graduate_project_android_streaming.main;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jiwon_hae on 2017. 9. 24..
 */

public class create_account extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private TextView create_acc_register;
    private Button create_account_email;
    private com.google.android.gms.common.SignInButton create_account_gmail;

    public static GoogleApiClient mGoogleApiClient;

    private final int GOOGLE_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        create_account_email = (Button)findViewById(R.id.create_account_email);
        create_account_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent to_login = new Intent(create_account.this, create_account_login.class);
                startActivity(to_login);
            }
        });

        create_acc_register = (TextView)findViewById(R.id.create_acc_register);
        create_acc_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent acc_email = new Intent(create_account.this, create_account_email.class);
                startActivity(acc_email);
            }
        });

        create_account_gmail = (com.google.android.gms.common.SignInButton)findViewById(R.id.create_account_gmail);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        create_account_gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);

        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            final GoogleSignInAccount acct = result.getSignInAccount();

            Response.Listener<String> responseListener = new Response.Listener<String>(){
                @Override
                public void onResponse(String response) {
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");

                        Log.e("google", String.valueOf(success));

                        if(success){
                            Intent to_main = new Intent(create_account.this, main.class);
                            to_main.putExtra("intent_direction", "login");
                            to_main.putExtra("userName", acct.getDisplayName());

                            startActivity(to_main);

                            Toast.makeText(create_account.this, "가입해주셔서 감사합니다", Toast.LENGTH_SHORT).show();

                        }else{
                            Toast.makeText(create_account.this, "잠시후에 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                        }

                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            create_acc_request Validate = new create_acc_request(acct.getEmail(), acct.getEmail(), acct.getId(), "google", responseListener);

            RequestQueue queue = Volley.newRequestQueue(create_account.this);
            queue.add(Validate);

        } else if(!result.isSuccess()) {
            Toast.makeText(create_account.this, "취소하셨습니다", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("create_account", "googleOnConnectionFailed");
    }
}
