package com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.streamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.skku.jiwon_hae.graduate_project_android_streaming.R;
import com.skku.jiwon_hae.graduate_project_android_streaming.main;

public class streaming_end extends AppCompatActivity {

    private Button to_home;
    private TextView total_streaming_time;
    private TextView total_streaming_viewer;
    private TextView total_new_subscriber;

    private String total_duration = "00:00:00";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming_end);

        getSupportActionBar().hide();

        to_home = (Button)findViewById(R.id.streaming_end_toHome);
        to_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent to_home = new Intent(streaming_end.this, main.class);
                startActivity(to_home);
            }
        });

        total_streaming_time = (TextView)findViewById(R.id.streaming_end_total_streaming_time);
        total_streaming_viewer = (TextView)findViewById(R.id.streaming_end_viwerCount);
        total_new_subscriber = (TextView)findViewById(R.id.streaming_end_new_subscriber_count);

        if(getIntent().hasExtra("streaming_duration")){
            if(getIntent().getStringExtra("streaming_duration").length()==5){
                total_streaming_time.setText("00:"+getIntent().getStringExtra("streaming_duration"));
            }else if(getIntent().getStringExtra("streaming_duration").length()==8){
                total_streaming_time.setText(getIntent().getStringExtra("streaming_duration"));
            }
        }else{
            total_streaming_time.setText(total_duration);
        }

        if(getIntent().hasExtra("streaming_total_viewer")){
            total_streaming_viewer.setText(getIntent().getStringExtra("streaming_total_viewer"));
        }

        if(getIntent().hasExtra("streaming_new_subscriber")){
            total_new_subscriber.setText(getIntent().getStringExtra("streaming_new_subscriber"));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent to_home = new Intent(streaming_end.this, main.class);
        startActivity(to_home);
    }
}
