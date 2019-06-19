package com.skku.jiwon_hae.graduate_project_android_streaming.vlog;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.skku.jiwon_hae.graduate_project_android_streaming.R;
import com.skku.jiwon_hae.graduate_project_android_streaming.image.transform.CircleTransform;
import com.skku.jiwon_hae.graduate_project_android_streaming.main;
import com.skku.jiwon_hae.graduate_project_android_streaming.utils.useSharedPreference;
import com.skku.jiwon_hae.graduate_project_android_streaming.vlog.comments.add_comment_volley;
import com.skku.jiwon_hae.graduate_project_android_streaming.vlog.comments.comment_item;
import com.skku.jiwon_hae.graduate_project_android_streaming.vlog.comments.vlog_comment_adapter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class watch_vlog extends AppCompatActivity {

    //UserInformation
    private useSharedPreference useDB;
    private String userInformation;
    private String user_email;
    private String userName;

    private String vlog_registration;
    private vlog_videoView vlog_videoView;

    private ImageView uploader_profile_icon;
    private TextView uploaderName_textView;
    private TextView vlog_tags;

    private String uploaderName;
    private ImageButton back;

    private ScrollView vlog_layout;

    //Comments
    private ListView commentListView;
    private vlog_comment_adapter vlogCommentAdapter;
    private ImageButton add_comment;
    private EditText comment_content;
    private final int comment_child_height = 177;

    private TextView no_of_comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_vlog);
        getSupportActionBar().hide();

        useDB = new useSharedPreference(this);
        userInformation = useDB.get_user_information();

        try {
            JSONObject jsonObject = new JSONObject(userInformation);
            userName = jsonObject.get("userName").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Uri vlog_uri=Uri.parse(getIntent().getStringExtra("vlog_addr"));
        Log.d("VLOG", vlog_uri.toString());

        vlog_videoView = (vlog_videoView)findViewById(R.id.vlog_videoView);
        vlog_videoView.setVideoURI(vlog_uri);

        uploaderName = getIntent().getStringExtra("vlog_uploader");

        uploader_profile_icon = (ImageView)findViewById(R.id.uploader_profile_pic_icon);
        uploaderName_textView = (TextView)findViewById(R.id.uploaderName);
        vlog_tags = (TextView)findViewById(R.id.tags);

        Picasso.with(this)
                .load("http://13.125.170.236/streaming_application/profile_pic/" + uploaderName+ ".jpg")
                .transform(new CircleTransform())
                .placeholder(R.drawable.default_icon)
                .into(uploader_profile_icon);

        uploaderName_textView.setText(uploaderName);

        String tags = getIntent().getStringExtra("vlog_tag");

        if(!tags.replace(" ","").equals("")){
            vlog_tags.setText(tags);
            vlog_tags.setVisibility(View.VISIBLE);
        }

        back = (ImageButton)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        commentListView = (ListView)findViewById(R.id.vlog_comments_listView);
        commentListView.setEnabled(false);

        vlogCommentAdapter = new vlog_comment_adapter(this);
        commentListView.setAdapter(vlogCommentAdapter);

        comment_content = (EditText)findViewById(R.id.comment_content);
        add_comment = (ImageButton)findViewById(R.id.save_comment);
        add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!comment_content.getText().toString().replace(" ", "").equals("")){
                    addComments(userName, comment_content.getText().toString());
                }
            }
        });

        vlog_layout= (ScrollView)findViewById(R.id.vlog_layout);
        no_of_comments = (TextView)findViewById(R.id.no_of_comments);
    }

    @Override
    protected void onPause() {
        super.onPause();

        vlog_videoView.stopPlayback();
    }

    @Override
    protected void onResume() {
        super.onResume();

        vlog_registration = getIntent().getStringExtra("vlog_registration");
        vlog_videoView.start();
        vlog_videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        new get_all_comments().execute();
    }

    @Override
    public void onBackPressed() {
        Intent to_vlog = new Intent(watch_vlog.this, main.class);
        to_vlog.putExtra("intent_direction", "VLOG");
        startActivity(to_vlog);
    }

    public class get_all_comments extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... params) {
            String targetURL = "http://13.125.170.236/streaming_application/vlog/comment/vlog_getAll_comments.php?vlog_registration="+vlog_registration;

            try {
                URL url = new URL(targetURL);

                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String temp;
                StringBuilder stringBuilder = new StringBuilder();

                while((temp = bufferedReader.readLine())!= null){
                    stringBuilder.append(temp + "\n");
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return stringBuilder.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                if(result != null){
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    JSONObject jsonItem = new JSONObject(String.valueOf(jsonArray.get(0)));
                    JSONArray chatList = (JSONArray) jsonItem.get("comments");

                    for(int i = 0; i < chatList.length() ; i++){
                        JSONObject chat_jsonItem = new JSONObject(String.valueOf(chatList.get(i)));

                        comment_item item = new comment_item();
                        item.comment = String.valueOf(chat_jsonItem.get("comment"));
                        item.commenter = String.valueOf(chat_jsonItem.get("commenter"));
                        item.timeStamp = String.valueOf(chat_jsonItem.get("timeStamp"));

                        vlogCommentAdapter.add(vlogCommentAdapter.getCount(), item);
                    }

                    setListViewHeight(commentListView, comment_child_height * chatList.length());
                    no_of_comments.setText(String.valueOf(chatList.length()));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void addComments(String commenter, String comment){
        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);

                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        add_comment_volley add_comment = new add_comment_volley(vlog_registration, commenter, comment, String.valueOf(System.currentTimeMillis()),responseListener);
        RequestQueue queue = Volley.newRequestQueue(watch_vlog.this);
        queue.add(add_comment);

        comment_content.setText("");

        comment_item new_comment = new comment_item();
        new_comment.comment = comment;
        new_comment.commenter = commenter;
        new_comment.timeStamp = String.valueOf(System.currentTimeMillis());

        vlogCommentAdapter.add(vlogCommentAdapter.getCount(), new_comment);
        setListViewHeight(commentListView, comment_child_height);
        no_of_comments.setText(String.valueOf(Integer.parseInt(no_of_comments.getText().toString())+1));

        vlog_layout.post(new Runnable() {
            @Override
            public void run() {
                vlog_layout.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    public void setListViewHeight(ListView listView, int size){
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = size + listView.getHeight();
        listView.setLayoutParams(params);
        listView.requestLayout();
        listView.requestLayout();
    }


}
