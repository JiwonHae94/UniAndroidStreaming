package com.skku.jiwon_hae.graduate_project_android_streaming.video_call;

/**
 * Created by jiwon_hae on 2018. 7. 29..
 */

public class constants {

    public static final String SHARED_PREFS = "me.pntutorial.SHARED_PREFS";
    public static final String CALL_USER    = "VIDEO_CALLER";
    public static final String STDBY_SUFFIX = "-stdby";
    public static final String PUB_KEY = "\n" +
            "pub-c-96b84e82-c401-48e0-a100-3977239803e4"; // Your Pub Key
    public static final String SUB_KEY = "\n" +
            "sub-c-59057f48-9fa0-11e8-944c-22e677923cb5"; // Your Sub Key
    public static final String JSON_CALL_USER = "call_user";
    public static final String JSON_CALL_TIME = "call_time";
    public static final String JSON_OCCUPANCY = "occupancy";
    public static final String JSON_STATUS    = "status";

    // JSON for user messages
    public static final String JSON_USER_MSG  = "user_message";
    public static final String JSON_MSG_UUID  = "msg_uuid";
    public static final String JSON_MSG       = "msg_message";
    public static final String JSON_TIME      = "msg_timestamp";

    public static final String STATUS_AVAILABLE = "Available";
    public static final String STATUS_OFFLINE   = "Offline";
    public static final String STATUS_BUSY      = "Busy";
}
