package com.skku.jiwon_hae.graduate_project_android_streaming.image.filter;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.skku.jiwon_hae.graduate_project_android_streaming.R;
import com.skku.jiwon_hae.graduate_project_android_streaming.image.camera.capture.preview_capture_image;
import com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.streamer.streaming;
import com.skku.jiwon_hae.graduate_project_android_streaming.vlog.record.vlog_recording;
import com.skku.jiwon_hae.graduate_project_android_streaming.vlog.upload_vlog_videos.upload_vlog;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class image_filters extends AppCompatActivity implements View.OnClickListener{

    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }

    private static final String TAG = "opencv";
    static final int PERMISSION_REQUEST_CODE = 1;
    String[] PERMISSIONS  = {"android.permission.WRITE_EXTERNAL_STORAGE"};

    //NextButton
    private Button next_btn;

    //Back Button
    private ImageButton back_btn;

    //ImageViews
    private ImageView preview_image;
    private ImageView filter_original;
    private ImageView filter_canny;
    private ImageView filter_negative;
    private ImageView filter_grey;
    private ImageView filter_blur;
    private ImageView filter_erode;
    private ImageView filter_dilate;

    //ImageName
    private String image_addr;
    private String userName;

    //Mat
    private Mat img_input = new Mat();
    private Mat img_output_filter_original = new Mat();
    private Mat img_output_filter_canny = new Mat();
    private Mat img_output_filter_negtaive = new Mat();
    private Mat img_output_filter_grey = new Mat();
    private Mat img_output_filter_blur = new Mat();
    private Mat img_output_filter_erosion = new Mat();
    private Mat img_output_filter_dilation = new Mat();

    private Mat currentMat = img_output_filter_original;

    //Brightness Control
    private SeekBar birghtness_seekbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_filters);

        android.support.v7.app.ActionBar mActionBar = this.getSupportActionBar();
        mActionBar.hide();

        next_btn = (Button)findViewById(R.id.next);
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    File mImageFolder = new File("sdcard", "nova_streaming");

                    File imageFile = File.createTempFile(userName+"_", ".jpg", mImageFolder);

                    if(currentFilter.equals("filter_original") || currentFilter.equals("filter_erode") || currentFilter.equals("filter_blur") || currentFilter.equals("filter_dilation")){
                        saveImage(currentMat.getNativeObjAddr(), imageFile.toString(), true);
                    }else{
                        saveImage(currentMat.getNativeObjAddr(), imageFile.toString(), false);
                    }

                    switch (getIntent().getStringExtra("TAG")){
                        case "VLOG":
                            Intent to_vlog = new Intent(image_filters.this, upload_vlog.class);
                            to_vlog.putExtra("vlogAddr", imageFile.toString());
                            to_vlog.putExtra("userName", userName);
                            to_vlog.putExtra("TAG", getIntent().getStringExtra("TAG"));
                            to_vlog.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(to_vlog);

                            finish();
                            break;

                        case "PROFILE":
                            Intent to_profile = new Intent(image_filters.this, vlog_recording.class);
                            startActivity(to_profile);
                            break;
                        case "STREAMING":
                            Intent to_preview_picture_taken = new Intent(image_filters.this, preview_capture_image.class);
                            to_preview_picture_taken.putExtra("image_addr", imageFile.toString());
                            to_preview_picture_taken.putExtra("userName", userName);
                            to_preview_picture_taken.putExtra("TAG", getIntent().getStringExtra("TAG"));
                            to_preview_picture_taken.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(to_preview_picture_taken);

                            finish();
                            break;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });

        preview_image = (ImageView)findViewById(R.id.image_preview);
        filter_original = (ImageView)findViewById(R.id.filter_original);
        filter_canny = (ImageView)findViewById(R.id.filter_canny);
        filter_negative = (ImageView)findViewById(R.id.filter_negative);
        filter_grey = (ImageView)findViewById(R.id.filter_grey);
        filter_blur = (ImageView)findViewById(R.id.filter_blur);
        filter_erode = (ImageView)findViewById(R.id.filter_erosion);
        filter_dilate = (ImageView)findViewById(R.id.filter_dilation);

        birghtness_seekbar = (SeekBar)findViewById(R.id.control_brightness);

        if(getIntent().hasExtra("image_addr")){
            image_addr = getIntent().getStringExtra("image_addr");
            Log.e("image_addr", image_addr);
        }

        if(getIntent().hasExtra("userName")){
            userName = getIntent().getStringExtra("userName");
        }

        if (!hasPermissions(PERMISSIONS)) { //퍼미션 허가를 했었는지 여부를 확인
            requestNecessaryPermissions(PERMISSIONS);//퍼미션 허가안되어 있다면 사용자에게 요청
        } else {
            //이미 사용자에게 퍼미션 허가를 받음.
            read_image_file(image_addr);
            imageprocess_and_showResult();
        }

        birghtness_seekbar.setMax(100);

        birghtness_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ChangeBrightness(currentFilter, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        back_btn = (ImageButton) findViewById(R.id.image_filter_back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(image_addr);

                if(file.delete()){
                    Toast.makeText(image_filters.this, "file deleted", Toast.LENGTH_SHORT).show();
                }

                finish();
            }
        });
    }

    public void initializeSeekBars(){
        birghtness_seekbar.setProgress(0);
    }

    private void ChangeBrightness(String filter, int value){
        double alpha = 1 + (value * 0.01);

        Mat input = null;
        Mat output = new Mat();

        switch(filter){
            case "filter_original":
                input = img_output_filter_original;
                break;
            case "filter_grey":
                input = img_output_filter_grey;
                break;
            case "filter_canny":
                input = img_output_filter_canny;
                break;
            case "filter_negative":
                input = img_output_filter_negtaive;
                break;
            case "filter_blur":
                input = img_output_filter_blur;
                break;
            case "filter_erode":
                input = img_output_filter_erosion;
                break;
            case "filter_dilation":
                input = img_output_filter_dilation;
                break;
        }

        changeBrightness(input.getNativeObjAddr(), output.getNativeObjAddr(), alpha);

        Bitmap bitmapOutput_brightness = Bitmap.createBitmap(output.cols(), output.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(output, bitmapOutput_brightness);
        preview_image.setImageBitmap(bitmapOutput_brightness);

        currentMat = output;
    }

    private void read_image_file(String image) {
        copyFile(image);
        loadImage(image, img_input.getNativeObjAddr());
    }

    private void copyFile(String filename) {
        String baseDir = Environment.getExternalStorageDirectory().getPath();
        String pathDir = baseDir + File.separator + filename;

        AssetManager assetManager = this.getAssets();

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            Log.d( TAG, "copyFile :: 다음 경로로 파일복사 "+ pathDir);
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
            Log.d(TAG, "copyFile :: 파일 복사 중 예외 발생 "+e.toString() );
        }
    }


    //Permission
    private boolean hasPermissions(String[] permissions) {
        int ret = 0;
        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions){
            ret = checkCallingOrSelfPermission(perms);
            if (!(ret == PackageManager.PERMISSION_GRANTED)){
                //퍼미션 허가 안된 경우
                return false;
            }

        }
        //모든 퍼미션이 허가된 경우
        return true;
    }

    private void requestNecessaryPermissions(String[] permissions) {
        //마시멜로( API 23 )이상에서 런타임 퍼미션(Runtime Permission) 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
        switch(permsRequestCode){

            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean writeAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        if (!writeAccepted )
                        {
                            showDialogforPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                            return;
                        }else
                        {
                            read_image_file(image_addr);
                            imageprocess_and_showResult();
                        }
                    }
                }
                break;
        }
    }

    private void showDialogforPermission(String msg) {

        final AlertDialog.Builder myDialog = new AlertDialog.Builder(  image_filters.this);
        myDialog.setTitle("알림");
        myDialog.setMessage(msg);
        myDialog.setCancelable(false);
        myDialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE);
                }

            }
        });
        myDialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        myDialog.show();
    }

    public native void loadImage(String imageFileName, long img);
    public native void saveImage(long inputImage, String imageName, boolean convert);

    public native void filter_original(long inputImage, long outputImage);
    public native void filter_canny(long inputImage, long outputImage);
    public native void filter_negative(long inputImage, long outputImage);
    public native void filter_grey(long inputImage, long outputImage);
    public native void filter_blur(long inputImage, long outputImage);
    public native void filter_erode(long inputImage, long outputImage);
    public native void filter_dilate(long inputImage, long outputImage);

    //SeekBar Feature
    public native void changeBrightness(long inputImage, long outputImage, double value);


    private String currentFilter = "filter_original";

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.filter_original:
                if(!currentFilter.equals("filter_original")) {
                    Bitmap bitmapInput_original = Bitmap.createBitmap(img_output_filter_original.cols(), img_output_filter_original.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(img_output_filter_original, bitmapInput_original);
                    preview_image.setImageBitmap(bitmapInput_original);

                    currentFilter = "filter_original";
                    currentMat = img_output_filter_original;
                    birghtness_seekbar.setProgress(0);
                }

                break;

            case R.id.filter_canny:
                if(!currentFilter.equals("filter_canny")) {
                    Bitmap bitmapOutput_canny = Bitmap.createBitmap(img_output_filter_canny.cols(), img_output_filter_canny.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(img_output_filter_canny, bitmapOutput_canny);
                    preview_image.setImageBitmap(bitmapOutput_canny);

                    currentFilter = "filter_canny";
                    currentMat = img_output_filter_canny;
                    birghtness_seekbar.setProgress(0);
                }

                break;

            case R.id.filter_negative:

                if(!currentFilter.equals("filter_negative")) {
                    Bitmap bitmapOutput_negative = Bitmap.createBitmap(img_output_filter_negtaive.cols(), img_output_filter_negtaive.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(img_output_filter_negtaive, bitmapOutput_negative);
                    preview_image.setImageBitmap(bitmapOutput_negative);

                    currentFilter = "filter_negative";
                    currentMat = img_output_filter_negtaive;
                    birghtness_seekbar.setProgress(0);
                }
                break;

            case R.id.filter_grey:
                if(!currentFilter.equals("filter_grey")) {
                    Bitmap bitmapOutput_grey = Bitmap.createBitmap(img_output_filter_grey.cols(), img_output_filter_grey.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(img_output_filter_grey, bitmapOutput_grey);
                    preview_image.setImageBitmap(bitmapOutput_grey);

                    currentFilter = "filter_grey";
                    currentMat = img_output_filter_grey;
                    birghtness_seekbar.setProgress(0);
                }
                break;

            case R.id.filter_blur:
                if(!currentFilter.equals("filter_blur")) {
                    Bitmap bitmapOutput_blur = Bitmap.createBitmap(img_output_filter_blur.cols(), img_output_filter_blur.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(img_output_filter_blur, bitmapOutput_blur);
                    preview_image.setImageBitmap(bitmapOutput_blur);

                    currentFilter = "filter_blur";
                    currentMat = img_output_filter_blur;
                    birghtness_seekbar.setProgress(0);
                }
                break;

            case R.id.filter_erosion:
                if(!currentFilter.equals("filter_erode")) {
                    Bitmap bitmapOutput_blur = Bitmap.createBitmap(img_output_filter_erosion.cols(), img_output_filter_erosion.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(img_output_filter_erosion, bitmapOutput_blur);
                    preview_image.setImageBitmap(bitmapOutput_blur);

                    currentFilter = "filter_erode";
                    currentMat = img_output_filter_erosion;
                    birghtness_seekbar.setProgress(0);
                }
                break;

            case R.id.filter_dilation:
                if(!currentFilter.equals("filter_dilation")) {
                    Bitmap bitmapOutput_dilate = Bitmap.createBitmap(img_output_filter_dilation.cols(), img_output_filter_dilation.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(img_output_filter_dilation, bitmapOutput_dilate);
                    preview_image.setImageBitmap(bitmapOutput_dilate);

                    currentFilter = "filter_dilation";
                    currentMat = img_output_filter_dilation;
                    birghtness_seekbar.setProgress(0);
                }
                break;
        }
    }

    private void imageprocess_and_showResult() {

        filter_original(img_input.getNativeObjAddr(), img_output_filter_original.getNativeObjAddr());

        Bitmap bitmapInput = Bitmap.createBitmap(img_output_filter_original.cols(), img_output_filter_original.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_output_filter_original, bitmapInput);
        filter_original.setImageBitmap(bitmapInput);
        preview_image.setImageBitmap(bitmapInput);

        filter_canny(img_input.getNativeObjAddr(), img_output_filter_canny.getNativeObjAddr());

        Bitmap bitmapOutput_canny = Bitmap.createBitmap(img_output_filter_canny.cols(), img_output_filter_canny.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_output_filter_canny, bitmapOutput_canny);
        filter_canny.setImageBitmap(bitmapOutput_canny);

        filter_negative(img_input.getNativeObjAddr(), img_output_filter_negtaive.getNativeObjAddr());

        Bitmap bitmapOutput_negative = Bitmap.createBitmap(img_output_filter_negtaive.cols(), img_output_filter_negtaive.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_output_filter_negtaive, bitmapOutput_negative);
        filter_negative.setImageBitmap(bitmapOutput_negative);

        filter_grey(img_input.getNativeObjAddr(), img_output_filter_grey.getNativeObjAddr());

        Bitmap bitmapOutput_grey = Bitmap.createBitmap(img_output_filter_grey.cols(), img_output_filter_grey.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_output_filter_grey, bitmapOutput_grey);
        filter_grey.setImageBitmap(bitmapOutput_grey);

        filter_blur(img_input.getNativeObjAddr(), img_output_filter_blur.getNativeObjAddr());

        Bitmap bitmapOutput_blur = Bitmap.createBitmap(img_output_filter_blur.cols(), img_output_filter_blur.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_output_filter_blur, bitmapOutput_blur);
        filter_blur.setImageBitmap(bitmapOutput_blur);

        filter_erode(img_input.getNativeObjAddr(), img_output_filter_erosion.getNativeObjAddr());

        Bitmap bitmapOutput_erode = Bitmap.createBitmap(img_output_filter_erosion.cols(), img_output_filter_erosion.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_output_filter_erosion, bitmapOutput_erode);
        filter_erode.setImageBitmap(bitmapOutput_erode);

        filter_dilate(img_input.getNativeObjAddr(), img_output_filter_dilation.getNativeObjAddr());

        Bitmap bitmapOutput_dilate = Bitmap.createBitmap(img_output_filter_dilation.cols(), img_output_filter_dilation.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_output_filter_dilation, bitmapOutput_dilate);
        filter_dilate.setImageBitmap(bitmapOutput_dilate);
    }


    @Override
    public void onBackPressed() {

        switch (getIntent().getStringExtra("TAG")){
            case "VLOG":
                Intent to_vlog = new Intent(image_filters.this, vlog_recording.class);
                startActivity(to_vlog);
                break;
            case "PROFILE":
                Intent to_profile = new Intent(image_filters.this, vlog_recording.class);
                startActivity(to_profile);
                break;
            case "STREAMING":
                Intent to_streaming = new Intent(image_filters.this, streaming.class);
                startActivity(to_streaming);
                break;
        }
    }
}
