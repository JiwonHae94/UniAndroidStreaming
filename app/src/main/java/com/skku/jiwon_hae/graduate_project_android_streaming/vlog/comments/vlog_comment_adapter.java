package com.skku.jiwon_hae.graduate_project_android_streaming.vlog.comments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.skku.jiwon_hae.graduate_project_android_streaming.R;
import com.skku.jiwon_hae.graduate_project_android_streaming.image.transform.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by jiwon_hae on 2017. 11. 14..
 */

public class vlog_comment_adapter extends BaseAdapter {
    private ArrayList<comment_item> comments;
    private LayoutInflater inflater;
    private Context mContext;

    public vlog_comment_adapter(Context context){
        this.mContext = context;
        this.inflater = LayoutInflater.from(mContext);
        this.comments = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.vlog_comment_item, null);

        ImageView commenter_image = (ImageView)convertView.findViewById(R.id.commenter_profile_pic);
        TextView commenterName = (TextView)convertView.findViewById(R.id.commenter_name);
        TextView comment = (TextView)convertView.findViewById(R.id.comment);

        commenterName.setText(comments.get(position).commenter);
        comment.setText(comments.get(position).comment);

        Picasso.with(mContext)
                .load("http://13.125.170.236/streaming_application/profile_pic/"+ comments.get(position).commenter+ ".jpg")
                .transform(new CircleTransform())
                .placeholder(R.drawable.default_icon)
                .into(commenter_image);

        return convertView;
    }

    public void add(int position, comment_item item){
        comments.add(position, item);
        notifyDataSetChanged();
    }


}
