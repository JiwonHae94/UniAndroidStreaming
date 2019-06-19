package com.skku.jiwon_hae.graduate_project_android_streaming.vlog.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.VideoView;

import com.skku.jiwon_hae.graduate_project_android_streaming.R;
import com.skku.jiwon_hae.graduate_project_android_streaming.vlog.record.vlog_recording;

public class vlog_gallery extends AppCompatActivity {

    private Button cancel;
    private Button next;

    private GridView gridView;
    private VideoView video_preview;

    private vlog_gallery_adapter vlog_galleryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vlog_gallery);

        getSupportActionBar().hide();

        gridView = (GridView) findViewById(R.id.gallery_grid);
        video_preview = (VideoView) findViewById(R.id.video_preview);
        next = (Button) findViewById(R.id.next);

        vlog_galleryAdapter = new vlog_gallery_adapter(this, this, video_preview, next);

        cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent to_review = new Intent(vlog_gallery.this, vlog_recording.class);
                to_review.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                to_review.putExtra("fragment", "review");
                startActivity(to_review);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        gridView.setAdapter(vlog_galleryAdapter);
    }

    @Override
    public void onBackPressed() {
        Intent to_recording = new Intent(vlog_gallery.this, vlog_recording.class);
        startActivity(to_recording);
    }
}
