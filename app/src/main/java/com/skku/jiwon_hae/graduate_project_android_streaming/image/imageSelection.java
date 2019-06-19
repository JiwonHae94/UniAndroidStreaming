package com.skku.jiwon_hae.graduate_project_android_streaming.image;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.Gravity;

import com.skku.jiwon_hae.graduate_project_android_streaming.R;
import com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.streamer.streaming;

/**
 * Created by jiwon_hae on 2017. 10. 30..
 */

public class imageSelection extends FragmentActivity {
    private ViewPager viewPager;
    private PagerTitleStrip pagerTitleStrip;
    private imageSelection_adapter imageSelection_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selection);

        viewPager = (ViewPager)findViewById(R.id.write_review_pager);
        pagerTitleStrip = (PagerTitleStrip)findViewById(R.id.write_review_pager_tabs);
        pagerTitleStrip.setGravity(Gravity.CENTER);
        pagerTitleStrip.setTextSpacing(0);

        imageSelection_adapter = new imageSelection_adapter(getSupportFragmentManager());

        viewPager.setAdapter(imageSelection_adapter);
        viewPager.setCurrentItem(0, false);
        viewPager.setMotionEventSplittingEnabled(true);
    }

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {

        Intent to_streaming = new Intent(imageSelection.this, streaming.class);
        to_streaming.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(to_streaming);

    }

    @Override
    protected void onPause() {
        super.onPause();

        finish();
    }
}
