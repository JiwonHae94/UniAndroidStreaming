package com.skku.jiwon_hae.graduate_project_android_streaming.vlog.upload_vlog_videos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.skku.jiwon_hae.graduate_project_android_streaming.R;
import com.skku.jiwon_hae.graduate_project_android_streaming.main;
import com.skku.jiwon_hae.graduate_project_android_streaming.utils.useSharedPreference;
import com.skku.jiwon_hae.graduate_project_android_streaming.vlog.record.vlog_recording;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class upload_vlog extends AppCompatActivity {

    private String vlogAddr;
    private ImageView vlogImage;
    private Button cancel;
    private EditText tags;

    private File current_vlog_file;

    //Keyboard
    private Button save;
    private LinearLayout keyboard_related_layout;
    private Button add_tags;

    //UserInformation
    private useSharedPreference useDB;
    private String userInformation;
    private String user_email;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_vlog);
        getSupportActionBar().hide();

        useDB = new useSharedPreference(this);
        userInformation = useDB.get_user_information();

        try {
            JSONObject jsonObject = new JSONObject(userInformation);
            userName = jsonObject.get("userName").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        cancel = (Button)findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_vlog_file.delete();
                Intent to_recording = new Intent(upload_vlog.this, vlog_recording.class);
                finish();
                startActivity(to_recording);
            }
        });

        vlogImage = (ImageView)findViewById(R.id.vlog_preview);
        vlogAddr = getIntent().getStringExtra("vlogAddr");
        current_vlog_file = new File(vlogAddr);

        Glide.with(this)
                .load(vlogAddr)
                .thumbnail(0.5f)
                .into(vlogImage);

        //Tags
        keyboard_related_layout = (LinearLayout)findViewById(R.id.key_board_layout);
        add_tags = (Button)findViewById(R.id.add_tag);

        add_tags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tags.append(" #");
                tags.setSelection(tags.getText().length());
            }
        });


        tags = (EditText)findViewById(R.id.vlog_tags);
        tags.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    keyboard_related_layout.setVisibility(View.VISIBLE);
                }else{
                    keyboard_related_layout.setVisibility(View.GONE);
                }
            }
        });

        save = (Button)findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent upload_vlog = new Intent(upload_vlog.this, upload_vlog_video.class);
                upload_vlog.putExtra("userName", userName);
                upload_vlog.putExtra("vlog_addr", vlogAddr);
                upload_vlog.putExtra("tags", tags.getText().toString());
                upload_vlog.putExtra("vlog_name", current_vlog_file.getName());
                startService(upload_vlog);

                Intent to_vlog = new Intent(upload_vlog.this, main.class);
                to_vlog.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(to_vlog);
            }
        });
    }

    @Override
    public void onBackPressed() {

        if(tags.hasFocus()){
            tags.clearFocus();
        }else{
            current_vlog_file.delete();
            Intent to_recording = new Intent(upload_vlog.this, vlog_recording.class);
            finish();
            startActivity(to_recording);
        }
    }
}
