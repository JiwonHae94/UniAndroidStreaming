package com.skku.jiwon_hae.graduate_project_android_streaming.vlog;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.VideoView;

/**
 * Created by jiwon_hae on 2017. 11. 11..
 */

public class vlog_videoView extends VideoView{
    public vlog_videoView(Context context) {
        super(context);
        init(context);
    }

    public vlog_videoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public vlog_videoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
        int deviceWidth = displayMetrics.widthPixels;
        int deviceHeight = (int)(displayMetrics.heightPixels *0.65);
        setMeasuredDimension(deviceWidth, deviceHeight);
    }

}
