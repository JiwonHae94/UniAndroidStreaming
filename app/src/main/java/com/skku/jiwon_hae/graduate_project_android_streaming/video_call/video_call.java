package com.skku.jiwon_hae.graduate_project_android_streaming.video_call;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.skku.jiwon_hae.graduate_project_android_streaming.R;
import com.skku.jiwon_hae.graduate_project_android_streaming.utils.useSharedPreference;
import com.skku.jiwon_hae.graduate_project_android_streaming.utils.utility;
import com.skku.jiwon_hae.graduate_project_android_streaming.video_call.friends.friendListAdapter;
import com.skku.jiwon_hae.graduate_project_android_streaming.video_call.friends.get_all_users_friend;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jiwon_hae on 2017. 11. 20..
 */

public class video_call extends Fragment {
    private utility utils = new utility(getContext());
    private ImageView new_video_friend;

    private GridView friendGridView;
    private friendListAdapter friendAdapter;

    //constants
    private constants video_call_constants = new constants();

    //VideoCall url
    private String connectURL = "https://appr.tc";

    //UserInformation
    private useSharedPreference useDB;
    private String userInformation;
    private String userName;
    private String user_email;

    //Permission
    private static int REQUEST_PERMISSION = 9001;

    //Search
    private EditText friendSearch;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_video_call, container, false);

        new_video_friend = (ImageView)view.findViewById(R.id.video_call_new_friend);
        useDB = new useSharedPreference(getContext());

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        request_permission();
        userInformation = useDB.get_user_information();

        try {
            JSONObject jsonObject = new JSONObject(userInformation);
            userName = jsonObject.get("userName").toString();
            user_email = jsonObject.get("user_id").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new_video_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent to_add_friend = new Intent(getContext(), video_call_add_friend.class);
                startActivity(to_add_friend);
            }
        });

        friendGridView = (GridView)view.findViewById(R.id.gridView_friends);
        friendAdapter = new friendListAdapter(getContext(),getActivity(), userName);
        friendGridView.setAdapter(friendAdapter);

        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String friends = jsonObject.getString("friends");

                    String[] friends_list = friends.split(",");

                    if(!friends.equals("")) {
                        for (int i = 0; i < friends_list.length; i++) {
                            friendAdapter.add(friends_list[i]);
                        }
                    }

                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        get_all_users_friend Validate = new get_all_users_friend(user_email, responseListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(Validate);

        friendSearch = (EditText)view.findViewById(R.id.friend_search);
        friendSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    friendAdapter.search(friendSearch.getText().toString());
                }
                return false;
            }
        });

        friendSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                friendAdapter.search(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        setDefaultSettingForVideoCall();
    }

    private void request_permission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.MODIFY_AUDIO_SETTINGS) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.MODIFY_AUDIO_SETTINGS) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)  {

            }else{
                if(this.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                    Toast.makeText(getContext(), "방송을 하시려면 카메라 접근을 허락하셔야합니다", Toast.LENGTH_SHORT).show();
                }

                if(this.shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)){
                    Toast.makeText(getContext(), "방송을 하시려면 마이크 접근을 허락하셔야합니다", Toast.LENGTH_SHORT).show();
                }

                if(this.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    Toast.makeText(getContext(), "방송을 하시려면 외부파일 접근을 허락하셔야합니다", Toast.LENGTH_SHORT).show();
                }

                this.requestPermissions(new String[] {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MODIFY_AUDIO_SETTINGS}, REQUEST_PERMISSION);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_PERMISSION){
            if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                Toast.makeText(getContext(), "방송을 하기 위해선, 카메라 접근을 허락하셔야합니다", Toast.LENGTH_SHORT).show();
            }

            if(grantResults[1] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(getContext(), "방송을 하기 위해선, 오디오 접근을 허락하셔야합니다", Toast.LENGTH_SHORT).show();
            }

            if(grantResults[2] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(getContext(), "방송을 하기 위해선, 외부파일 접근을 허락하셔야합니다", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        friendAdapter.onPause();
    }

    private String keyprefVideoCallEnabled;
    private String keyprefScreencapture;
    private String keyprefCamera2;
    private String keyprefResolution;
    private String keyprefFps;
    private String keyprefCaptureQualitySlider;
    private String keyprefVideoBitrateType;
    private String keyprefVideoBitrateValue;
    private String keyprefVideoCodec;
    private String keyprefAudioBitrateType;
    private String keyprefAudioBitrateValue;
    private String keyprefAudioCodec;
    private String keyprefHwCodecAcceleration;
    private String keyprefCaptureToTexture;
    private String keyprefFlexfec;
    private String keyprefNoAudioProcessingPipeline;
    private String keyprefAecDump;
    private String keyprefOpenSLES;
    private String keyprefDisableBuiltInAec;
    private String keyprefDisableBuiltInAgc;
    private String keyprefDisableBuiltInNs;
    private String keyprefEnableLevelControl;
    private String keyprefDisableWebRtcAGCAndHPF;
    private String keyprefDisplayHud;
    private String keyprefTracing;
    private String keyprefRoomServerUrl;
    private String keyprefRoom;
    private String keyprefRoomList;
    private ArrayList<String> roomList;
    private ArrayAdapter<String> adapter;
    private String keyprefEnableDataChannel;
    private String keyprefOrdered;
    private String keyprefMaxRetransmitTimeMs;
    private String keyprefMaxRetransmits;
    private String keyprefDataProtocol;
    private String keyprefNegotiated;
    private String keyprefDataId;

    private SharedPreferences sharedPref;

    public void setDefaultSettingForVideoCall(){
        PreferenceManager.setDefaultValues(getContext(), R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        keyprefVideoCallEnabled = getString(R.string.pref_videocall_key);
        keyprefScreencapture = getString(R.string.pref_screencapture_key);
        keyprefCamera2 = getString(R.string.pref_camera2_key);
        keyprefResolution = getString(R.string.pref_resolution_key);
        keyprefFps = getString(R.string.pref_fps_key);
        keyprefCaptureQualitySlider = getString(R.string.pref_capturequalityslider_key);
        keyprefVideoBitrateType = getString(R.string.pref_maxvideobitrate_key);
        keyprefVideoBitrateValue = getString(R.string.pref_maxvideobitratevalue_key);
        keyprefVideoCodec = getString(R.string.pref_videocodec_key);
        keyprefHwCodecAcceleration = getString(R.string.pref_hwcodec_key);
        keyprefCaptureToTexture = getString(R.string.pref_capturetotexture_key);
        keyprefFlexfec = getString(R.string.pref_flexfec_key);
        keyprefAudioBitrateType = getString(R.string.pref_startaudiobitrate_key);
        keyprefAudioBitrateValue = getString(R.string.pref_startaudiobitratevalue_key);
        keyprefAudioCodec = getString(R.string.pref_audiocodec_key);
        keyprefNoAudioProcessingPipeline = getString(R.string.pref_noaudioprocessing_key);
        keyprefAecDump = getString(R.string.pref_aecdump_key);
        keyprefOpenSLES = getString(R.string.pref_opensles_key);
        keyprefDisableBuiltInAec = getString(R.string.pref_disable_built_in_aec_key);
        keyprefDisableBuiltInAgc = getString(R.string.pref_disable_built_in_agc_key);
        keyprefDisableBuiltInNs = getString(R.string.pref_disable_built_in_ns_key);
        keyprefEnableLevelControl = getString(R.string.pref_enable_level_control_key);
        keyprefDisableWebRtcAGCAndHPF = getString(R.string.pref_disable_webrtc_agc_and_hpf_key);
        keyprefDisplayHud = getString(R.string.pref_displayhud_key);
        keyprefTracing = getString(R.string.pref_tracing_key);
        keyprefRoomServerUrl = getString(R.string.pref_room_server_url_key);
        keyprefRoom = getString(R.string.pref_room_key);
        keyprefRoomList = getString(R.string.pref_room_list_key);
        keyprefEnableDataChannel = getString(R.string.pref_enable_datachannel_key);
        keyprefOrdered = getString(R.string.pref_ordered_key);
        keyprefMaxRetransmitTimeMs = getString(R.string.pref_max_retransmit_time_ms_key);
        keyprefMaxRetransmits = getString(R.string.pref_max_retransmits_key);
        keyprefDataProtocol = getString(R.string.pref_data_protocol_key);
        keyprefNegotiated = getString(R.string.pref_negotiated_key);
        keyprefDataId = getString(R.string.pref_data_id_key);
    }

}
