package com.skku.jiwon_hae.graduate_project_android_streaming.video_call.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jiwon_hae on 2017. 11. 24..
 */

public class PnRTCMessage extends JSONObject {
    public static final String JSON_TYPE       = "type";
    public static final String JSON_PACKET     = "packet";
    public static final String JSON_ID         = "id";
    public static final String JSON_NUMBER     = "number"; // Todo: Change to more accurate name.
    public static final String JSON_MESSAGE    = "message";
    public static final String JSON_USERMSG    = "usermsg";
    public static final String JSON_HANGUP     = "hangup";
    public static final String JSON_THUMBNAIL  = "thumbnail";
    public static final String JSON_SDP        = "sdp";
    public static final String JSON_ICE        = "candidate"; // Identify ICE

    private String message;
    private JSONObject json;

    public PnRTCMessage(String message){
        super();
        try {
            this.put(JSON_MESSAGE, message);
        } catch (JSONException e){
            throw new RuntimeException("Invalid JSON Payload");
        }
        this.message = message;
        this.json = this;
    }

    public PnRTCMessage(JSONObject json){
        super();
        try {
            this.put(JSON_MESSAGE, json);
        } catch (JSONException e){
            throw new RuntimeException("Invalid JSON Payload");
        }
        this.message = json.toString();
        this.json = this;
    }

    public String getMessage() {
        return message;
    }

    public JSONObject getJSON(){
        return this.json;
    }
}
