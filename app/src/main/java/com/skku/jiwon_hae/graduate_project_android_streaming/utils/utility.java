package com.skku.jiwon_hae.graduate_project_android_streaming.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by jiwon_hae on 2017. 11. 23..
 */

public class utility {
    private Context context;

    public utility(Context mContext){
        this.context = mContext;
    }

    public void showToast(String word){
        Toast.makeText(context, word, Toast.LENGTH_SHORT).show();
    }
}
