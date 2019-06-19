package com.skku.jiwon_hae.graduate_project_android_streaming.vlog.vlog_adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.skku.jiwon_hae.graduate_project_android_streaming.R;
import com.skku.jiwon_hae.graduate_project_android_streaming.image.transform.CircleTransform;
import com.skku.jiwon_hae.graduate_project_android_streaming.vlog.watch_vlog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by jiwon_hae on 2017. 10. 30..
 */

public class vlog_adapter extends BaseAdapter {
    private Context mContext;
    private Activity mActivity;
    private ArrayList<vlog_items> vlog_lists;
    private LayoutInflater inflater;
    private String vlog_uri;
    private String vlog_TAG;
    private String vlog_comments;

    public vlog_adapter(final Context mContext, Activity mActivity){
        this.mContext = mContext;
        this.inflater = LayoutInflater.from(mContext);
        this.mActivity = mActivity;
        vlog_lists = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return vlog_lists.size();
    }

    @Override
    public Object getItem(int position) {
        return vlog_lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class Viewholder {
        ImageView vlog_preview_image;
        ImageView vlog_uploader_image;
        TextView like_textView;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.vlog_items, null);

        Viewholder h = new Viewholder();
        h.vlog_preview_image = (ImageView)convertView.findViewById(R.id.vlog_preview);
        h.vlog_uploader_image = (ImageView)convertView.findViewById(R.id.vlog_uploader_profile);
        h.like_textView = (TextView)convertView.findViewById(R.id.vlog_likes);

        final String vlog_path="http://13.125.170.236/streaming_application/vlog/video/" + vlog_lists.get(position).vlogAddr;

        Glide.with(mContext)
                .load("http://13.125.170.236/streaming_application/vlog/thumbnail/"+vlog_lists.get(position).registration_code+".jpg")
                .thumbnail(0.7f)
                .into(h.vlog_preview_image);

        Picasso.with(mContext)
                .load("http://13.125.170.236/streaming_application/profile_pic/" + vlog_lists.get(position).vlog_userName+ ".jpg")
                .transform(new CircleTransform())
                .placeholder(R.drawable.default_icon)
                .into(h.vlog_uploader_image);

        h.like_textView.setText(String.valueOf(vlog_lists.get(position).likes));

        LinearLayout item_layout = (LinearLayout)convertView.findViewById(R.id.item_layout);
        item_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent to_watch_vlog = new Intent(mContext, watch_vlog.class);
                to_watch_vlog.putExtra("vlog_addr", vlog_path);
                to_watch_vlog.putExtra("vlog_tag", vlog_lists.get(position).tags);
                to_watch_vlog.putExtra("vlog_likes", vlog_lists.get(position).likes);
                to_watch_vlog.putExtra("vlog_uploader", vlog_lists.get(position).vlog_userName);
                to_watch_vlog.putExtra("vlog_registration", vlog_lists.get(position).registration_code);
                mActivity.startActivity(to_watch_vlog);
            }
        });

        return convertView;
    }

    public void add(int position, vlog_items item){
        vlog_lists.add(position, item);
        notifyDataSetChanged();
    }
}
