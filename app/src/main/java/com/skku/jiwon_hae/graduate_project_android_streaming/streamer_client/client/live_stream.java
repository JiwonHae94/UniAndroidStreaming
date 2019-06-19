package com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.media.AudioManager;
import android.media.MediaCodec;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.skku.jiwon_hae.graduate_project_android_streaming.R;
import com.skku.jiwon_hae.graduate_project_android_streaming.image.transform.CircleTransform;
import com.skku.jiwon_hae.graduate_project_android_streaming.main;
import com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.chatting.chat_client;
import com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.chatting.chat_items;
import com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.polling.polling_decision;
import com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.util.get_like;
import com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.util.send_like;
import com.skku.jiwon_hae.graduate_project_android_streaming.utils.useSharedPreference;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.android.exoplayer.DefaultLoadControl;
import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.LoadControl;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.chunk.Format;
import com.google.android.exoplayer.hls.DefaultHlsTrackSelector;
import com.google.android.exoplayer.hls.HlsChunkSource;
import com.google.android.exoplayer.hls.HlsMasterPlaylist;
import com.google.android.exoplayer.hls.HlsPlaylist;
import com.google.android.exoplayer.hls.HlsPlaylistParser;
import com.google.android.exoplayer.hls.HlsSampleSource;
import com.google.android.exoplayer.hls.PtsTimestampAdjusterProvider;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.util.ManifestFetcher;
import com.google.android.exoplayer.util.PlayerControl;
import com.google.android.exoplayer.util.Util;
import com.squareup.picasso.Picasso;

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

import static com.google.android.exoplayer.ExoPlayer.STATE_BUFFERING;
import static com.google.android.exoplayer.ExoPlayer.STATE_ENDED;
import static com.google.android.exoplayer.ExoPlayer.STATE_PREPARING;
import static com.google.android.exoplayer.ExoPlayer.STATE_READY;

public class live_stream extends AppCompatActivity implements ManifestFetcher.ManifestCallback<HlsPlaylist>,
        ExoPlayer.Listener,HlsSampleSource.EventListener, AudioManager.OnAudioFocusChangeListener, OnChartValueSelectedListener {

    private SurfaceView stream_live_view;
    private ExoPlayer player;
    private PlayerControl playerControl;
    private ProgressBar stream_live_ready;

    private String video_url;

    private Handler mainHandler;
    private AudioManager am;
    private String userAgent;

    private ManifestFetcher<HlsPlaylist> playlistFetcher;
    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int MAIN_BUFFER_SEGMENTS = 254;
    public static final int TYPE_VIDEO = 0;

    private TrackRenderer videoRenderer;
    private MediaCodecAudioTrackRenderer audioRenderer;

    private ImageButton leave_streaming_room;
    private ImageButton chat_appear;
    private ImageButton poll_appear;
    private LinearLayout layout_appear_whenLive;
    private LinearLayout layout_appear_chatting;
    private EditText chat_content;
    private Button chat_submit;

    //UserInformation
    private useSharedPreference useDB;
    private String userInformation;
    private String user_email;
    private String userName;

    //Streamer
    private String streamerName;
    private String streamTitle;
    private TextView streamerNameTextView;
    private ImageView streamer_profile_pic;
    private TextView streamTitleTextView;

    //Chatting
    private chat_client Chatting;

    //translation
    private FrameLayout translation_options;
    private TextView current_language_option;
    private ImageView translation_icon;

    //Polling
    private BroadcastReceiver polling_receiver;
    private LocalBroadcastManager broadcastManager;
    private TextView poll_badge;

    //Like
    private ImageView heartAnimation;
    private TextView no_of_likes;
    private ToggleButton send_like_btn;
    private boolean has_liked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_stream);
        getSupportActionBar().hide();

        streamerName = getIntent().getStringExtra("streamer");
        streamTitle = getIntent().getStringExtra("stream_title");

        streamerNameTextView = (TextView)findViewById(R.id.streaming_streamerId);
        streamerNameTextView.setText(streamerName);

        streamTitleTextView = (TextView)findViewById(R.id.streaming_title);
        streamTitleTextView.setText(streamTitle);

        //Chatting
        ListView chatting_listview = (ListView)findViewById(R.id.chatting_listview);

        useDB = new useSharedPreference(this);
        userInformation = useDB.get_user_information();

        try {
            JSONObject jsonObject = new JSONObject(userInformation);
            userName = jsonObject.get("userName").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Chatting = new chat_client(this, chatting_listview, streamerName, "viewer");
        Chatting.startClient();

        stream_live_view = (SurfaceView)findViewById(R.id.stream_live_view);

        player = ExoPlayer.Factory.newInstance(2);
        playerControl = new PlayerControl(player);

        video_url = "http://ec2-13-125-170-236.ap-northeast-2.compute.amazonaws.com/streaming_application/stream/"+streamerName+"/index.m3u8";

        am = (AudioManager) this.getApplicationContext().getSystemService(Context.AUDIO_SERVICE); // for requesting audio
        mainHandler = new Handler(); //handler required for hls
        userAgent = Util.getUserAgent(this, "MainActivity"); //useragent required for hls

        HlsPlaylistParser parser = new HlsPlaylistParser(); // init HlsPlaylistParser
        playlistFetcher = new ManifestFetcher<>(video_url, new DefaultUriDataSource(this, userAgent),
                parser); // url goes here, useragent and parser
        playlistFetcher.singleLoad(mainHandler.getLooper(), this); //with 'this' we'll implement ManifestFetcher.ManifestCallback<HlsPlaylist>
        //listener with it will come two functions
        stream_live_ready = (ProgressBar)findViewById(R.id.stream_live_ready);

        layout_appear_whenLive = (LinearLayout)findViewById(R.id.stremaing_layout_whenLive);
        layout_appear_chatting = (LinearLayout)findViewById(R.id.chat_input_layout);
        chat_appear = (ImageButton)findViewById(R.id.chat_appear);
        poll_appear = (ImageButton)findViewById(R.id.poll_appear);

        chat_content = (EditText)findViewById(R.id.chat_content);

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
                Chatting.send(message, "message", streamerName, userName);
                chat_content.setText("");
            }
        });

        poll_appear.setEnabled(false);

        //Polling
        Glide.with(this)
                .load(R.drawable.polling_icon)
                .thumbnail(0.1f)
                .into(poll_appear);

        poll_badge = (TextView)findViewById(R.id.badge_textView);

        leave_streaming_room = (ImageButton)findViewById(R.id.streaming_stop_streaming);
        leave_streaming_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chatting.send("logout", "logout", streamerName, userName);

                Intent to_main = new Intent(live_stream.this, main.class);
                startActivity(to_main);
            }
        });

        IntentFilter info_intentfilter = new IntentFilter();
        info_intentfilter.addAction("streaming_polling");

        polling_receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final chat_items poll_item = intent.getParcelableExtra("poll_content");

                poll_badge.setVisibility(View.VISIBLE);
                poll_appear.setEnabled(true);

                AlertDialog.Builder builder = new AlertDialog.Builder(live_stream.this);
                builder.setMessage("투표가 등록됬습니다")
                        .setPositiveButton("확인하기", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    showPolling_select(poll_item.getContents());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });

                android.support.v7.app.AlertDialog create_polling = builder.create();
                create_polling.setCanceledOnTouchOutside(true);
                create_polling.show();
            }
        };

        broadcastManager = LocalBroadcastManager.getInstance(live_stream.this);
        broadcastManager.registerReceiver(polling_receiver, info_intentfilter);

        streamer_profile_pic = (ImageView)findViewById(R.id.streamer_profile_pic);

        Picasso.with(live_stream.this)
                .load("http://ec2-13-125-170-236.ap-northeast-2.compute.amazonaws.com/streaming_application/profile_pic/" + streamerName+ ".jpg")
                .fit()
                .placeholder(R.drawable.default_icon)
                .transform(new CircleTransform())
                .into(streamer_profile_pic);

        //Translation
        translation_options = (FrameLayout)findViewById(R.id.translation);
        current_language_option = (TextView)findViewById(R.id.current_lang_option);
        translation_icon = (ImageView)findViewById(R.id.translation_icon);
        translation_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(live_stream.this, translation_options);
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

        //LIKES
        no_of_likes = (TextView)findViewById(R.id.stream_no_of_likes);
        send_like_btn = (ToggleButton)findViewById(R.id.stream_like);

    }



    @Override
    public void onBackPressed() {
        if(layout_appear_chatting.getVisibility() == View.VISIBLE){
            layout_appear_chatting.setVisibility(View.GONE);
        }else{
            Intent to_main = new Intent(live_stream.this, main.class);
            startActivity(to_main);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        playerControl.start();
        new get_vote_result().execute(false);
        poll_appear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new get_vote_result().execute();
            }
        });

        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String likes = jsonObject.get("likes").toString();

                    no_of_likes.setText(likes);

                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        get_like get_like = new get_like(streamerName, responseListener);
        RequestQueue queue = Volley.newRequestQueue(live_stream.this);
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

    }



    @Override
    protected void onPause() {
        super.onPause();

        playerControl.pause();
        Chatting.stopClient();
        broadcastManager.unregisterReceiver(polling_receiver);
    }


    @Override
    public void onAudioFocusChange(int focusChange) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch(playbackState){

            case STATE_ENDED:
                Log.e("STATE_ENDED", String.valueOf(STATE_ENDED));
                Intent live_end = new Intent(live_stream.this, live_end.class);
                live_end.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(live_end);
                break;

            case STATE_PREPARING:
                Log.e("STATE_PREPARING", String.valueOf(STATE_PREPARING));
                stream_live_ready.setVisibility(View.VISIBLE);
                break;

            case STATE_READY:
                Log.e("STATE_READY", String.valueOf(STATE_READY));
                stream_live_ready.setVisibility(View.GONE);
                layout_appear_whenLive.setVisibility(View.VISIBLE);

                break;

            case STATE_BUFFERING:
                Log.e("STATE_BUFFERING", String.valueOf(STATE_BUFFERING));
                stream_live_ready.setVisibility(View.VISIBLE);
                break;
        }

    }

    @Override
    public void onPlayWhenReadyCommitted() {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onLoadStarted(int sourceId, long length, int type, int trigger, Format format, long mediaStartTimeMs, long mediaEndTimeMs) {

    }

    @Override
    public void onLoadCompleted(int sourceId, long bytesLoaded, int type, int trigger, Format format, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs) {

    }

    @Override
    public void onLoadCanceled(int sourceId, long bytesLoaded) {

    }

    @Override
    public void onLoadError(int sourceId, IOException e) {

    }

    @Override
    public void onUpstreamDiscarded(int sourceId, long mediaStartTimeMs, long mediaEndTimeMs) {

    }

    @Override
    public void onDownstreamFormatChanged(int sourceId, Format format, int trigger, long mediaTimeMs) {

    }

    @Override
    public void onSingleManifest(HlsPlaylist manifest) {

        LoadControl loadControl = new DefaultLoadControl(new DefaultAllocator(BUFFER_SEGMENT_SIZE));
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        PtsTimestampAdjusterProvider timestampAdjusterProvider = new PtsTimestampAdjusterProvider();
        boolean haveSubtitles = false;
        boolean haveAudios = false;
        if (manifest instanceof HlsMasterPlaylist) {
            HlsMasterPlaylist masterPlaylist = (HlsMasterPlaylist) manifest;
            haveSubtitles = !masterPlaylist.subtitles.isEmpty();
        }
        // Build the video/id3 renderers.
        DataSource dataSource = new DefaultUriDataSource(this, bandwidthMeter, userAgent);
        HlsChunkSource chunkSource = new HlsChunkSource(true /* isMaster */, dataSource, manifest,
                DefaultHlsTrackSelector.newDefaultInstance(this), bandwidthMeter,
                timestampAdjusterProvider, HlsChunkSource.ADAPTIVE_MODE_SPLICE);

        HlsSampleSource sampleSource = new HlsSampleSource(chunkSource, loadControl,
                MAIN_BUFFER_SEGMENTS * BUFFER_SEGMENT_SIZE, mainHandler, this, TYPE_VIDEO);

        MediaCodecVideoTrackRenderer videoRenderer = new MediaCodecVideoTrackRenderer(this, sampleSource,
                MediaCodecSelector.DEFAULT, MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT);

        MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource,
                MediaCodecSelector.DEFAULT);

        this.videoRenderer = videoRenderer;
        this.audioRenderer = audioRenderer;
        pushSurface(false); // here we pushsurface
        player.prepare(videoRenderer,audioRenderer); //prepare
        player.addListener(this); //add listener for the text field

        if (requestFocus())
            player.setPlayWhenReady(true);
    }

    @Override
    public void onSingleManifestError(IOException e) {
    }

    public boolean requestFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                am.requestAudioFocus(live_stream.this, AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN);
    }

    private void pushSurface(boolean blockForSurfacePush) {

        if (videoRenderer == null) {return;}
        if (blockForSurfacePush) {
            player.blockingSendMessage(
                    videoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, stream_live_view.getHolder().getSurface());
        } else {
            player.sendMessage(
                    videoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, stream_live_view.getHolder().getSurface());
        }
    }

    //Polling
    public void showPolling_select(String poll_content) throws JSONException {
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.polling_selection_layout, null);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setView(view);

        JSONObject jsonObject = new JSONObject(poll_content);

        final TextView polling_title = (TextView)view.findViewById(R.id.polling_title);
        polling_title.setText(jsonObject.getString("poll_title"));

        final ToggleButton poll_yes = (ToggleButton)view.findViewById(R.id.polling_yes);
        poll_yes.setText(jsonObject.getString("option1"));
        poll_yes.setTextOn(jsonObject.getString("option1"));
        poll_yes.setTextOff(jsonObject.getString("option1"));

        int[] color_yes = {getResources().getColor(R.color.poll_color1),getResources().getColor(R.color.poll_color2)};
        float[] position_yes = {0, 1};
        Shader.TileMode tile_mode_yes =  Shader.TileMode.REPEAT;
        LinearGradient lin_grad_yes = new LinearGradient(0, 0, 200, 0,color_yes, position_yes, tile_mode_yes);
        Shader shader_gradient_yes = lin_grad_yes;
        poll_yes.getPaint().setShader(shader_gradient_yes);

        final ToggleButton poll_no = (ToggleButton) view.findViewById(R.id.polling_no);
        poll_no.setText(jsonObject.getString("option2"));
        poll_no.setTextOn(jsonObject.getString("option2"));
        poll_no.setTextOff(jsonObject.getString("option2"));

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
                poll_yes.setTextOn(jsonObject.getString("option1") + "\n"+p_yes + "%");
                poll_yes.setTextOff(jsonObject.getString("option1") + "\n" +p_yes + "%");

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
                poll_no.setTextOn(jsonObject.getString("option2") + "\n" + p_no + "%");
                poll_no.setTextOff(jsonObject.getString("option2") + "\n" + p_no + "%");

                LinearLayout.LayoutParams param_no = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        Float.parseFloat(jsonObject.get("option2_result").toString())
                );

                poll_no.setLayoutParams(param_no);
            }
        }

        poll_yes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    poll_no.setChecked(false);
                }
            }
        });

        poll_no.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    poll_yes.setChecked(false);
                }
            }
        });

        builder.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selected_item = null;

                if(poll_yes.isChecked() || poll_no.isChecked()){
                    if(poll_yes.isChecked()){
                        selected_item = "option1";
                    }else{
                        selected_item = "option2";
                    }

                    Response.Listener<String> responseListener = new Response.Listener<String>(){
                        @Override
                        public void onResponse(String response) {
                        }
                    };

                    polling_decision poll_decision = new polling_decision(streamerName, selected_item+"_result", responseListener);
                    RequestQueue queue = Volley.newRequestQueue(live_stream.this);
                    queue.add(poll_decision);

                    submitPoll = true;
                    poll_appear.setEnabled(true);

                }else{
                    Toast.makeText(live_stream.this, "선택해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        android.support.v7.app.AlertDialog create_polling = builder.create();
        create_polling.setCanceledOnTouchOutside(false);
        create_polling.show();
        create_polling.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private boolean submitPoll = false;

    public class get_vote_result extends AsyncTask<Object, Object, String> {
        private boolean has_poll = false;
        private ArrayList<PieEntry> entries = new ArrayList<>();


        @Override
        protected String doInBackground(Object... params) {
            String targetURL = "http://13.125.170.236/streaming_application/redis/polling/get_polling_result.php?streamer=" + streamerName;

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

                if (has_poll) {
                    if (submitPoll) {
                        showPolling_result(poll_item.toString(), entries);
                    } else {
                        if(poll_appear.isEnabled()){
                            showPolling_select(poll_item.toString());
                        }
                    }

                    poll_appear.setEnabled(true);
                    poll_badge.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void showPolling_result(String data_set, ArrayList poll_result) throws JSONException {
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.polling_result_layout, null);
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

        PieDataSet dataSet = new PieDataSet(poll_result, "Election Results");

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
        mChart.setOnChartValueSelectedListener(this);

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        android.support.v7.app.AlertDialog create_polling = builder.create();
        create_polling.setCanceledOnTouchOutside(true);
        create_polling.show();
        create_polling.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    private void mSendLike(String like){
        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {

            }
        };

        send_like stream_send_like = new send_like(streamerName, like, responseListener);
        RequestQueue queue = Volley.newRequestQueue(live_stream.this);
        queue.add(stream_send_like);
    }
}
