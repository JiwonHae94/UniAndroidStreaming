package com.skku.jiwon_hae.graduate_project_android_streaming.image.camera.capture;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.skku.jiwon_hae.graduate_project_android_streaming.R;
import com.skku.jiwon_hae.graduate_project_android_streaming.image.upload_image.upload_images;
import com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.streamer.streaming;

public class preview_capture_image extends AppCompatActivity {

    private ImageView preview_imageView;
    private String image_addr;

    private Button save;
    private String TAG;

    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_capture_image);

        preview_imageView = (ImageView)findViewById(R.id.preview_imageView);

        if(getIntent().hasExtra("image_addr")){
            image_addr = getIntent().getStringExtra("image_addr");
        }

        if(getIntent().hasExtra("userName")){
            userName = getIntent().getStringExtra("userName");
        }

        Glide.with(this)
                .load(image_addr)
                .into(preview_imageView);

        if(getIntent().hasExtra("TAG")){
            this.TAG = getIntent().getStringExtra("TAG");
        }

        save = (Button)findViewById(R.id.preview_image_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent upload_images = new Intent(preview_capture_image.this, upload_images.class);
                upload_images.putExtra("userName", userName);
                Log.e("capture", userName);
                upload_images.putExtra("image_addr", image_addr);
                startService(upload_images);

                if(getIntent().hasExtra("TAG")){
                    if(getIntent().getStringExtra("TAG").equals("STREAMING")){
                        Intent to_streaming = new Intent(preview_capture_image.this, streaming.class);
                        to_streaming.putExtra("image", image_addr);
                        startActivity(to_streaming);
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        finish();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        View decorView = getWindow().getDecorView();

        if(hasFocus){
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            );
        }
    }
}
