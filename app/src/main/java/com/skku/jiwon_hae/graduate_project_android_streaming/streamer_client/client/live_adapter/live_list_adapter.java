package com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.client.live_adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.skku.jiwon_hae.graduate_project_android_streaming.R;
import com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.client.live_stream;

import java.util.ArrayList;

/**
 * Created by jiwon_hae on 2017. 10. 11..
 */

public class live_list_adapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private Context mContext;

    public ArrayList<live_list_items> live_lists;

    public live_list_adapter(Context context, ArrayList<live_list_items> live){
        this.layoutInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.live_lists = live;
    }

    @Override
    public int getCount() {
        return live_lists.size();
    }

    @Override
    public Object getItem(int position) {
        return live_lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class Viewholder {
        ImageView stream_cover_page;
        ImageView stream_viewer_icon;
        TextView stream_title;
        TextView streamer_name;
        TextView stream_tags;
        TextView stream_viewer_count;
        RelativeLayout stream;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.listview_live_list, null);

        Viewholder h = new Viewholder();

        h.stream = (RelativeLayout)convertView.findViewById(R.id.stream);
        h.stream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent to_live = new Intent(parent.getContext(), live_stream.class);
                String stream_url = "http://bdc01051.cafe24.com/streaming/" + live_lists.get(position).streamer + "/index.m3u8";

                to_live.putExtra("live_url", stream_url);
                to_live.putExtra("streamer", live_lists.get(position).streamer);
                to_live.putExtra("stream_title", live_lists.get(position).stream_title);

                Log.e("streamer", live_lists.get(position).streamer);

                parent.getContext().startActivity(to_live);
            }
        });

        // Stream info
        h.stream_cover_page = (ImageView)convertView.findViewById(R.id.streamer_cover_image);
        h.streamer_name = (TextView)convertView.findViewById(R.id.streamer_name);
        h.stream_title = (TextView)convertView.findViewById(R.id.stream_title);
        h.stream_tags = (TextView)convertView.findViewById(R.id.stream_tags);

        Glide.with(parent.getContext())
                .load("http://bdc01051.cafe24.com/profile_pic/" + live_lists.get(position).streamer+ ".jpg")
                .placeholder(R.drawable.default_icon)
                .into(h.stream_cover_page);

        setText(h.stream_title, live_lists.get(position).stream_title);
        setText(h.stream_tags, live_lists.get(position).stream_tags);
        setText(h.streamer_name, live_lists.get(position).streamer);

        // Viewer Count
        h.stream_viewer_icon = (ImageView)convertView.findViewById(R.id.streamer_viewer_count_icon);
        h.stream_viewer_count = (TextView)convertView.findViewById(R.id.streamer_viewer_count);

        Glide.with(parent.getContext())
                .load(R.drawable.viewer_icon)
                .thumbnail(0.08f)
                .into(h.stream_viewer_icon);

        h.stream_viewer_count.setText(String.valueOf(live_lists.get(position).stream_current_viewer));

        return convertView;
    }

    public void setText(TextView textView, String text){
        if(text.equals("")){
            textView.setVisibility(View.GONE);
        }else{
            if(textView.getVisibility()==View.GONE){
                textView.setVisibility(View.VISIBLE);
            }
            textView.setText(text);
        }
    }

    public void clear(){
        for(int i =0; i<live_lists.size();i++){
            live_lists.remove(i);
        }
        notifyDataSetChanged();
    }


    public void add(int position, live_list_items item){
        live_lists.add(position, item);
        notifyDataSetChanged();
    }
}
