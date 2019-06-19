package com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.client;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.skku.jiwon_hae.graduate_project_android_streaming.R;
import com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.client.live_adapter.live_list_adapter;
import com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.client.live_adapter.live_list_items;

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
import java.util.ArrayList;

/**
 * Created by jiwon_hae on 2017. 9. 24..
 */

public class live_list extends Fragment {
    private ListView liveList;
    private live_list_adapter lAdapter;
    private ArrayList<live_list_items> streams;
    private TextView streamTitle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_live_list, container, false);
        streamTitle = (TextView)view.findViewById(R.id.stream_title);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        streams = new ArrayList<>();
        liveList = (ListView)view.findViewById(R.id.streamer_liveList);
        lAdapter = new live_list_adapter(getContext(), streams);
        liveList.setAdapter(lAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        lAdapter.clear();
        new get_streamers_onLive().execute();
    }

    public class get_streamers_onLive extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... params) {
            String targetURL = "http://13.125.170.236/streaming_application/redis/streaming/streamers_onLive_list.php";

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
                    Log.e("jsonArray",jsonArray.toString());

                    for(int i =0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        live_list_items items = new live_list_items();

                        items.stream_title = object.getString("title");

                        if(object.has("tags")){
                            items.stream_tags = object.getString("tags");
                        }else {
                            items.stream_tags ="";
                        }

                        items.stream_current_viewer = object.getInt("viewer_count");
                        items.streamer = object.getString("streamer");

                        lAdapter.add(0, items);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
