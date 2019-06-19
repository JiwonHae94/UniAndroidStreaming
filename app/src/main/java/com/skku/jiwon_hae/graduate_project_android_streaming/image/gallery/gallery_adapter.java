package com.skku.jiwon_hae.graduate_project_android_streaming.image.gallery;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.skku.jiwon_hae.graduate_project_android_streaming.R;
import com.skku.jiwon_hae.graduate_project_android_streaming.image.filter.image_filters;

import java.util.ArrayList;

/**
 * Created by jiwon_hae on 2017. 10. 30..
 */

public class gallery_adapter extends BaseAdapter {
    private Context mContext;
    private Activity mActivity;
    private ImageView fullImage;
    private ArrayList<String> listOfImages;
    private LayoutInflater inflater;
    private String imageURI;
    private String TAG;

    private String userName;

    public gallery_adapter(final Context context, final Activity mActivity, ImageView imageView, final String TAG, Button next){
        this.mContext = context;
        this.fullImage = imageView;
        this.inflater = LayoutInflater.from(mContext);
        this.TAG = TAG;

        userName = mActivity.getIntent().getStringExtra("userName");

        listOfImages = getAllShownImagesPath(mActivity);

        if(listOfImages.size() > 0) {
            imageURI = listOfImages.get(0);

            Glide.with(this.mContext)
                    .load(listOfImages.get(0))
                    .into(this.fullImage);
        }else{
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
            alert.setMessage("갖고 있는 사진이 없습니다");
            alert.show();
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent to_preview_picture_taken = new Intent(mActivity, image_filters.class);
                to_preview_picture_taken.putExtra("image_addr", imageURI);
                to_preview_picture_taken.putExtra("userName", userName);
                to_preview_picture_taken.putExtra("TAG", TAG);
                mActivity.startActivity(to_preview_picture_taken);
            }
        });
    }

    @Override
    public int getCount() {
        return listOfImages.size();
    }

    @Override
    public Object getItem(int position) {
        return listOfImages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View gridLayout = convertView;

        gridLayout = inflater.inflate(R.layout.gallery_item, null);
        ImageView grid_img = (ImageView)gridLayout.findViewById(R.id.gallery_item);

        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        int width = dm.heightPixels;
        int height = dm.heightPixels;

        grid_img.setMinimumWidth(width -1);
        grid_img.setMaxHeight(height-1);

        Glide.with(mContext)
                .load(listOfImages.get(position))
                .dontAnimate()
                .thumbnail(0.1f)
                .into(grid_img);

        grid_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(mContext)
                        .load(listOfImages.get(position))
                        .thumbnail(0.3f)
                        .into(fullImage);

                imageURI = listOfImages.get(position);
            }
        });


        return gridLayout;
    }

    private ArrayList<String> getAllShownImagesPath(Activity activity) {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;

        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            //Log.e("cursor.getString(column_index_data)", cursor.getString(column_index_folder_name));
            listOfAllImages.add(0, absolutePathOfImage);
        }

        return listOfAllImages;
    }
}
