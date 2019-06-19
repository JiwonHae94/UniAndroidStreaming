package com.skku.jiwon_hae.graduate_project_android_streaming.vlog;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;

import com.skku.jiwon_hae.graduate_project_android_streaming.R;
import com.skku.jiwon_hae.graduate_project_android_streaming.vlog.record.vlog_recording;
import com.skku.jiwon_hae.graduate_project_android_streaming.vlog.vlog_adapter.vlog_adapter;
import com.skku.jiwon_hae.graduate_project_android_streaming.vlog.vlog_adapter.vlog_items;

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
 * Created by jiwon_hae on 2017. 10. 30..
 */

public class vlog extends Fragment {

    private ImageButton add_vlog;
    private GridView vlog_gridView;
    private vlog_adapter vAdapter;
    private ArrayList<vlog_items> vlog_itemList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_vlog, container, false);

        vlog_gridView = (GridView)view.findViewById(R.id.vlog_grid);
        add_vlog = (ImageButton)view.findViewById(R.id.add_vlog);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        add_vlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent to_add_vlog = new Intent(getContext(), vlog_recording.class);
                to_add_vlog.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(to_add_vlog);
            }
        });

        vAdapter = new vlog_adapter(getContext(), getActivity());
        vlog_itemList = new ArrayList<>();
        vlog_gridView.setAdapter(vAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        new get_all_vlogs().execute();
    }

    public class get_all_vlogs extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... params) {
            String targetURL = "http://13.125.170.236/streaming_application/vlog/vlog_getAll.php";

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

                    for(int i =0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        vlog_items items = new vlog_items();

                        items.vlogAddr = object.getString("vlog");
                        items.vlog_userName = object.getString("uploader");
                        items.tags = object.getString("tags");
                        items.registration_code = object.getString("registration");
                        items.likes = object.getInt("likes");

                        vAdapter.add(0, items);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
