package com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.chatting;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ListView;

import com.skku.jiwon_hae.graduate_project_android_streaming.utils.useSharedPreference;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by jiwon_hae on 2017. 10. 17..
 */

public class chat_client {
    private Context context;

    private SocketChannel socketChannel;
    private Boolean OnChatting = false;
    private ListView chat_display;

    private ArrayList<chat_items> chatting;
    public chatting_adapter cAdapter;

    private Gson gson = new Gson();
    private chat_items m;

    //UserInformation
    private useSharedPreference useDB;
    private String userInformation;
    private String user_email;
    private String userName;
    private String type;
    private String streamer;

    public chat_client(Context context, ListView listView, String streamer, String type){
        this.context = context;
        this.chat_display = listView;
        chatting = new ArrayList<>();

        cAdapter = new chatting_adapter(context, chatting);
        this.chat_display.setAdapter(cAdapter);

        useDB = new useSharedPreference(context);
        userInformation = useDB.get_user_information();
        this.type = type;
        this.streamer = streamer;

        try {
            JSONObject jsonObject = new JSONObject(userInformation);
            userName = jsonObject.get("userName").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void startClient() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    socketChannel = SocketChannel.open();
                    socketChannel.configureBlocking(true);
                    socketChannel.connect(new InetSocketAddress("ec2-13-125-170-236.ap-northeast-2.compute.amazonaws.com", 2990));

                    Thread send_login_msg = new Thread(){
                        public void run(){
                            if(socketChannel.isConnected()){
                                loginMsg(type, streamer, userName);
                            }
                        }

                    };
                    send_login_msg.start();

                    if(socketChannel.isConnected()){
                        send_login_msg.interrupt();
                    }

                } catch (Exception e) {
                    Log.e("Chatting_client", "[서버 통신 안됨]");

                    if (socketChannel.isOpen()) {
                        stopClient();
                    }
                    return;
                }
                receive();
            }
        };

        thread.start();
    }

    public void stopClient() {
        try {
            Log.e("chat_client", "[연결 끊음]");

            if (socketChannel != null && socketChannel.isOpen()) {
                socketChannel.close();
            }
        } catch (IOException e) {
            Log.e("stopClient", e.toString());
        }
    }

    public void receive() {
        while (true) {
            try {
                ByteBuffer byteBuffer = ByteBuffer.allocate(2500);

                int byteCount = socketChannel.read(byteBuffer);

                if (byteCount == -1) {
                    throw new IOException();
                }

                byteBuffer.flip();
                Charset charset = Charset.forName("UTF-8");
                String data = charset.decode(byteBuffer).toString();

                Gson gson = new Gson();
                final chat_items m = gson.fromJson(data, chat_items.class);

                Handler mHandler = new Handler(Looper.getMainLooper());

                if(m.getType().equals("message")){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            cAdapter.add(m);
                        }
                    });
                }else if(m.getType().equals("polling_made")){
                    Intent new_poll = new Intent("streaming_polling");
                    new_poll.putExtra("poll_content", m);

                    LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(context);
                    mgr.sendBroadcast(new_poll);
                }
            } catch (Exception e) {
                Log.e("receive:", e.toString());
                Log.e("chatting_client : ", "[서버 통신 안됨]");
                stopClient();
                break;
            }
        }
    }

    public void loginMsg(String type, String streamerName, String userName){
        if(type.equals("viewer")){
            send("login", "viewer_login", streamerName, userName);
        }else if(type.equals("streamer")){
            send("login", "streamer_login", userName, userName);
        }
    }

    public void send(final String data, final String type, final String streaming_room, final String username) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Charset charset = Charset.forName("UTF-8");
                    m = new chat_items(username, data, type, streaming_room);
                    ByteBuffer byteBuffer = charset.encode(gson.toJson(m));
                    socketChannel.write(byteBuffer);

                } catch (Exception e) {
                    Log.e("send:", e.toString());
                    Log.e("chatting_client : ", "[서버 통신 안됨]");
                    stopClient();
                }
            }

        };
        thread.start();
    }

    public void setSrcLanague(String lang){
        switch(lang){
            case "KOR":
                cAdapter.setLanguageOption("ko");
                break;
            case "ENG":
                cAdapter.setLanguageOption("en");
                break;
            default:
                cAdapter.setLanguageOption("NULL");
                break;
        }
    }


}
