package com.skku.jiwon_hae.graduate_project_android_streaming.image;

import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.display.DisplayManager;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.view.SurfaceView;

import com.pedro.encoder.audio.AudioEncoder;
import com.pedro.encoder.audio.GetAacData;
import com.pedro.encoder.input.audio.GetMicrophoneData;
import com.pedro.encoder.input.audio.MicrophoneManager;
import com.pedro.encoder.input.video.GetCameraData;
import com.pedro.encoder.video.FormatVideoEncoder;
import com.pedro.encoder.video.GetH264Data;
import com.pedro.encoder.video.VideoEncoder;

import java.io.IOException;
import java.nio.ByteBuffer;

import static android.content.Context.MEDIA_PROJECTION_SERVICE;

/**
 * Created by jiwon_hae on 2017. 9. 27..
 */

public abstract class streaming_setting implements GetAacData, GetCameraData, GetH264Data, GetMicrophoneData {

    protected Context context;
    private MediaProjection mediaProjection;
    private MediaProjectionManager mediaProjectionManager;
    protected VideoEncoder videoEncoder;
    protected MicrophoneManager microphoneManager;
    protected AudioEncoder audioEncoder;
    private boolean streaming;
    protected SurfaceView surfaceView;
    private boolean videoEnabled = true;
    //record
    private MediaMuxer mediaMuxer;
    private int videoTrack = -1;
    private int audioTrack = -1;
    private boolean recording = false;
    private boolean canRecord = false;
    private MediaFormat videoFormat;
    private MediaFormat audioFormat;
    private int dpi = 320;

    public streaming_setting(Context context) {
        this.context = context;
        mediaProjectionManager =
                ((MediaProjectionManager) context.getSystemService(MEDIA_PROJECTION_SERVICE));
        this.surfaceView = null;
        videoEncoder = new VideoEncoder(this);
        microphoneManager = new MicrophoneManager(this);
        audioEncoder = new AudioEncoder(this);
        streaming = false;
    }

    protected abstract void prepareAudioRtp(boolean isStereo, int sampleRate);

    public boolean prepareAudio(int bitrate, int sampleRate, boolean isStereo, boolean echoCanceler,
                                boolean noiseSuppressor) {
        microphoneManager.createMicrophone(sampleRate, isStereo, echoCanceler, noiseSuppressor);
        prepareAudioRtp(isStereo, sampleRate);
        return audioEncoder.prepareAudioEncoder(bitrate, sampleRate, isStereo);
    }

    public boolean prepareVideo(int width, int height, int fps, int bitrate, boolean hardwareRotation,
                                int rotation) {
        return prepareVideo(width, height, fps, bitrate, hardwareRotation, 2, rotation);
    }

    public boolean prepareVideo(int width, int height, int fps, int bitrate, boolean hardwareRotation,
                                int iFrameInterval, int rotation) {

        int imageFormat = ImageFormat.NV21; //supported nv21 and yv12

        return videoEncoder.prepareVideoEncoder(width, height, fps, bitrate, rotation,
                hardwareRotation, iFrameInterval, FormatVideoEncoder.SURFACE);
    }

    /*Need be called while stream*/
    public void startRecord(String path) throws IOException {
        if (streaming) {
            mediaMuxer = new MediaMuxer(path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            videoTrack = mediaMuxer.addTrack(videoFormat);
            audioTrack = mediaMuxer.addTrack(audioFormat);
            mediaMuxer.start();
            recording = true;
        } else {
            throw new IOException("Need be called while stream");
        }
    }

    public void stopRecord() {
        recording = false;
        canRecord = false;
        if (mediaMuxer != null) {
            mediaMuxer.stop();
            mediaMuxer.release();
            mediaMuxer = null;
        }
        videoTrack = -1;
        audioTrack = -1;
    }

    public abstract boolean prepareAudio();

    protected abstract void startStreamRtp(String url);

    public Intent sendIntent() {
        return mediaProjectionManager.createScreenCaptureIntent();
    }

    public void startStream(String url, int resultCode, Intent data) {
        videoEncoder.start();
        audioEncoder.start();
        mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
        mediaProjection.createVirtualDisplay("Stream Display", videoEncoder.getWidth(),
                videoEncoder.getHeight(), dpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY, videoEncoder.getInputSurface(), null, null);
        microphoneManager.start();
        streaming = true;
        startStreamRtp(url);
    }

    protected abstract void stopStreamRtp();

    public void stopStream() {
        microphoneManager.stop();
        if (mediaProjection != null) {
            mediaProjection.stop();
        }
        stopStreamRtp();
        videoEncoder.stop();
        audioEncoder.stop();
        streaming = false;
    }

    public void disableAudio() {
        microphoneManager.mute();
    }

    public void enableAudio() {
        microphoneManager.unMute();
    }

    public boolean isAudioMuted() {
        return microphoneManager.isMuted();
    }

    public boolean isVideoEnabled() {
        return videoEnabled;
    }

    public void disableVideo() {
        videoEncoder.startSendBlackImage();
        videoEnabled = false;
    }

    public void enableVideo() {
        videoEncoder.stopSendBlackImage();
        videoEnabled = true;
    }

    /** need min API 19 */
    public void setVideoBitrateOnFly(int bitrate) {
        if (Build.VERSION.SDK_INT >= 19) {
            videoEncoder.setVideoBitrateOnFly(bitrate);
        }
    }

    public boolean isStreaming() {
        return streaming;
    }

    protected abstract void getAacDataRtp(ByteBuffer aacBuffer, MediaCodec.BufferInfo info);

    @Override
    public void getAacData(ByteBuffer aacBuffer, MediaCodec.BufferInfo info) {
        if (recording && canRecord) {
            mediaMuxer.writeSampleData(audioTrack, aacBuffer, info);
        }
        getAacDataRtp(aacBuffer, info);
    }

    protected abstract void onSPSandPPSRtp(ByteBuffer sps, ByteBuffer pps);

    @Override
    public void onSPSandPPS(ByteBuffer sps, ByteBuffer pps) {
        onSPSandPPSRtp(sps, pps);
    }

    protected abstract void getH264DataRtp(ByteBuffer h264Buffer, MediaCodec.BufferInfo info);

    @Override
    public void getH264Data(ByteBuffer h264Buffer, MediaCodec.BufferInfo info) {
        if (recording) {
            if (info.flags == MediaCodec.BUFFER_FLAG_KEY_FRAME) canRecord = true;
            if (canRecord) {
                mediaMuxer.writeSampleData(videoTrack, h264Buffer, info);
            }
        }
        getH264DataRtp(h264Buffer, info);
    }

    @Override
    public void onVideoFormat(MediaFormat mediaFormat) {
        videoFormat = mediaFormat;
    }

    @Override
    public void onAudioFormat(MediaFormat mediaFormat) {
        audioFormat = mediaFormat;
    }

}
