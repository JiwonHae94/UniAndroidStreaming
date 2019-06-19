package com.skku.jiwon_hae.graduate_project_android_streaming.image.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.skku.jiwon_hae.graduate_project_android_streaming.R;
import com.skku.jiwon_hae.graduate_project_android_streaming.streamer_client.streamer.streaming;

/**
 * Created by jiwon_hae on 2017. 10. 30..
 */

public class gallery extends Fragment {

    private String TAG;
    private Button cancel;
    private Button next;

    private GridView gridView;
    private ImageView imageView;

    private gallery_adapter galleryAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gallery_display, container, false);

        gridView = (GridView)rootView.findViewById(R.id.gallery_grid);
        imageView = (ImageView)rootView.findViewById(R.id.image_preview);
        next = (Button)rootView.findViewById(R.id.next);

        if(getActivity().getIntent().hasExtra("TAG")){
            TAG = getActivity().getIntent().getStringExtra("TAG");
        }

        galleryAdapter = new gallery_adapter(getContext(), getActivity(), imageView, TAG, next);

        cancel = (Button)rootView.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent to_review = new Intent(getActivity(), streaming.class);
                to_review.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                to_review.putExtra("fragment", "review");
                startActivity(to_review);
            }
        });

        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gridView.setAdapter(galleryAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().finish();
    }
}
