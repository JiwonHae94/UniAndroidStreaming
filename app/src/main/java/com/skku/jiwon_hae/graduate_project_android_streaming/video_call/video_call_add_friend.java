package com.skku.jiwon_hae.graduate_project_android_streaming.video_call;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.skku.jiwon_hae.graduate_project_android_streaming.R;
import com.skku.jiwon_hae.graduate_project_android_streaming.main;
import com.skku.jiwon_hae.graduate_project_android_streaming.utils.useSharedPreference;
import com.skku.jiwon_hae.graduate_project_android_streaming.video_call.friends.findFriends;
import com.skku.jiwon_hae.graduate_project_android_streaming.video_call.friends.findFriendsAdapter;
import com.skku.jiwon_hae.graduate_project_android_streaming.video_call.friends.userInformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class video_call_add_friend extends AppCompatActivity {

    private SearchView searchView;
    private ArrayList<userInformation> friendList;
    private findFriendsAdapter fAdapter;
    private ListView listView;
    private TextView current_userId;
    private String userInformation;
    private String userName;
    private String user_email;
    private ImageView back_btn;

    private useSharedPreference useDB = new useSharedPreference(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call_add_friend);
        getSupportActionBar().hide();

        searchView = (SearchView)findViewById(R.id.search_friends);
        listView = (ListView)findViewById(R.id.friendList);
        current_userId = (TextView)findViewById(R.id.video_call_current_userId);

        userInformation = useDB.get_user_information();
        try {
            JSONObject jsonObject = new JSONObject(userInformation);
            userName = jsonObject.get("userName").toString();
            user_email = jsonObject.get("user_id").toString();

            current_userId.setText("내 아이디: "+ userName);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        friendList = new ArrayList<>();
        searchView.setIconified(true);

        if(!searchView.isIconified()){
            searchView.onActionViewCollapsed();
        }

        fAdapter = new findFriendsAdapter(this, friendList, user_email);
        listView.setAdapter(fAdapter);

        searchView.setOnSearchClickListener(new SearchView.OnClickListener()  {
            @Override
            public void onClick(View v) {
                searchView.setBackgroundColor(Color.WHITE);
            }
        });

        searchView.setOnCloseListener(new android.support.v7.widget.SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                fAdapter.clear();
                current_userId.setVisibility(View.VISIBLE);
                return true;
            }
        });

        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fAdapter.clear();
                current_userId.setVisibility(View.GONE);

                if(query.length() > 0){
                    Response.Listener<String> responseListener = new Response.Listener<String>(){
                        @Override
                        public void onResponse(String response) {
                            try{
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = new JSONArray(jsonObject.getString("response"));

                                for(int i =0 ; i < jsonArray.length(); i++){
                                    JSONObject friend = new JSONObject(String.valueOf(jsonArray.get(i)));

                                    if(!friend.get("userEmail").equals("null") && !friend.get("userName").equals("null")){
                                        userInformation user_info = new userInformation();
                                        user_info.userID = String.valueOf(friend.get("userEmail"));
                                        user_info.userName = String.valueOf(friend.get("userName"));

                                        fAdapter.add(user_info);
                                    }
                                }

                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    findFriends Validate = new findFriends(user_email, userName, query, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(video_call_add_friend.this);
                    queue.add(Validate);
                }else{
                    fAdapter.clear();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fAdapter.clear();

                if(newText.length() == 0){
                    current_userId.setVisibility(View.VISIBLE);
                }else{
                    current_userId.setVisibility(View.GONE);
                }
                return true;
            }
        });

        EditText searchEditText = (EditText)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(Color.BLACK);
        searchEditText.setHintTextColor(Color.LTGRAY);
        searchEditText.setTextSize(13);
        searchEditText.setMaxHeight(13);
        searchEditText.setGravity(Gravity.CENTER_VERTICAL);
        searchEditText.setHint("아이디 검색...");

        back_btn = (ImageView)findViewById(R.id.video_call_back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent to_main = new Intent(video_call_add_friend.this, main.class);
                startActivity(to_main);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent to_main = new Intent(video_call_add_friend.this, main.class);
        startActivity(to_main);
    }
}
