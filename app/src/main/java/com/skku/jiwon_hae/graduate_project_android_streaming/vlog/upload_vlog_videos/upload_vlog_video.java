package com.skku.jiwon_hae.graduate_project_android_streaming.vlog.upload_vlog_videos;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jiwon_hae on 2017. 10. 30..
 */

public class upload_vlog_video extends Service {
    private String upload_serverUri = "http://13.125.170.236/streaming_application/redis/vlog/vlog_upload.php";
    private String fileName;
    private String file_uri;

    private int serverResponseCode = 0;
    int bytesRead, bytesAvailable, bufferSize;
    int maxBufferSize = 1 * 1024 * 1024;

    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";

    private String userName;
    private String registrationNo;
    private String tags;
    private int startId;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.startId = startId;

        if(intent != null) {
            if (intent.hasExtra("vlog_addr") && intent.hasExtra("userName") && intent.hasExtra("vlog_name")) {
                fileName = intent.getStringExtra("vlog_name");
                file_uri = intent.getStringExtra("vlog_addr");

                userName = intent.getStringExtra("userName");

                if (intent.hasExtra("tags")) {
                    tags = intent.getStringExtra("tags");
                } else {
                    tags = "";
                }
                registrationNo = userName + "_" + System.currentTimeMillis();

                new Thread(new Runnable() {
                    public void run() {
                        uploadFile(file_uri);
                    }
                }).start();
            }
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public int uploadFile(String sourceFileUri) {
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {

            return 0;

        } else {
            try {
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upload_serverUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name='uploaded_file';filename='"
                        + fileName + "'" + lineEnd);
                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }
                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){
                    Log.e("vlog_upload", "done");

                    //thumbnail_name
                    String thumbnail_name = registrationNo+".jpg";

                    //thumbnail_folder
                    File thumbnail_folder = new File("sdcard/nova_streaming/VLOG/thumbnail");

                    if(!thumbnail_folder.exists()) {
                        thumbnail_folder.mkdirs();
                    }

                    //create a file to write bitmap data
                    File thumbnail_file = new File(thumbnail_folder+ "/"+thumbnail_name);
                    thumbnail_file.createNewFile();

                    //Convert bitmap to byte array
                    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(file_uri,
                            MediaStore.Images.Thumbnails.MINI_KIND);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , bos);
                    byte[] bitmapdata = bos.toByteArray();

                    //write the bytes in file
                    FileOutputStream fos = new FileOutputStream(thumbnail_file);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();

                    Intent upload_vlog_thumbnail = new Intent(upload_vlog_video.this, upload_vlog_thumbnail.class);
                    upload_vlog_thumbnail.putExtra("userName", userName);
                    upload_vlog_thumbnail.putExtra("thumbnail_addr", thumbnail_file.getAbsolutePath());
                    upload_vlog_thumbnail.putExtra("thumbnail_name", thumbnail_name);
                    upload_vlog_thumbnail.putExtra("vlog_name", fileName);
                    upload_vlog_thumbnail.putExtra("userName", userName);
                    upload_vlog_thumbnail.putExtra("tags", tags);
                    upload_vlog_thumbnail.putExtra("registrationNo", registrationNo);
                    startService(upload_vlog_thumbnail);

                }else{
                    Toast.makeText(upload_vlog_video.this, "VLOG 업로드에 문제가 생겼습니다. 나중에 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                    this.stopSelf(startId);
                }

                fileInputStream.close();
                dos.flush();
                dos.close();

                this.stopSelf(startId);

            } catch (MalformedURLException ex) {
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                Log.e("Upload file to server", "Exception : "
                        + e.getMessage(), e);
            }
            return serverResponseCode;

        } // End else block
    }
}
