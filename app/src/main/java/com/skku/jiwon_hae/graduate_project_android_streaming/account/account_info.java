package com.skku.jiwon_hae.graduate_project_android_streaming.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skku.jiwon_hae.graduate_project_android_streaming.R;
import com.skku.jiwon_hae.graduate_project_android_streaming.image.transform.CircleTransform;
import com.skku.jiwon_hae.graduate_project_android_streaming.utils.useSharedPreference;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jiwon_hae on 2017. 9. 24..
 */

public class account_info extends Fragment {
    private Button login;
    private LinearLayout has_login_user;
    private LinearLayout no_login_user;

    //UserInformation
    private useSharedPreference useDB;
    private String userInformation;
    private String user_email;
    private String userName;

    //userInformation
    private ImageView user_profileImage;
    private TextView user_id_textView;
    private TextView userName_textView;

    private Button logout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_account_info, container, false);
        login = (Button)view.findViewById(R.id.to_account);

        has_login_user = (LinearLayout)view.findViewById(R.id.has_login_user);
        no_login_user = (LinearLayout)view.findViewById(R.id.no_login_user);

        useDB = new useSharedPreference(getContext());
        userInformation = useDB.get_user_information();

        user_profileImage = (ImageView)view.findViewById(R.id.userProfilePic);
        user_id_textView = (TextView)view.findViewById(R.id.userId);
        userName_textView = (TextView)view.findViewById(R.id.userName);

        logout = (Button)view.findViewById(R.id.logout_btn);
        
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(userInformation.equals("")){
            if(no_login_user.getVisibility() != View.VISIBLE){
                no_login_user.setVisibility(View.VISIBLE);
            }

            if(has_login_user.getVisibility() != View.GONE){
                has_login_user.setVisibility(View.GONE);
            }
        }else{
            if(no_login_user.getVisibility() != View.GONE){
                no_login_user.setVisibility(View.GONE);
            }

            if(has_login_user.getVisibility() != View.VISIBLE){
                has_login_user.setVisibility(View.VISIBLE);
            }

            try {
                JSONObject jsonObject = new JSONObject(userInformation);
                userName = jsonObject.get("userName").toString();
                user_email = jsonObject.get("user_id").toString();

                user_id_textView.setText(user_email);
                userName_textView.setText(userName);

                Picasso.with(getContext())
                        .load("http://bdc01051.cafe24.com/profile_pic/" + userName+ ".jpg")
                        .fit()
                        .transform(new CircleTransform())
                        .into(user_profileImage);

                logout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "logout", Toast.LENGTH_SHORT).show();

                        if(no_login_user.getVisibility() != View.VISIBLE){
                            no_login_user.setVisibility(View.VISIBLE);
                        }

                        if(has_login_user.getVisibility() != View.GONE){
                            has_login_user.setVisibility(View.GONE);
                        }

                        useDB.logout_from_app();

                        if(no_login_user.getVisibility() != View.VISIBLE){
                            no_login_user.setVisibility(View.VISIBLE);
                        }

                        if(has_login_user.getVisibility() != View.GONE){
                            has_login_user.setVisibility(View.GONE);
                        }
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent to_create_acc = new Intent(getActivity(), create_account.class);
                startActivity(to_create_acc);
            }
        });
    }
}