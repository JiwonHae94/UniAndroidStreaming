package com.skku.jiwon_hae.graduate_project_android_streaming.video_call;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.skku.jiwon_hae.graduate_project_android_streaming.R;
import com.skku.jiwon_hae.graduate_project_android_streaming.main;
import com.skku.jiwon_hae.graduate_project_android_streaming.utils.useSharedPreference;
import com.skku.jiwon_hae.graduate_project_android_streaming.video_call.utils.PnRTCMaterial;
import com.pubnub.api.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import static com.skku.jiwon_hae.graduate_project_android_streaming.video_call.service.video_call_service.mPubNub;
import static com.skku.jiwon_hae.graduate_project_android_streaming.video_call.video_call_con.EXTRA_ROOMID;

public class video_call_incoming_call extends AppCompatActivity implements View.OnClickListener {

    private ImageView accept_call;
    private ImageView drop_call;
    private String connectURL = "https://appr.tc";

    private String username;
    private String callUser;
    private TextView callerName;

    private ImageView caller_profile_pic;

    private useSharedPreference useDB = new useSharedPreference(this);
    private String userInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call_incoming_call);
        getSupportActionBar().hide();

        accept_call = (ImageView)findViewById(R.id.incoming_call_accept);
        accept_call.setOnClickListener(this);

        drop_call = (ImageView)findViewById(R.id.incoming_call_drop);
        drop_call.setOnClickListener(this);

        useDB = new useSharedPreference(this);
        userInformation = useDB.get_user_information();

        try {
            JSONObject jsonObject = new JSONObject(userInformation);
            username = jsonObject.get("userName").toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Bundle extras = getIntent().getExtras();
        callUser = extras.getString(constants.CALL_USER);

        /*if (extras==null || !extras.containsKey(username)){
            Intent intent = new Intent(this, main.class);
            startActivity(intent);
            Toast.makeText(this, callUser,
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }*/

        callerName = (TextView)findViewById(R.id.incoming_call_callerName);
        callerName.setText(callUser);

        caller_profile_pic = (ImageView)findViewById(R.id.incoming_call_caller_profile_pic);
        String profile_pic_url = "http://ec2-13-125-170-236.ap-northeast-2.compute.amazonaws.com/streaming_application/profile_pic/" + callUser + ".jpg";

        Glide.with(this)
                .load(profile_pic_url)
                .placeholder(R.drawable.default_icon)
                .into(caller_profile_pic);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.incoming_call_accept:
                acceptCall();
                break;

            case R.id.incoming_call_drop:
                rejectCall();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        setDefaultConfig();
    }

    private void setDefaultConfig(){
        keyprefRoomServerUrl = getString(R.string.pref_room_server_url_key);
        keyprefResolution = getString(R.string.pref_resolution_key);
        keyprefVideoBitrateType = getString(R.string.pref_maxvideobitrate_key);
        keyprefVideoBitrateValue = getString(R.string.pref_maxvideobitratevalue_key);
        keyprefAudioBitrateValue = getString(R.string.pref_startaudiobitratevalue_key);
        keyprefAudioBitrateType = getString(R.string.pref_startaudiobitrate_key);
    }

    public void acceptCall(){
        connectToRoom(username, false, false, false, 0);
        finish();
    }

    private final String TAG = "Incoming call";

    private void connectToRoom(String roomId, boolean commandLineRun, boolean loopback,
                               boolean useValuesFromIntent, int runTimeMs) {
        this.commandLineRun = commandLineRun;

        // roomId is random for loopback.
        if (loopback) {
            roomId = Integer.toString((new Random()).nextInt(100000000));
        }

        String roomUrl = sharedPref.getString(
                keyprefRoomServerUrl, getString(R.string.pref_room_server_url_default));

        // Video call enabled flag.
        boolean videoCallEnabled = sharedPrefGetBoolean(R.string.pref_videocall_key,
                video_call_con.EXTRA_VIDEO_CALL, R.string.pref_videocall_default, useValuesFromIntent);

        // Use screencapture option.
        boolean useScreencapture = sharedPrefGetBoolean(R.string.pref_screencapture_key,
                video_call_con.EXTRA_SCREENCAPTURE, R.string.pref_screencapture_default, useValuesFromIntent);

        // Use Camera2 option.
        boolean useCamera2 = sharedPrefGetBoolean(R.string.pref_camera2_key, video_call_con.EXTRA_CAMERA2,
                R.string.pref_camera2_default, useValuesFromIntent);

        // Get default codecs.
        String videoCodec = sharedPrefGetString(R.string.pref_videocodec_key,
                video_call_con.EXTRA_VIDEOCODEC, R.string.pref_videocodec_default, useValuesFromIntent);
        String audioCodec = sharedPrefGetString(R.string.pref_audiocodec_key,
                video_call_con.EXTRA_AUDIOCODEC, R.string.pref_audiocodec_default, useValuesFromIntent);

        // Check HW codec flag.
        boolean hwCodec = sharedPrefGetBoolean(R.string.pref_hwcodec_key,
                video_call_con.EXTRA_HWCODEC_ENABLED, R.string.pref_hwcodec_default, useValuesFromIntent);

        // Check Capture to texture.
        boolean captureToTexture = sharedPrefGetBoolean(R.string.pref_capturetotexture_key,
                video_call_con.EXTRA_CAPTURETOTEXTURE_ENABLED, R.string.pref_capturetotexture_default,
                useValuesFromIntent);

        // Check FlexFEC.
        boolean flexfecEnabled = sharedPrefGetBoolean(R.string.pref_flexfec_key,
                video_call_con.EXTRA_FLEXFEC_ENABLED, R.string.pref_flexfec_default, useValuesFromIntent);

        // Check Disable Audio Processing flag.
        boolean noAudioProcessing = sharedPrefGetBoolean(R.string.pref_noaudioprocessing_key,
                video_call_con.EXTRA_NOAUDIOPROCESSING_ENABLED, R.string.pref_noaudioprocessing_default,
                useValuesFromIntent);

        // Check Disable Audio Processing flag.
        boolean aecDump = sharedPrefGetBoolean(R.string.pref_aecdump_key,
                video_call_con.EXTRA_AECDUMP_ENABLED, R.string.pref_aecdump_default, useValuesFromIntent);

        // Check OpenSL ES enabled flag.
        boolean useOpenSLES = sharedPrefGetBoolean(R.string.pref_opensles_key,
                video_call_con.EXTRA_OPENSLES_ENABLED, R.string.pref_opensles_default, useValuesFromIntent);

        // Check Disable built-in AEC flag.
        boolean disableBuiltInAEC = sharedPrefGetBoolean(R.string.pref_disable_built_in_aec_key,
                video_call_con.EXTRA_DISABLE_BUILT_IN_AEC, R.string.pref_disable_built_in_aec_default,
                useValuesFromIntent);

        // Check Disable built-in AGC flag.
        boolean disableBuiltInAGC = sharedPrefGetBoolean(R.string.pref_disable_built_in_agc_key,
                video_call_con.EXTRA_DISABLE_BUILT_IN_AGC, R.string.pref_disable_built_in_agc_default,
                useValuesFromIntent);

        // Check Disable built-in NS flag.
        boolean disableBuiltInNS = sharedPrefGetBoolean(R.string.pref_disable_built_in_ns_key,
                video_call_con.EXTRA_DISABLE_BUILT_IN_NS, R.string.pref_disable_built_in_ns_default,
                useValuesFromIntent);

        // Check Enable level control.
        boolean enableLevelControl = sharedPrefGetBoolean(R.string.pref_enable_level_control_key,
                video_call_con.EXTRA_ENABLE_LEVEL_CONTROL, R.string.pref_enable_level_control_key,
                useValuesFromIntent);

        // Check Disable gain control
        boolean disableWebRtcAGCAndHPF = sharedPrefGetBoolean(
                R.string.pref_disable_webrtc_agc_and_hpf_key, video_call_con.EXTRA_DISABLE_WEBRTC_AGC_AND_HPF,
                R.string.pref_disable_webrtc_agc_and_hpf_key, useValuesFromIntent);

        // Get video resolution from settings.
        int videoWidth = 0;
        int videoHeight = 0;
        if (useValuesFromIntent) {
            videoWidth = getIntent().getIntExtra(video_call_con.EXTRA_VIDEO_WIDTH, 0);
            videoHeight = getIntent().getIntExtra(video_call_con.EXTRA_VIDEO_HEIGHT, 0);
        }
        if (videoWidth == 0 && videoHeight == 0) {
            String resolution =
                    sharedPref.getString(keyprefResolution, getString(R.string.pref_resolution_default));
            String[] dimensions = resolution.split("[ x]+");
            if (dimensions.length == 2) {
                try {
                    videoWidth = Integer.parseInt(dimensions[0]);
                    videoHeight = Integer.parseInt(dimensions[1]);
                } catch (NumberFormatException e) {
                    videoWidth = 0;
                    videoHeight = 0;
                    Log.e(TAG, "Wrong video resolution setting: " + resolution);
                }
            }
        }

        // Get camera fps from settings.
        int cameraFps = 0;
        if (useValuesFromIntent) {
            cameraFps = getIntent().getIntExtra(video_call_con.EXTRA_VIDEO_FPS, 0);
        }
        if (cameraFps == 0) {
            String fps = sharedPref.getString(keyprefFps, getString(R.string.pref_fps_default));
            String[] fpsValues = fps.split("[ x]+");
            if (fpsValues.length == 2) {
                try {
                    cameraFps = Integer.parseInt(fpsValues[0]);
                } catch (NumberFormatException e) {
                    cameraFps = 0;
                    Log.e(TAG, "Wrong camera fps setting: " + fps);
                }
            }
        }

        // Check capture quality slider flag.
        boolean captureQualitySlider = sharedPrefGetBoolean(R.string.pref_capturequalityslider_key,
                video_call_con.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED,
                R.string.pref_capturequalityslider_default, useValuesFromIntent);

        // Get video and audio start bitrate.
        int videoStartBitrate = 0;
        if (useValuesFromIntent) {
            videoStartBitrate = getIntent().getIntExtra(video_call_con.EXTRA_VIDEO_BITRATE, 0);
        }
        if (videoStartBitrate == 0) {
            String bitrateTypeDefault = getString(R.string.pref_maxvideobitrate_default);
            String bitrateType = sharedPref.getString(keyprefVideoBitrateType, bitrateTypeDefault);
            if (!bitrateType.equals(bitrateTypeDefault)) {
                String bitrateValue = sharedPref.getString(
                        keyprefVideoBitrateValue, getString(R.string.pref_maxvideobitratevalue_default));
                videoStartBitrate = Integer.parseInt(bitrateValue);
            }
        }

        int audioStartBitrate = 0;
        if (useValuesFromIntent) {
            audioStartBitrate = getIntent().getIntExtra(video_call_con.EXTRA_AUDIO_BITRATE, 0);
        }
        if (audioStartBitrate == 0) {
            String bitrateTypeDefault = getString(R.string.pref_startaudiobitrate_default);
            String bitrateType = sharedPref.getString(keyprefAudioBitrateType, bitrateTypeDefault);
            if (!bitrateType.equals(bitrateTypeDefault)) {
                String bitrateValue = sharedPref.getString(
                        keyprefAudioBitrateValue, getString(R.string.pref_startaudiobitratevalue_default));
                audioStartBitrate = Integer.parseInt(bitrateValue);
            }
        }

        // Check statistics display option.
        boolean displayHud = sharedPrefGetBoolean(R.string.pref_displayhud_key,
                video_call_con.EXTRA_DISPLAY_HUD, R.string.pref_displayhud_default, useValuesFromIntent);

        boolean tracing = sharedPrefGetBoolean(R.string.pref_tracing_key, video_call_con.EXTRA_TRACING,
                R.string.pref_tracing_default, useValuesFromIntent);

        // Get datachannel options
        boolean dataChannelEnabled = sharedPrefGetBoolean(R.string.pref_enable_datachannel_key,
                video_call_con.EXTRA_DATA_CHANNEL_ENABLED, R.string.pref_enable_datachannel_default,
                useValuesFromIntent);
        boolean ordered = sharedPrefGetBoolean(R.string.pref_ordered_key, video_call_con.EXTRA_ORDERED,
                R.string.pref_ordered_default, useValuesFromIntent);
        boolean negotiated = sharedPrefGetBoolean(R.string.pref_negotiated_key,
                video_call_con.EXTRA_NEGOTIATED, R.string.pref_negotiated_default, useValuesFromIntent);
        int maxRetrMs = sharedPrefGetInteger(R.string.pref_max_retransmit_time_ms_key,
                video_call_con.EXTRA_MAX_RETRANSMITS_MS, R.string.pref_max_retransmit_time_ms_default,
                useValuesFromIntent);
        int maxRetr =
                sharedPrefGetInteger(R.string.pref_max_retransmits_key, video_call_con.EXTRA_MAX_RETRANSMITS,
                        R.string.pref_max_retransmits_default, useValuesFromIntent);
        int id = sharedPrefGetInteger(R.string.pref_data_id_key, video_call_con.EXTRA_ID,
                R.string.pref_data_id_default, useValuesFromIntent);
        String protocol = sharedPrefGetString(R.string.pref_data_protocol_key,
                video_call_con.EXTRA_PROTOCOL, R.string.pref_data_protocol_default, useValuesFromIntent);

        // Start AppRTCMobile activity.
        Log.e(TAG, "Connecting to room " + roomId + " at URL " + roomUrl);

        if (validateUrl(roomUrl)) {
            Uri uri = Uri.parse(roomUrl);
            Intent intent = new Intent(video_call_incoming_call.this, video_call_con.class);
            intent.setData(uri);
            intent.putExtra(EXTRA_ROOMID, username);
            intent.putExtra(video_call_con.EXTRA_LOOPBACK, loopback);
            intent.putExtra(video_call_con.EXTRA_VIDEO_CALL, videoCallEnabled);
            intent.putExtra(video_call_con.EXTRA_SCREENCAPTURE, useScreencapture);
            intent.putExtra(video_call_con.EXTRA_CAMERA2, useCamera2);
            intent.putExtra(video_call_con.EXTRA_VIDEO_WIDTH, videoWidth);
            intent.putExtra(video_call_con.EXTRA_VIDEO_HEIGHT, videoHeight);
            intent.putExtra(video_call_con.EXTRA_VIDEO_FPS, cameraFps);
            intent.putExtra(video_call_con.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED, captureQualitySlider);
            intent.putExtra(video_call_con.EXTRA_VIDEO_BITRATE, videoStartBitrate);
            intent.putExtra(video_call_con.EXTRA_VIDEOCODEC, videoCodec);
            intent.putExtra(video_call_con.EXTRA_HWCODEC_ENABLED, hwCodec);
            intent.putExtra(video_call_con.EXTRA_CAPTURETOTEXTURE_ENABLED, captureToTexture);
            intent.putExtra(video_call_con.EXTRA_FLEXFEC_ENABLED, flexfecEnabled);
            intent.putExtra(video_call_con.EXTRA_NOAUDIOPROCESSING_ENABLED, noAudioProcessing);
            intent.putExtra(video_call_con.EXTRA_AECDUMP_ENABLED, aecDump);
            intent.putExtra(video_call_con.EXTRA_OPENSLES_ENABLED, useOpenSLES);
            intent.putExtra(video_call_con.EXTRA_DISABLE_BUILT_IN_AEC, disableBuiltInAEC);
            intent.putExtra(video_call_con.EXTRA_DISABLE_BUILT_IN_AGC, disableBuiltInAGC);
            intent.putExtra(video_call_con.EXTRA_DISABLE_BUILT_IN_NS, disableBuiltInNS);
            intent.putExtra(video_call_con.EXTRA_ENABLE_LEVEL_CONTROL, enableLevelControl);
            intent.putExtra(video_call_con.EXTRA_DISABLE_WEBRTC_AGC_AND_HPF, disableWebRtcAGCAndHPF);
            intent.putExtra(video_call_con.EXTRA_AUDIO_BITRATE, audioStartBitrate);
            intent.putExtra(video_call_con.EXTRA_AUDIOCODEC, audioCodec);
            intent.putExtra(video_call_con.EXTRA_DISPLAY_HUD, displayHud);
            intent.putExtra(video_call_con.EXTRA_TRACING, tracing);
            intent.putExtra(video_call_con.EXTRA_CMDLINE, commandLineRun);
            intent.putExtra(video_call_con.EXTRA_RUNTIME, runTimeMs);

            intent.putExtra(video_call_con.EXTRA_DATA_CHANNEL_ENABLED, dataChannelEnabled);

            if (dataChannelEnabled) {
                intent.putExtra(video_call_con.EXTRA_ORDERED, ordered);
                intent.putExtra(video_call_con.EXTRA_MAX_RETRANSMITS_MS, maxRetrMs);
                intent.putExtra(video_call_con.EXTRA_MAX_RETRANSMITS, maxRetr);
                intent.putExtra(video_call_con.EXTRA_PROTOCOL, protocol);
                intent.putExtra(video_call_con.EXTRA_NEGOTIATED, negotiated);
                intent.putExtra(video_call_con.EXTRA_ID, id);
            }

            if (useValuesFromIntent) {
                if (getIntent().hasExtra(video_call_con.EXTRA_VIDEO_FILE_AS_CAMERA)) {
                    String videoFileAsCamera =
                            getIntent().getStringExtra(video_call_con.EXTRA_VIDEO_FILE_AS_CAMERA);
                    intent.putExtra(video_call_con.EXTRA_VIDEO_FILE_AS_CAMERA, videoFileAsCamera);
                }

                if (getIntent().hasExtra(video_call_con.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE)) {
                    String saveRemoteVideoToFile =
                            getIntent().getStringExtra(video_call_con.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE);
                    intent.putExtra(video_call_con.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE, saveRemoteVideoToFile);
                }

                if (getIntent().hasExtra(video_call_con.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH)) {
                    int videoOutWidth =
                            getIntent().getIntExtra(video_call_con.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, 0);
                    intent.putExtra(video_call_con.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, videoOutWidth);
                }

                if (getIntent().hasExtra(video_call_con.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT)) {
                    int videoOutHeight =
                            getIntent().getIntExtra(video_call_con.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, 0);
                    intent.putExtra(video_call_con.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, videoOutHeight);
                }
            }

            startActivity(intent);
        }
    }

    private boolean validateUrl(String url) {
        if (URLUtil.isHttpsUrl(url) || URLUtil.isHttpUrl(url)) {
            return true;
        }
        new AlertDialog.Builder(this)
                .setTitle(getText(R.string.invalid_url_title))
                .setMessage(getString(R.string.invalid_url_text, url))
                .setCancelable(false)
                .setNeutralButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .create()
                .show();
        return false;
    }

    public void rejectCall(){
        JSONObject hangupMsg = PnRTCMaterial.generateHangupPacket(this.username);

        mPubNub.publish(this.callUser, hangupMsg, new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                Intent intent = new Intent(video_call_incoming_call.this, main.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private static boolean commandLineRun = false;
    private SharedPreferences sharedPref;
    private String keyprefResolution;
    private String keyprefVideoBitrateType;
    private String keyprefFps;
    private String keyprefVideoBitrateValue;
    private String keyprefAudioBitrateValue;
    private String keyprefAudioBitrateType;
    private String keyprefRoomServerUrl;

    private boolean sharedPrefGetBoolean(
            int attributeId, String intentName, int defaultId, boolean useFromIntent) {
        boolean defaultValue = Boolean.valueOf(getString(defaultId));
        if (useFromIntent) {
            return getIntent().getBooleanExtra(intentName, defaultValue);
        } else {
            String attributeName = getString(attributeId);
            return sharedPref.getBoolean(attributeName, defaultValue);
        }
    }

    private String sharedPrefGetString(
            int attributeId, String intentName, int defaultId, boolean useFromIntent) {
        String defaultValue = getString(defaultId);
        if (useFromIntent) {
            String value = getIntent().getStringExtra(intentName);
            if (value != null) {
                return value;
            }
            return defaultValue;
        } else {
            String attributeName = getString(attributeId);
            return sharedPref.getString(attributeName, defaultValue);
        }
    }

    private int sharedPrefGetInteger(
            int attributeId, String intentName, int defaultId, boolean useFromIntent) {
        String defaultString = getString(defaultId);
        int defaultValue = Integer.parseInt(defaultString);
        if (useFromIntent) {
            return getIntent().getIntExtra(intentName, defaultValue);
        } else {
            String attributeName = getString(attributeId);
            String value = sharedPref.getString(attributeName, defaultString);
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Wrong setting for: " + attributeName + ":" + value);
                return defaultValue;
            }
        }
    }
}
