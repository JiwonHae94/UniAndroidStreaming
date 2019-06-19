package com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.chatting;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.skku.jiwon_hae.graduate_project_android_streaming.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by jiwon_hae on 2017. 10. 11..
 */

public class chatting_adapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<chat_items> chat_list;
    private LayoutInflater layoutInflater;
    private String translation_requested;

    public chatting_adapter(Context context, ArrayList<chat_items> chat_list){
        this.mContext = context;
        this.chat_list = chat_list;
        this.layoutInflater = LayoutInflater.from(context);
        this.translation_requested = "NULL";
    }

    @Override
    public int getCount() {
        return chat_list.size();
    }

    @Override
    public Object getItem(int position) {
        return chat_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class Viewholder{
        TextView username;
        TextView chat_content;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.chat_item, null);

        Viewholder h = new Viewholder();

        h.username = (TextView)convertView.findViewById(R.id.chat_user);
        h.chat_content = (TextView)convertView.findViewById(R.id.chat_content);

        h.username.setText(chat_list.get(position).getUsername());
        h.chat_content.setText(chat_list.get(position).getContents());

        return convertView;
    }

    public void add(chat_items item){
        if(item.getType().equals("message")){
            if(translation_requested.equals("NULL")){
                chat_list.add(chat_list.size(), item);
            }else{
                boolean isEng = false;
                boolean isKor = false;

                String source_lang = "ko";

                for(int i = 0; i < item.getContents().length() ; i++){
                    if((i+1) <= item.getContents().length()){
                        if(Pattern.matches("[a-z]", item.getContents().substring(i, i+1)) || Pattern.matches("[A-Z]", item.getContents().substring(i))){
                            isEng = true;
                        }
                        if(Pattern.matches("[가-힣]", item.getContents().substring(i, i+1))){
                            isKor = true;
                        }
                    }
                }

                if(isEng){
                    source_lang = "en";
                }else if(isKor){
                    source_lang = "ko";
                }

                if(source_lang.equals(translation_requested)){
                    chat_list.add(chat_list.size(), item);
                }else{
                    new requestTranslation().execute(item, source_lang ,translation_requested);
                }

                Log.e(source_lang, translation_requested);
            }
        }
        notifyDataSetChanged();
    }

    public void setLanguageOption(String lang){
        this.translation_requested = lang;
    }

    private chat_items translated_item;

    //Google
    private class requestTranslation extends AsyncTask<Object, Void, String> {
        private chat_items item ;
        private String source_lang;
        private String target_lang;

        private final static String URL = "https://www.googleapis.com/language/translate/v2?key=";
        private final static String KEY = "AIzaSyAAkEvlaqb-ocDstQWRojB8IFtP5BY0DOI";
        private String TARGET = "&target=";
        private String SOURCE = "&source=";
        private final static String QUERY = "&q=";

        @Override
        protected String doInBackground(Object... params) {
            item = (chat_items)params[0];
            source_lang = (String)params[1];
            target_lang = (String)params[2];

            TARGET = TARGET + target_lang;
            SOURCE = SOURCE + source_lang;

            String word = item.getContents();

            StringBuilder result = new StringBuilder();

            try {
                String encodedText = URLEncoder.encode(word, "UTF-8");
                URL url = new URL(URL + KEY + SOURCE + TARGET + QUERY + encodedText);

                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                InputStream stream;

                if(conn.getResponseCode() == 200){
                    stream = conn.getInputStream();
                }else{
                    stream = conn.getErrorStream();
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line;

                while((line = reader.readLine()) != null){
                    result.append(line);
                }

                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                JSONObject jsonObject1 = new JSONObject(String.valueOf(jsonObject.get("data")));
                JSONArray jsonArray = (JSONArray) jsonObject1.get("translations");

                JSONObject translated_word = new JSONObject(String.valueOf(jsonArray.get(0)));
                String translated_outcome = String.valueOf(translated_word.get("translatedText"));

                return translated_outcome;

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s.isEmpty()){
                chat_list.add(chat_list.size(), item);
            }else{
                item.setChatContent(s);
                chat_list.add(chat_list.size(), item);
            }
            notifyDataSetChanged();
        }
    }
}
