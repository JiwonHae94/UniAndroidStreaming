package com.skku.jiwon_hae.graduate_project_android_streaming.image;

import android.content.Context;
import android.media.MediaCodec;

import com.pedro.encoder.input.video.Frame;

import net.ossrs.rtmp.ConnectCheckerRtmp;
import net.ossrs.rtmp.SrsFlvMuxer;

import java.nio.ByteBuffer;

/**
 * Created by jiwon_hae on 2017. 9. 27..
 */

public class streaming_display extends streaming_setting {

    private SrsFlvMuxer srsFlvMuxer;

    public streaming_display(Context context, ConnectCheckerRtmp connectChecker) {
        super(context);
        srsFlvMuxer = new SrsFlvMuxer(connectChecker);
    }

    @Override
    protected void prepareAudioRtp(boolean isStereo, int sampleRate) {
        srsFlvMuxer.setIsStereo(isStereo);
        srsFlvMuxer.setSampleRate(sampleRate);
    }

    @Override
    public boolean prepareAudio() {
        microphoneManager.createMicrophone();
        return audioEncoder.prepareAudioEncoder();
    }

    @Override
    protected void startStreamRtp(String url) {
        if (videoEncoder.getRotation() == 90 || videoEncoder.getRotation() == 270) {
            srsFlvMuxer.setVideoResolution(videoEncoder.getHeight(), videoEncoder.getWidth());
        } else {
            srsFlvMuxer.setVideoResolution(videoEncoder.getWidth(), videoEncoder.getHeight());
        }
        srsFlvMuxer.start(url);
    }

    @Override
    protected void stopStreamRtp() {
        srsFlvMuxer.stop();
    }

    @Override
    protected void getAacDataRtp(ByteBuffer aacBuffer, MediaCodec.BufferInfo info) {
        srsFlvMuxer.sendAudio(aacBuffer, info);
    }

    @Override
    protected void onSPSandPPSRtp(ByteBuffer sps, ByteBuffer pps) {
        srsFlvMuxer.setSpsPPs(sps, pps);
    }

    @Override
    protected void getH264DataRtp(ByteBuffer h264Buffer, MediaCodec.BufferInfo info) {
        srsFlvMuxer.sendVideo(h264Buffer, info);
    }

    @Override
    public void inputPCMData(byte[] bytes, int i) {

    }

    @Override
    public void inputYUVData(Frame frame) {

    }
}

