package com.skku.jiwon_hae.graduate_project_android_streaming.vlog.record;

import android.content.Intent;
import android.content.res.AssetManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.skku.jiwon_hae.graduate_project_android_streaming.R;
import com.skku.jiwon_hae.graduate_project_android_streaming.main;
import com.skku.jiwon_hae.graduate_project_android_streaming.utils.useSharedPreference;
import com.skku.jiwon_hae.graduate_project_android_streaming.vlog.gallery.vlog_gallery;
import com.skku.jiwon_hae.graduate_project_android_streaming.vlog.upload_vlog_videos.upload_vlog;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class vlog_recording extends AppCompatActivity implements JavaCameraView.CvCameraViewListener2, View.OnClickListener  {

    //UserInformation
    private useSharedPreference useDB;
    private String userInformation;
    private String user_email;
    private String userName;

    private JavaCameraView VideoView;
    private Mat view_mat;

    private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);
    public static final int JAVA_DETECTOR = 0;
    public static final int NATIVE_DETECTOR = 1;

    private CascadeClassifier mJavaDetector;

    private int mDetectorType = JAVA_DETECTOR;
    private String[] mDetectorName;

    private final int NO_MASK = 0;
    private final int DOG1_MASK = 1;
    private final int DOG2_MASK = 2;
    private final int CAT1_MASK = 3;
    private final int CAT2_MASK = 4;
    private final int RABBIT_MASK = 5;
    private final int MICE_MASK = 6;
    private final int SHOW_FACE_DETECT = 7;

    private int MASK_NUMBER = NO_MASK;

    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }

    private ImageButton show_masks;
    private ImageButton to_gallery;
    private RelativeLayout mask_layout;
    private LinearLayout record_layout;

    //Masks
    private ImageView dog1_mask;
    private ImageView dog2_mask;
    private ImageView cat1_mask;
    private ImageView cat2_mask;
    private ImageView rabbit_mask;
    private ImageView mice_mask;
    private ImageView remove_masks ;
    private ImageView face_detect;

    //Record
    private MediaRecorder vlog_recorder;
    private boolean isRecording = false;
    private Camera mCamera;
    private ProgressBar vlog_record;
    private int recordTime = 0;
    private int maxRecordTime = 1000 * 60; //1분
    private CountDownTimer record_timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vlog_recording);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getSupportActionBar().hide();

        useDB = new useSharedPreference(this);
        userInformation = useDB.get_user_information();

        try {
            JSONObject jsonObject = new JSONObject(userInformation);
            userName = jsonObject.get("userName").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mDetectorName = new String[2];
        mDetectorName[JAVA_DETECTOR] = "Java";
        mDetectorName[NATIVE_DETECTOR] = "Native (tracking)";

        VideoView = (JavaCameraView) findViewById(R.id.video_view);

        read_cascade_file();

        dog1_mask = (ImageView)findViewById(R.id.mask_dog1);
        dog1_mask.setOnClickListener(this);

        dog2_mask = (ImageView)findViewById(R.id.mask_dog2);
        dog2_mask.setOnClickListener(this);

        cat1_mask = (ImageView)findViewById(R.id.mask_cat1);
        cat1_mask.setOnClickListener(this);

        cat2_mask = (ImageView)findViewById(R.id.mask_cat2);
        cat2_mask.setOnClickListener(this);

        rabbit_mask = (ImageView)findViewById(R.id.mask_rabbit);
        rabbit_mask.setOnClickListener(this);

        mice_mask = (ImageView)findViewById(R.id.mask_mice);
        mice_mask.setOnClickListener(this);

        face_detect = (ImageView)findViewById(R.id.face_detection_display);
        face_detect.setOnClickListener(this);

        show_masks = (ImageButton)findViewById(R.id.vlog_mask);
        show_masks.setOnClickListener(this);

        to_gallery = (ImageButton)findViewById(R.id.vlog_gallery);
        to_gallery.setOnClickListener(this);

        remove_masks = (ImageButton)findViewById(R.id.remove_masks);
        remove_masks.setOnClickListener(this);

        mask_layout = (RelativeLayout)findViewById(R.id.mask_layout);
        record_layout = (LinearLayout)findViewById(R.id.record_layout);


        vlog_record = (ProgressBar) findViewById(R.id.vlog_record) ;

        vlog_record.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        try {
                            startRecording();
                            VideoView.startRecording();
                            isRecording = true;

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        record_timer = new CountDownTimer(maxRecordTime, 100) {
                            public void onTick(long millisUntilFinished) {
                                recordTime += 100;
                                vlog_record.setProgress(recordTime);
                            }

                            public void onFinish() {
                                VideoView.stopRecording();
                                isRecording = false;

                                Intent to_upload_vlog = new Intent(vlog_recording.this, upload_vlog.class);
                                startActivity(to_upload_vlog);

                                finish();
                            }
                        }.start();

                        return true;
                    case MotionEvent.ACTION_UP:

                        record_timer.cancel();
                        vlog_record.setProgress(0);

                        recordTime = 0;

                        if(VideoView.isRecording){
                            VideoView.stopRecording();
                            isRecording = false;
                        }

                        Intent to_upload_vlog = new Intent(vlog_recording.this, upload_vlog.class);
                        to_upload_vlog.putExtra("vlogAddr", vlog_file.toString());
                        to_upload_vlog.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(to_upload_vlog);

                        finish();

                        return true;
                }
                return true;
            }
        });
    }

    private void copyFile(String filename) {
        String baseDir = Environment.getExternalStorageDirectory().getPath();
        String pathDir = baseDir + File.separator + filename;

        AssetManager assetManager = this.getAssets();

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = assetManager.open(filename);
            outputStream = new FileOutputStream(pathDir);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            inputStream = null;
            outputStream.flush();
            outputStream.close();
            outputStream = null;
        } catch (Exception e) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        VideoView.setVisibility(CameraBridgeViewBase.VISIBLE);
        VideoView.setCvCameraViewListener(this);
        VideoView.setCameraIndex(1);

        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
    }



    @Override
    public void onClick(View v) {
        switch(v.getId()) {

            case R.id.vlog_mask:
                if(mask_layout.getVisibility() == View.GONE){
                    mask_layout.setVisibility(View.VISIBLE);

                    if(record_layout.getVisibility() == View.VISIBLE){
                        record_layout.setVisibility(View.GONE);
                    }
                }
                break;

            case R.id.vlog_gallery:
                Intent to_gallery = new Intent(vlog_recording.this, vlog_gallery.class);
                startActivity(to_gallery);
                break;

            case R.id.mask_dog1:
                MASK_NUMBER = DOG1_MASK;
                break;
            case R.id.mask_dog2:
                MASK_NUMBER = DOG2_MASK;
                break;
            case R.id.mask_cat1:
                MASK_NUMBER = CAT1_MASK;
                break;
            case R.id.mask_cat2:
                MASK_NUMBER = CAT2_MASK;
                break;
            case R.id.mask_rabbit:
                MASK_NUMBER = RABBIT_MASK;
                break;
            case R.id.mask_mice:
                MASK_NUMBER = MICE_MASK;
                break;
            case R.id.face_detection_display:
                MASK_NUMBER = SHOW_FACE_DETECT;
                break;
            case R.id.remove_masks:
                MASK_NUMBER = NO_MASK;
                break;
        }
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    VideoView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {
    }

    private File image_file;
    private boolean move_to_filters = true;

    private Mat matInput;
    private Mat matResult = new Mat();

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        matInput = inputFrame.rgba();

        Core.flip(matInput.t(), matInput, 0);
        Core.flip(matInput, matInput, 1);

        if(matResult != null){
            matResult.release();
        }

        switch (MASK_NUMBER) {
            case 0:
                maskOn(NO_MASK, cascadeClassifier_face, cascadeClassifier_eye, matInput.getNativeObjAddr(), matResult.getNativeObjAddr());
                break;
            case 1:
                maskOn(DOG1_MASK, cascadeClassifier_face, cascadeClassifier_eye, matInput.getNativeObjAddr(), matResult.getNativeObjAddr());
                break;
            case 2:
                maskOn(DOG2_MASK, cascadeClassifier_face, cascadeClassifier_eye, matInput.getNativeObjAddr(), matResult.getNativeObjAddr());
                break;
            case 3:
                maskOn(CAT1_MASK, cascadeClassifier_face, cascadeClassifier_eye, matInput.getNativeObjAddr(), matResult.getNativeObjAddr());
                break;
            case 4:
                maskOn(CAT2_MASK, cascadeClassifier_face, cascadeClassifier_eye, matInput.getNativeObjAddr(), matResult.getNativeObjAddr());
                break;
            case 5:
                maskOn(RABBIT_MASK, cascadeClassifier_face, cascadeClassifier_eye, matInput.getNativeObjAddr(), matResult.getNativeObjAddr());
                break;
            case 6:
                maskOn(MICE_MASK, cascadeClassifier_face, cascadeClassifier_eye, matInput.getNativeObjAddr(), matResult.getNativeObjAddr());
                break;
            case 7:
                maskOn(SHOW_FACE_DETECT, cascadeClassifier_face, cascadeClassifier_eye, matInput.getNativeObjAddr(), matResult.getNativeObjAddr());
                break;
        }
        return matResult;
    }




    @Override
    public void onBackPressed() {

        if(mask_layout.getVisibility() == View.VISIBLE){
            mask_layout.setVisibility(View.GONE);

            if(record_layout.getVisibility() == View.GONE){
                record_layout.setVisibility(View.VISIBLE);
            }

        } else{
            Intent to_main = new Intent(vlog_recording.this, main.class);
            startActivity(to_main);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        VideoView.disableView();
        finish();
    }

    private MediaRecorder recorder = new MediaRecorder();
    private File vlog_file;

    //Video Recording
    public void setMediaRecorder() throws IOException {
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);

        CamcorderProfile cpHigh = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        recorder.setProfile(cpHigh);

        File vlog_folder = new File("sdcard/nova_streaming/VLOG");

        if(!vlog_folder.exists()) {
            vlog_folder.mkdirs();
        }

        vlog_file = new File(vlog_folder + "/" + userName+"_" + System.currentTimeMillis()+".mp4");

        recorder.setOutputFile(vlog_file.toString());
        recorder.setVideoSize(VideoView.getFrameWidth(),  VideoView.getFrameHeight());
        recorder.setVideoFrameRate(500);

        recorder.prepare();
        VideoView.setRecorder(recorder);
    }

    public void startRecording() throws IOException {
        setMediaRecorder();
        recorder.start();
    }

    //Camera and filters
    private void read_cascade_file(){
        copyFile("haarcascade_frontalface_alt.xml");
        copyFile("haarcascade_eye_tree_eyeglasses.xml");

        cascadeClassifier_face = loadCascade("haarcascade_frontalface_alt.xml");
        cascadeClassifier_eye = loadCascade("haarcascade_eye_tree_eyeglasses.xml");
    }

    public native long loadCascade(String cascadeFileName );
    public native void maskOn(int masktype, long cascadeClassifier_face,
                              long cascadeClassifier_eye, long matAddrInput, long matAddrResult);

    public native void captureImage(long matAddrInput, String imageName);

    public long cascadeClassifier_face = 0;
    public long cascadeClassifier_eye = 0;
}