package com.skku.jiwon_hae.graduate_project_android_streaming.image.camera.capture;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.skku.jiwon_hae.graduate_project_android_streaming.R;
import com.skku.jiwon_hae.graduate_project_android_streaming.image.camera.camera_setting;

/**
 * Created by jiwon_hae on 2017. 10. 30..
 */

public class capture_image1 extends Fragment {

    private TextureView capture_textureView;

    private camera_setting capture_camera;
    private ImageButton capture_btn;

    private String userName;
    private String TAG;

    private static int REQUEST_CAMERA_PERMISSION = 7900;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_capture_image, container, false);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {
            // 이 권한을 필요한 이유를 설명해야하는가?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_CONTACTS)) {


            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA_PERMISSION);
            }
        }

        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 7900:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(getContext(), "해당 권한 없이는 사용 하실 수 없습니다", Toast.LENGTH_LONG).show();
                    getActivity().finish();
                }
                return;
        }
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        capture_textureView = (TextureView)view.findViewById(R.id.capture_preview);

        userName = getActivity().getIntent().getStringExtra("userName");

        if(getActivity().getIntent().hasExtra("TAG")){
            TAG = getActivity().getIntent().getStringExtra("TAG");
        }

        capture_camera = new camera_setting(getActivity(), "CAPTURE_IMAGE");

        capture_camera.setTextureView(capture_textureView);
        capture_camera.createImageFolder();

        capture_btn = (ImageButton)view.findViewById(R.id.capture_btn);
        capture_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capture_camera.lockFocus(userName);
            }
        });
    }

    @Override
    public void onResume() {
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
    public void onPause() {
        super.onPause();

        capture_camera.closeCamera();
        capture_camera.stopBackgroundThread();

        getActivity().finish();
    }


}
