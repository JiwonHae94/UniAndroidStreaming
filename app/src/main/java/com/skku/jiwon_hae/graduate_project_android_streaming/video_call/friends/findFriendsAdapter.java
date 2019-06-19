package com.skku.jiwon_hae.graduate_project_android_streaming.video_call.friends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.skku.jiwon_hae.graduate_project_android_streaming.R;
import com.skku.jiwon_hae.graduate_project_android_streaming.image.transform.CircleTransform;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jiwon_hae on 2017. 11. 27..
 */

public class findFriendsAdapter extends BaseAdapter{
    private Context mContext;
    private ArrayList<userInformation> dataList;
    private LayoutInflater layoutInflater;
    private String userName;

    public findFriendsAdapter(Context mContext, ArrayList friendsList, String userName){
        this.mContext = mContext;
        this.dataList = friendsList;
        this.layoutInflater = LayoutInflater.from(mContext);
        this.userName = userName;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class Viewholder {
        ImageView userProfile;
        TextView userName;
        TextView userEmail;
        Button addFriend_btn;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.add_friend_child, null);

        Viewholder h = new Viewholder();
        h.userProfile = (ImageView)convertView.findViewById(R.id.friend_profile_image);
        h.userName = (TextView)convertView.findViewById(R.id.add_friend_name);
        h.userEmail = (TextView)convertView.findViewById(R.id.add_friend_email);
        h.addFriend_btn = (Button)convertView.findViewById(R.id.add_friend_btn);

        Picasso.with(mContext)
                .load("http://bdc01051.cafe24.com/profile_pic/" + dataList.get(position).userName+ ".jpg")
                .fit()
                .placeholder(R.drawable.default_icon)
                .transform(new CircleTransform())
                .into(h.userProfile);

        h.userName.setText(dataList.get(position).userName);
        h.userEmail.setText(dataList.get(position).userID);

        h.addFriend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListener = new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject result = new JSONObject(response);
                            boolean result_output = result.getBoolean("success");

                            if(result_output){
                                Toast.makeText(mContext, dataList.get(position).userName + "를 추가했습니다", Toast.LENGTH_SHORT).show();
                            } else{
                                Toast.makeText(mContext, "이미 추가된 친구입니다", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                add_to_friends add_friends = new add_to_friends(userName,dataList.get(position).userName , responseListener);
                RequestQueue queue = Volley.newRequestQueue(mContext);
                queue.add(add_friends);
            }
        });

        return convertView;
    }

    public void add(userInformation user){
        dataList.add(0, user);
        notifyDataSetChanged();
    }

    public void clear(){
        dataList.clear();
        notifyDataSetChanged();
    }

}
