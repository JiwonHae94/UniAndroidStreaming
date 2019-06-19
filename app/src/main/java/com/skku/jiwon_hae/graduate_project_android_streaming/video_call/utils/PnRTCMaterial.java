package com.skku.jiwon_hae.graduate_project_android_streaming.video_call.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jiwon_hae on 2017. 11. 24..
 */

public class PnRTCMaterial {
    public static JSONObject generateHangupPacket(String userId){
        JSONObject json = new JSONObject();
        try {
            JSONObject packet = new JSONObject();
            packet.put(PnRTCMessage.JSON_HANGUP, true);
            json.put(PnRTCMessage.JSON_PACKET, packet);
            json.put(PnRTCMessage.JSON_ID, ""); //Todo: session id, unused in js SDK?
            json.put(PnRTCMessage.JSON_NUMBER, userId);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return json;
    }
}
