package com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.skku.jiwon_hae.graduate_project_android_streaming.R;
import com.skku.jiwon_hae.graduate_project_android_streaming.main;

public class live_end extends AppCompatActivity {

    private Button to_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_end);

        getSupportActionBar().hide();

        to_home = (Button)findViewById(R.id.live_end_toHome);
        to_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent to_home = new Intent(live_end.this, main.class);
                to_home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(to_home);
            }
        });
    }
}
