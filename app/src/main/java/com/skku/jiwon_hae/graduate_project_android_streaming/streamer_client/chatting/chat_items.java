package com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.chatting;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jiwon_hae on 2017. 10. 11..
 */

public class chat_items implements Parcelable {
    private String username;
    private String contents;
    private String type;
    private String streaming;

    public chat_items(String username, String contents, String type, String streaming){
        this.username = username;
        this.contents = contents;
        this.type = type;
        this.streaming = streaming;
    }

    public chat_items(Parcel parcel){
        this.username = parcel.readString();
        this.contents = parcel.readString();
        this.type = parcel.readString();
        this.streaming = parcel.readString();
    }

    public static final Creator<chat_items> CREATOR = new Creator<chat_items>() {
        @Override
        public chat_items createFromParcel(Parcel in) {
            return new chat_items(in);
        }

        @Override
        public chat_items[] newArray(int size) {
            return new chat_items[size];
        }
    };
    public void setChatContent(String content){
        this.contents = content;
    }


    public String getUsername(){
        return this.username;
    }

    public String getContents(){
        return this.contents;
    }

    public String getType(){
        return this.type;
    }

    public String getStreaming(){
        return this.streaming;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(contents);
        dest.writeString(type);
        dest.writeString(streaming);
    }
}
