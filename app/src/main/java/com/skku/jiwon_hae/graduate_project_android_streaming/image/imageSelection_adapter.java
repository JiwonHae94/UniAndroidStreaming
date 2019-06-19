package com.skku.jiwon_hae.graduate_project_android_streaming.image;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.skku.jiwon_hae.graduate_project_android_streaming.image.camera.capture.capture_image1;
import com.skku.jiwon_hae.graduate_project_android_streaming.image.gallery.gallery;

/**
 * Created by jiwon_hae on 2017. 10. 30..
 */

public class imageSelection_adapter extends FragmentPagerAdapter {

    public imageSelection_adapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new gallery();
            case 1:
                return new capture_image1();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "라이브러리";
            case 1:
                return "카메라";
        }
        return null;
    }
}
