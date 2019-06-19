package com.skku.jiwon_hae.graduate_project_android_streaming;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.skku.jiwon_hae.graduate_project_android_streaming.account.account_info;
import com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.client.live_list;
import com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.streamer.streaming;
import com.skku.jiwon_hae.graduate_project_android_streaming.utils.useSharedPreference;
import com.skku.jiwon_hae.graduate_project_android_streaming.video_call.service.video_call_service;
import com.skku.jiwon_hae.graduate_project_android_streaming.video_call.video_call;
import com.skku.jiwon_hae.graduate_project_android_streaming.vlog.vlog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class main extends AppCompatActivity {

    private String intent_direction;

    //  ----------- Bottom Navigation elements ---------
    Fragment fragment;
    FragmentManager fragmentManager;
    FragmentTransaction transaction;

    ToggleButton nav_live;
    ImageButton nav_stream;
    ToggleButton nav_vlog;
    ToggleButton nav_account;
    ToggleButton nav_video_call;

    private static int REQUEST_PERMISSION = 9000;

    List<ToggleButton> buttomNavigationList = new ArrayList<ToggleButton>();

    //  ---------- CAMERA_PERMISSION ----------
    private int REQUEST_CODE_PERMISSION = 393939;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        request_permission();

//  ----------------- Bottom Navigation ----------------
        fragmentManager = getSupportFragmentManager();

        nav_live = (ToggleButton)findViewById(R.id.nav_live);
        nav_stream = (ImageButton)findViewById(R.id.nav_stream);
        nav_vlog = (ToggleButton)findViewById(R.id.nav_vlog);
        nav_account = (ToggleButton)findViewById(R.id.nav_account);
        nav_video_call = (ToggleButton)findViewById(R.id.nav_video_call);

        buttomNavigationList.add(nav_live);
        buttomNavigationList.add(nav_vlog);
        buttomNavigationList.add(nav_account);
        buttomNavigationList.add(nav_video_call);

        setButtonNavigationClickListener();
        buttonNavigationTransition();

        if(getIntent().hasExtra("intent_direction")){
            intent_direction = getIntent().getStringExtra("intent_direction");
            change_according_to_intend(intent_direction);
        }

        nav_stream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i =0; i< buttomNavigationList.size() ;i++){
                    buttomNavigationList.get(i).setChecked(false);
                }

                Intent to_streaming = new Intent(main.this, streaming.class);
                startActivity(to_streaming);
            }
        });
    }

    private useSharedPreference useDB;
    private String userInformation;

    @Override
    protected void onStart() {
        useDB = new useSharedPreference(this);
        userInformation = useDB.get_user_information();

        try {
            JSONObject jsonObject = new JSONObject(userInformation);
            Intent start_video_call_service = new Intent(this, video_call_service.class);
            start_video_call_service.putExtra("userName", jsonObject.get("userName").toString());
            startService(start_video_call_service);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        super.onStart();
    }



    //  ----------------- Bottom Navigation ----------------
    private void buttomNavigationManager(String s){
        for(int i = 0 ; i < buttomNavigationList.size() ; i++){
            if(s.equals(buttomNavigationList.get(i).getTag())){
                buttomNavigationList.get(i).setChecked(true);
                buttomNavigationList.get(i).setClickable(false);
            }else{
                buttomNavigationList.get(i).setChecked(false);
                buttomNavigationList.get(i).setClickable(true);
            }
        }
    }

    private void buttonNavigationTransition(){
        for(int i = 0 ; i < buttomNavigationList.size() ; i++){
            if(buttomNavigationList.get(i).isChecked()) {
                switch(buttomNavigationList.get(i).getTag().toString()){
                    case "nav_live":
                        fragment = new live_list();
                        break;

                    case "nav_account":
                        fragment = new account_info();
                        break;

                    case "nav_vlog":
                        fragment = new vlog();
                        break;

                    case "nav_video_call":
                        fragment = new video_call();
                        break;
                }
            }
        }
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_container, fragment).commit();

        View view = this.getCurrentFocus();

        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void setButtonNavigationClickListener(){
        for(int i =0; i < buttomNavigationList.size(); i++){
            final int finalI = i;
            buttomNavigationList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttomNavigationManager(buttomNavigationList.get(finalI).getTag().toString());
                    buttonNavigationTransition();
                }
            });
        }
    }

    private void change_according_to_intend(String intent_direction){
        switch (intent_direction){
            case "login":
                fragment = new live_list();
                break;
            case "VLOG":
                fragment = new vlog();
                buttomNavigationManager("nav_vlog");
                break;
            case "face_talk":
                fragment = new video_call();
                buttomNavigationManager("nav_face_talk");
                break;

        }

        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_container, fragment).commit();
    }

    private void request_permission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.MODIFY_AUDIO_SETTINGS) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

            }else{
                if(this.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                    Toast.makeText(this, "방송을 하시려면 카메라 접근을 허락하셔야합니다", Toast.LENGTH_SHORT).show();
                }

                if(this.shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)){
                    Toast.makeText(this, "방송을 하시려면 마이크 접근을 허락하셔야합니다", Toast.LENGTH_SHORT).show();
                }

                if(this.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    Toast.makeText(this, "방송을 하시려면 외부파일 접근을 허락하셔야합니다", Toast.LENGTH_SHORT).show();
                }

                this.requestPermissions(new String[] {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MODIFY_AUDIO_SETTINGS}, REQUEST_PERMISSION);
            }
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_PERMISSION){
            if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                Toast.makeText(getApplicationContext(), "방송을 하기 위해선, 카메라 접근을 허락하셔야합니다", Toast.LENGTH_SHORT).show();
            }

            if(grantResults[1] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(getApplicationContext(), "방송을 하기 위해선, 오디오 접근을 허락하셔야합니다", Toast.LENGTH_SHORT).show();
            }

            if(grantResults[2] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(getApplicationContext(), "방송을 하기 위해선, 외부파일 접근을 허락하셔야합니다", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        finish();
    }


}
