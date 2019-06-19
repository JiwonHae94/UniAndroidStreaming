package com.skku.jiwon_hae.graduate_project_android_streaming.vlog.gallery;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.skku.jiwon_hae.graduate_project_android_streaming.R;
import com.skku.jiwon_hae.graduate_project_android_streaming.vlog.upload_vlog_videos.upload_vlog;

import java.util.ArrayList;

/**
 * Created by jiwon_hae on 2017. 10. 30..
 */

public class vlog_gallery_adapter extends BaseAdapter {
    private Context mContext;
    private Activity mActivity;
    private VideoView preview_video;
    private ArrayList<String> listOfVideos;
    private LayoutInflater inflater;
    private String videoURI;

    public vlog_gallery_adapter(final Context context, final Activity mActivity, VideoView video_preview, Button next) {
        this.mContext = context;
        this.preview_video = video_preview;
        this.inflater = LayoutInflater.from(mContext);

        listOfVideos = getAllShownImagesPath(mActivity);

        if (listOfVideos.size() > 0) {
            videoURI = listOfVideos.get(0);

            preview_video.setVideoURI(Uri.parse(videoURI));
            preview_video.requestFocus();
            preview_video.start();

        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alert.setMessage("갖고 있는 영상 없습니다");
            alert.show();
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent to_preview_picture_taken = new Intent(mActivity, upload_vlog.class);
                to_preview_picture_taken.putExtra("vlogAddr", videoURI);
                to_preview_picture_taken.putExtra("userName", "bdc01051");
                mActivity.finish();
                mActivity.startActivity(to_preview_picture_taken);
            }
        });
    }

    @Override
    public int getCount() {
        return listOfVideos.size();
    }

    @Override
    public Object getItem(int position) {
        return listOfVideos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View gridLayout = convertView;

        gridLayout = inflater.inflate(R.layout.vlog_gallery_item, null);
        ImageView grid_img = (ImageView) gridLayout.findViewById(R.id.gallery_item);

        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        int width = dm.heightPixels;
        int height = dm.heightPixels;

        grid_img.setMinimumWidth(width - 1);
        grid_img.setMaxHeight(height - 1);

        Glide.with(mContext)
                .load(listOfVideos.get(position))
                .thumbnail(0.1f)
                .into(grid_img);

        grid_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preview_video.setVideoURI(Uri.parse(listOfVideos.get(position)));
                preview_video.requestFocus();
                preview_video.start();
            }
        });


        return gridLayout;
    }

    private ArrayList<String> getAllShownImagesPath(Activity activity) {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllVideos = new ArrayList<String>();
        String absolutePathOfImage = null;

        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);

        Log.e("coumn", String.valueOf(column_index_data));

        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            listOfAllVideos.add(0, absolutePathOfImage);
        }

        return listOfAllVideos;
    }
}
