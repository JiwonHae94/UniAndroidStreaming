package com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.streamer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.skku.jiwon_hae.graduate_project_android_streaming.R;
import com.skku.jiwon_hae.graduate_project_android_streaming.image.camera.camera_setting;
import com.skku.jiwon_hae.graduate_project_android_streaming.image.imageSelection;
import com.skku.jiwon_hae.graduate_project_android_streaming.image.transform.CircleTransform;
import com.skku.jiwon_hae.graduate_project_android_streaming.main;
import com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.chatting.chat_client;
import com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.chatting.chatting_adapter;
import com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.polling.polling_register_volley;
import com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.streamer.endStreaming.endStreaming;
import com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.streamer.startStreaming.startStreaming;
import com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.util.get_like;
import com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.util.send_like;
import com.skku.jiwon_hae.graduate_project_android_streaming.utils.useSharedPreference;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.MPPointF;
import com.pedro.rtplibrary.rtmp.RtmpCamera2;
import com.squareup.picasso.Picasso;

import net.ossrs.rtmp.ConnectCheckerRtmp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class streaming extends AppCompatActivity implements ConnectCheckerRtmp{

    private TextureView mTextureView;
    private camera_setting streaming_camera_setting;
    private Button startLive;
    private ImageButton streaming_cancel;
    private ImageButton stop_streaming;

    private EditText stream_title;
    private EditText stream_tags;

    private Chronometer streaming_timer;

    private LinearLayout streaming_content;
    private LinearLayout streaming_whenLive;

    private MediaRecorder mediaRecorder;

    //Streaming camera;
    private RtmpCamera2 rtmpCamera2;
    //private streaming_display rtmpDisplay;

    //Permission
    private static final int REQUEST_PERMISSION = 9000;

    //RTMP streaming URL

    //UserInformation
    private useSharedPreference useDB;
    private String userInformation;
    private String user_email;
    private String userName;

    private String RTMP_URL;

    private boolean onLive = false;
    private final int REQUEST_CODE = 1;

    //Profile pic
    private ImageButton streamer_profile_pic_icon;
    private ImageView streamer_profile_pic;

    private ImageButton chat_appear;
    private ImageButton poll_appear;
    private LinearLayout layout_appear_whenLive;
    private LinearLayout layout_appear_chatting;
    private EditText chat_content;
    private Button chat_submit;

    //when Streaming
    private TextView streaming_streamerId;
    private ImageView streaming_streamerImage;
    private TextView streaming_title_whenLive;

    //Chatting
    private chat_client Chatting;
    private RelativeLayout ChattingLayout;

    //Translation
    private FrameLayout translation_options;
    private TextView current_language_option;
    private ImageView translation_icon;

    //Polling
    private boolean polling_available = false;
    private TextView poll_badge;

    //Like
    private ImageView heartAnimation;
    private TextView no_of_likes;
    private ToggleButton send_like_btn;
    private boolean has_liked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_streaming);

        useDB = new useSharedPreference(this);
        userInformation = useDB.get_user_information();

        try {
            JSONObject jsonObject = new JSONObject(userInformation);
            userName = jsonObject.get("userName").toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //streaming_camera_setting = new camera_setting(this, "STREAMING");


        //streaming_camera_setting.setTextureView(mTextureView);

        startLive = (Button)findViewById(R.id.streaming_startLive);
        startLive.setClickable(false);
        streaming_content = (LinearLayout) findViewById(R.id.streaming_content);

        streaming_cancel = (ImageButton) findViewById(R.id.streaming_cancel);
        streaming_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent to_main = new Intent(streaming.this, main.class);
                finish();
                startActivity(to_main);
            }
        });
        /*
        streaming_camera_setting.createVideoFolder();
        mediaRecorder = new MediaRecorder();

        streaming_camera_setting.setmMediaRecorder(mediaRecorder);
*/
        streaming_whenLive = (LinearLayout)findViewById(R.id.stremaing_layout_whenLive);
        streaming_timer = (Chronometer)findViewById(R.id.streaming_timer);
        //streaming_camera_setting.setChronometer(streaming_timer);

        //rtmpDisplay = new streaming_display(this, this);
        //rtmpCamera2.startPreview();

        stream_title = (EditText)findViewById(R.id.streaming_title);
        stream_tags = (EditText)findViewById(R.id.streaming_tag);

        streaming_streamerId = (TextView)findViewById(R.id.streaming_streamerId);
        streaming_streamerImage = (ImageView)findViewById(R.id.streamer_profile_pic_when_streaming);
        streaming_title_whenLive = (TextView)findViewById(R.id.streaming_title_when_live);

        //LIKES
        no_of_likes = (TextView)findViewById(R.id.stream_no_of_likes);
        send_like_btn = (ToggleButton)findViewById(R.id.stream_like);

        RTMP_URL = "rtmp://13.125.170.236:8712/hls/" + userName;
        startStreaming();
        stopStreaming();

        streamer_profile_pic = (ImageView)findViewById(R.id.streamer_profile_pic);
        streamer_profile_pic_icon = (ImageButton)findViewById(R.id.streaming_profile_pic_icon);

        if(getIntent().hasExtra("image")) {
            Picasso.with(this)
                    .load(getIntent().getStringExtra("image"))
                    .fit()
                    .transform(new CircleTransform())
                    .into(streamer_profile_pic_icon);

            Glide.with(this)
                    .load(getIntent().getStringExtra("image"))
                    .into(streamer_profile_pic);
        }else{
            Picasso.with(this)
                    .load("http://ec2-13-125-170-236.ap-northeast-2.compute.amazonaws.com/streaming_application/profile_pic/"+userName + ".jpg")
                    .fit()
                    .placeholder(R.drawable.default_icon)
                    .skipMemoryCache()
                    .transform(new CircleTransform())
                    .into(streamer_profile_pic_icon);

            Glide.with(this)
                    .load("http://ec2-13-125-170-236.ap-northeast-2.compute.amazonaws.com/streaming_application/profile_pic/"+userName + ".jpg")
                    .thumbnail(0.8f)
                    .placeholder(R.drawable.default_icon)
                    .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                    .into(streamer_profile_pic);
        }

        streamer_profile_pic_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent to_capture_preview = new Intent(streaming.this, imageSelection.class);
                to_capture_preview.putExtra("TAG", "STREAMING");
                to_capture_preview.putExtra("userName", userName);
                startActivity(to_capture_preview);
            }
        });

        layout_appear_whenLive = (LinearLayout)findViewById(R.id.stremaing_layout_whenLive);
        layout_appear_chatting = (LinearLayout)findViewById(R.id.chat_input_layout);

        //Chatting
        ListView chattingListView = (ListView)findViewById(R.id.chatting_listview);

        Chatting = new chat_client(this, chattingListView, userName, "streamer");
        ChattingLayout = (RelativeLayout)findViewById(R.id.chatting_linearLayout);

        chat_appear = (ImageButton)findViewById(R.id.chat_appear);
        poll_appear = (ImageButton)findViewById(R.id.poll_appear);
        chat_content = (EditText)findViewById(R.id.chat_content);

        //Chatting
        Glide.with(this)
                .load(R.drawable.chat_icon)
                .thumbnail(0.1f)
                .into(chat_appear);

        chat_appear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layout_appear_chatting.getVisibility() == View.GONE){
                    layout_appear_chatting.setVisibility(View.VISIBLE);

                }else if(layout_appear_chatting.getVisibility() == View.VISIBLE){
                    layout_appear_chatting.setVisibility(View.GONE);
                }
            }
        });

        chat_submit = (Button)findViewById(R.id.chat_submit_btn);
        chat_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = chat_content.getText().toString();
                Chatting.send(message, "message", userName, userName);
                chat_content.setText("");
            }
        });

        //Polling
        Glide.with(this)
                .load(R.drawable.polling_icon)
                .thumbnail(0.1f)
                .into(poll_appear);

        poll_badge = (TextView)findViewById(R.id.badge_textView);
        poll_appear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new get_vote_result().execute();
            }
        });

        //Translation
        translation_options = (FrameLayout)findViewById(R.id.translation);
        current_language_option = (TextView)findViewById(R.id.current_lang_option);
        translation_icon = (ImageView)findViewById(R.id.translation_icon);
        translation_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(streaming.this, translation_options);
                popupMenu.getMenuInflater().inflate(R.menu.language_options, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if(item.getTitle().equals("X")){
                            translation_icon.setVisibility(View.VISIBLE);
                            current_language_option.setVisibility(View.GONE);
                            Chatting.cAdapter.setLanguageOption("NULL");
                        }else{
                            current_language_option.setVisibility(View.VISIBLE);
                            translation_icon.setVisibility(View.GONE);
                            current_language_option.setText(item.getTitle());

                            Chatting.setSrcLanague(item.getTitle().toString());
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

    }

    public chatting_adapter getChattingAdpater(){
        //return cAdapter;
        return null;
    }

    @Override
    protected void onResume() {

        //startStreaming();

        super.onResume();


        /*
        streaming_camera_setting.startBackgroundThread();

        if(mTextureView.isAvailable()){
            streaming_camera_setting.setUpCamera(mTextureView.getWidth(), mTextureView.getHeight());
            streaming_camera_setting.connectCamera();
        }else{
            mTextureView.setSurfaceTextureListener(streaming_camera_setting.mSurfaceTextureListener);
        }
        */
    }

    @Override
    protected void onPause() {
        if(onLive){
            rtmpCamera2.stopStream();
            rtmpCamera2.stopPreview();
            Chatting.stopClient();
        }

        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_PERMISSION){
            if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                Toast.makeText(getApplicationContext(), "방송을 하기 위해선, 카메라 접근을 허락하셔야합니다", Toast.LENGTH_SHORT).show();
                startLive.setClickable(false);
            }else{
                startLive.setClickable(true);
            }

            if(grantResults[1] == PackageManager.PERMISSION_DENIED){
                Toast.makeText(getApplicationContext(), "방송을 하기 위해선, 오디오 접근을 허락하셔야합니다", Toast.LENGTH_SHORT).show();
                startLive.setClickable(false);
            }else{
                startLive.setClickable(true);
            }

            if(grantResults[2] == PackageManager.PERMISSION_DENIED){
                Toast.makeText(getApplicationContext(), "방송을 하기 위해선, 외부파일 접근을 허락하셔야합니다", Toast.LENGTH_SHORT).show();
                startLive.setClickable(false);
            }else{
                startLive.setClickable(true);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*

        if (requestCode == REQUEST_CODE) {
            if (rtmpDisplay.prepareAudio() && rtmpDisplay.prepareVideo()){
                if (Build.VERSION.SDK_INT >= 21) {
                    rtmpDisplay.startStream(RTMP_URL, resultCode, data);
                }
            } else {
                Toast.makeText(this, "Error preparing stream, This device cant do it", Toast.LENGTH_SHORT)
                        .show();
            }
        }
        */
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        if(layout_appear_chatting.getVisibility() == View.VISIBLE){
            layout_appear_chatting.setVisibility(View.GONE);
        }else{
            Intent to_main = new Intent(streaming.this, main.class);

            to_main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(to_main);
            finish();
        }
    }

    @Override
    public void onConnectionSuccessRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("Connectoin ", "success");
            }
        });

    }

    @Override
    public void onConnectionFailedRtmp(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("Connectoin failed ", s);
            }
        });
    }


    @Override
    public void onDisconnectRtmp() {

    }

    @Override
    public void onAuthErrorRtmp() {

    }

    @Override
    public void onAuthSuccessRtmp() {

    }

    //Polling
    public void createPolling(){
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.create_polling_layout, null);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setView(view);

        final EditText polling_title = (EditText)view.findViewById(R.id.polling_title);

        final EditText poll_yes = (EditText)view.findViewById(R.id.polling_yes);

        int[] color_yes = {getResources().getColor(R.color.poll_color1),getResources().getColor(R.color.poll_color2)};
        float[] position_yes = {0, 1};
        Shader.TileMode tile_mode_yes =  Shader.TileMode.REPEAT;
        LinearGradient lin_grad_yes = new LinearGradient(0, 0, 200, 0,color_yes, position_yes, tile_mode_yes);
        Shader shader_gradient_yes = lin_grad_yes;
        poll_yes.getPaint().setShader(shader_gradient_yes);

        final EditText poll_no = (EditText) view.findViewById(R.id.polling_no);

        int[] color_no = {getResources().getColor(R.color.poll_color3),getResources().getColor(R.color.poll_color4)};
        float[] position_no = {0, 1};
        Shader.TileMode tile_mode_no = Shader.TileMode.REPEAT;
        LinearGradient lin_grad_no = new LinearGradient(0, 0, 200, 0,color_no, position_no, tile_mode_no);
        Shader shader_gradient_no = lin_grad_no;
        poll_no.getPaint().setShader(shader_gradient_no);

        builder.setPositiveButton("만들기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final JSONObject polling_obj = new JSONObject();

                Response.Listener<String> responseListener = new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");

                            if(success){
                                polling_available = true;
                                poll_badge.setVisibility(View.VISIBLE);

                                JSONObject polling_msg = new JSONObject();

                                polling_msg.put("poll_title", polling_title.getText().toString());
                                polling_msg.put("option1", poll_yes.getText().toString());
                                polling_msg.put("option2", poll_no.getText().toString());

                                Chatting.send(polling_msg.toString(), "polling_made", userName, userName);

                            }else{
                                Toast.makeText(streaming.this, "다시 시도해주세요", Toast.LENGTH_SHORT).show();
                            }

                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                if(polling_title.getText().toString() == null){
                    Toast.makeText(streaming.this, "질문을 적어주세요", Toast.LENGTH_SHORT).show();
                }else{

                    try {
                        polling_obj.put("option1", poll_yes.getText().toString());
                        polling_obj.put("option2", poll_no.getText().toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    polling_register_volley create_poll = new polling_register_volley(userName, polling_title.getText().toString(), polling_obj.toString(), responseListener);
                    RequestQueue queue = Volley.newRequestQueue(streaming.this);
                    queue.add(create_poll);
                }
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(streaming.this, "cancel", Toast.LENGTH_SHORT).show();
            }
        });

        android.support.v7.app.AlertDialog create_polling = builder.create();
        create_polling.setCanceledOnTouchOutside(false);
        create_polling.show();
        create_polling.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void showPolling_result(){
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.polling_result_layout, null);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setView(view);

        TextView title = new TextView(streaming.this);
        title.setText("Polling");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setBackgroundColor(Color.parseColor("#f68588"));
        title.setTextColor(Color.WHITE);
        title.setTextSize(18);
        builder.setCustomTitle(title);

        android.support.v7.app.AlertDialog create_polling = builder.create();
        create_polling.setCanceledOnTouchOutside(true);
        create_polling.show();
        create_polling.getWindow().setBackgroundDrawableResource(android.R.color.white);
    }

    public class get_vote_result extends AsyncTask<Object, Object, String> {
        private boolean has_poll = false;
        private ArrayList<PieEntry> entries = new ArrayList<>();

        @Override
        protected String doInBackground(Object... params) {
            String targetURL = "http://13.125.170.236/streaming_application/redis/polling/get_polling_result.php?streamer=" + userName;

            try {
                URL url = new URL(targetURL);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String temp;
                StringBuilder stringBuilder = new StringBuilder();

                while ((temp = bufferedReader.readLine()) != null) {
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
                JSONObject jsonObject = new JSONObject(result);
                final JSONObject poll_item = new JSONObject(jsonObject.get("response").toString());

                if (poll_item.has("option1")) {
                    has_poll = true;
                }

                if (has_poll) {
                    float entry1 = 0;
                    float entry2 = 0;

                    Float op1 = (float) 0;
                    Float op2 = (float) 0;

                    int total = Integer.parseInt(poll_item.get("option1_result").toString()) + Integer.parseInt(poll_item.get("option2_result").toString());

                    if (Integer.parseInt(poll_item.get("option1_result").toString()) != 0) {
                        entry1 = (Float.parseFloat(poll_item.get("option1_result").toString()) / total) * 100;
                        op1 = Float.parseFloat(String.format("%.1f", entry1));
                    }

                    if (Integer.parseInt(poll_item.get("option2_result").toString()) != 0) {
                        entry2 = (Float.parseFloat(poll_item.get("option2_result").toString()) / total) * 100;
                        op2 = Float.parseFloat(String.format("%.1f", entry2));
                    }

                    entries.add(new PieEntry(op1, poll_item.get("option1")));
                    entries.add(new PieEntry(op2, poll_item.get("option2")));

                    poll_appear.setEnabled(true);
                    poll_badge.setVisibility(View.VISIBLE);

                    showPolling_result(poll_item.toString(), entries);

                } else{
                    createPolling();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void showPolling_result(String data_set, ArrayList poll_result) throws JSONException {
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.polling_result_layout_streamer, null);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setView(view);

        JSONObject jsonObject = new JSONObject(data_set);

        final TextView polling_title = (TextView)view.findViewById(R.id.polling_title);
        polling_title.setText(jsonObject.getString("poll_title"));

        final TextView poll_yes = (TextView)view.findViewById(R.id.polling_yes);
        poll_yes.setText(jsonObject.getString("option1"));

        int[] color_yes = {getResources().getColor(R.color.poll_color1),getResources().getColor(R.color.poll_color2)};
        float[] position_yes = {0, 1};
        Shader.TileMode tile_mode_yes =  Shader.TileMode.REPEAT;
        LinearGradient lin_grad_yes = new LinearGradient(0, 0, 200, 0,color_yes, position_yes, tile_mode_yes);
        Shader shader_gradient_yes = lin_grad_yes;
        poll_yes.getPaint().setShader(shader_gradient_yes);

        final TextView poll_no = (TextView) view.findViewById(R.id.polling_no);
        poll_no.setText(jsonObject.getString("option2"));

        int[] color_no = {getResources().getColor(R.color.poll_color3),getResources().getColor(R.color.poll_color4)};
        float[] position_no = {0, 1};
        Shader.TileMode tile_mode_no = Shader.TileMode.REPEAT;
        LinearGradient lin_grad_no = new LinearGradient(0, 0, 200, 0,color_no, position_no, tile_mode_no);
        Shader shader_gradient_no = lin_grad_no;
        poll_no.getPaint().setShader(shader_gradient_no);

        if(jsonObject.has("option1_result") && jsonObject.has("option2_result")){
            int total = Integer.parseInt(jsonObject.get("option2_result").toString()) + Integer.parseInt(jsonObject.get("option1_result").toString());

            if(Integer.parseInt(jsonObject.get("option1_result").toString()) != 0){
                float percentage_yes = Float.parseFloat(jsonObject.get("option1_result").toString()) / total;
                String p_yes= String.format("%.1f", percentage_yes*100);

                poll_yes.setText(jsonObject.getString("option1") + "\n" + p_yes + "%");
                LinearLayout.LayoutParams param_yes = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        Float.parseFloat(jsonObject.get("option1_result").toString())
                );

                poll_yes.setLayoutParams(param_yes);

            }

            if(Integer.parseInt(jsonObject.get("option2_result").toString()) != 0){
                float percentage_no = Float.parseFloat(jsonObject.get("option2_result").toString()) / total;

                String p_no= String.format("%.1f", percentage_no*100);
                poll_no.setText(jsonObject.getString("option2") + "\n" + p_no + "%");

                LinearLayout.LayoutParams param_no = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        Float.parseFloat(jsonObject.get("option2_result").toString())
                );

                poll_no.setLayoutParams(param_no);
            }
        }

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        //PieChart
        PieChart mChart = (PieChart)view.findViewById(R.id.poll_result_pieChart);
        PieDataSet dataSet = new PieDataSet(poll_result, "Poll Results");

        if(dataSet.getEntryForIndex(0).getY() == (float)0 && dataSet.getEntryForIndex(1).getY() == (float)0){
            mChart.setVisibility(View.GONE);
        }else{
            mChart.setVisibility(View.VISIBLE);
        }

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        int[] colors = {getResources().getColor(R.color.white),getResources().getColor(R.color.poll_color4)};
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(13f);
        data.setValueTextColor(Color.BLACK);

        mChart.setData(data);
        mChart.highlightValues(null);

        mChart.invalidate();

        // entry label styling
        mChart.setEntryLabelColor(Color.WHITE);
        mChart.setEntryLabelTextSize(12f);
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(getResources().getColor(R.color.transparent));
        mChart.getLegend().setEnabled(false);

        mChart.setHoleRadius(70f);
        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        android.support.v7.app.AlertDialog create_polling = builder.create();
        create_polling.setCanceledOnTouchOutside(true);
        create_polling.show();
        create_polling.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void mSendLike(String like){
        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {

            }
        };

        send_like stream_send_like = new send_like(userName, like, responseListener);
        RequestQueue queue = Volley.newRequestQueue(streaming.this);
        queue.add(stream_send_like);
    }

    public void startStreaming(){

        mTextureView = (TextureView)findViewById(R.id.streaming_textureView);
        rtmpCamera2 = new RtmpCamera2(mTextureView, this);


        startLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLive = true;

                if (rtmpCamera2.prepareAudio() && rtmpCamera2.prepareVideo(640, 480, 30, 1200 * 1024, true, 270)) {
                    rtmpCamera2.startStream(RTMP_URL);
                    rtmpCamera2.switchCamera();
                    streaming_timer.start();

                    Response.Listener<String> responseListener = new Response.Listener<String>(){
                        @Override
                        public void onResponse(String response) {
                            try{
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");

                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    startStreaming streaming_start = new startStreaming(userName, stream_title.getText().toString(), stream_tags.getText().toString(), responseListener);

                    RequestQueue queue = Volley.newRequestQueue(streaming.this);
                    queue.add(streaming_start);
                }

                Chatting.startClient();

                streaming_streamerId.setText(userName);

                Picasso.with(streaming.this)
                        .load("http://ec2-13-125-170-236.ap-northeast-2.compute.amazonaws.com/streaming_application/profile_pic/" + userName+ ".jpg")
                        .fit()
                        .placeholder(R.drawable.default_icon)
                        .transform(new CircleTransform())
                        .into(streaming_streamerImage);

                if(stream_title.getText().toString().equals("")){
                    streaming_title_whenLive.setVisibility(View.GONE);
                }else{
                    streaming_title_whenLive.setText(stream_title.getText().toString());
                }

                Response.Listener<String> responseListener = new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            Log.e("jsonObject likes :", jsonObject.toString());
                            String likes = jsonObject.get("likes").toString();

                            no_of_likes.setText(likes);

                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                get_like get_like = new get_like(userName, responseListener);
                RequestQueue queue = Volley.newRequestQueue(streaming.this);
                queue.add(get_like);

                send_like_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            has_liked = true;
                            no_of_likes.setText(String.valueOf(Integer.parseInt(no_of_likes.getText().toString()) + 1));
                            mSendLike("like");

                        }else if(!isChecked && has_liked){
                            has_liked = false;
                            no_of_likes.setText(String.valueOf(Integer.parseInt(no_of_likes.getText().toString()) -1));
                            mSendLike("dislike");
                        }
                    }
                });

                ChattingLayout.setVisibility(View.VISIBLE);
                streaming_content.setVisibility(View.INVISIBLE);
                streaming_whenLive.setVisibility(View.VISIBLE);
                streamer_profile_pic.setVisibility(View.INVISIBLE);

            }
        });
    }

    public void stopStreaming(){
        stop_streaming = (ImageButton)findViewById(R.id.streaming_stop_streaming);
        stop_streaming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chatting.send("logout", "logout", user_email, userName);
                rtmpCamera2.stopStream();
                rtmpCamera2.stopPreview();

                onLive = false;

                Response.Listener<String> responseListener = new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                    }
                };

                endStreaming end_stream = new endStreaming(userName, responseListener);
                RequestQueue queue = Volley.newRequestQueue(streaming.this);
                queue.add(end_stream);

                Intent streaming_end = new Intent(streaming.this, streaming_end.class);
                streaming_end.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                streaming_end.putExtra("streaming_duration", streaming_timer.getText().toString());
                startActivity(streaming_end);
                finish();
            }
        });
    }

}
