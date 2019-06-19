package com.skku.jiwon_hae.graduate_project_android_streaming.image.camera.capture;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;

import com.skku.jiwon_hae.graduate_project_android_streaming.R;
import com.skku.jiwon_hae.graduate_project_android_streaming.image.camera.camera_setting;

public class capture_image extends AppCompatActivity {

    private TextureView capture_textureView;

    private camera_setting capture_camera;
    private ImageButton capture_btn;

    private String userName;
    private String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_image);
        getSupportActionBar().hide();

        capture_textureView = (TextureView)findViewById(R.id.capture_preview);

        userName = getIntent().getStringExtra("userName");

        if(getIntent().hasExtra("TAG")){
            TAG = getIntent().getStringExtra("TAG");
        }

        capture_camera = new camera_setting(this, TAG);

        capture_camera.setTextureView(capture_textureView);
        capture_camera.createImageFolder();

        capture_btn = (ImageButton)findViewById(R.id.capture_btn);
        capture_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capture_camera.lockFocus(userName);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        capture_camera.startBackgroundThread();

        if(capture_textureView.isAvailable()){
            capture_camera.setupCamera(capture_textureView.getWidth(), capture_textureView.getHeight());
            capture_camera.connectCamera();
        }else{
            capture_textureView.setSurfaceTextureListener(capture_camera.mSurfaceTextureListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        capture_camera.closeCamera();
        capture_camera.stopBackgroundThread();
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
