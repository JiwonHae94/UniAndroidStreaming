#include <jni.h>
#include <string>

#include <iostream>
#include <opencv2/imgproc.hpp>
#include <opencv2/opencv.hpp>
#include <android/log.h>

using namespace cv;
using namespace std;

extern "C" {

Mat load_masks(Mat mask_mat, int mask_type) {
    switch(mask_type){

        case 1:{
            mask_mat = imread("/storage/emulated/0/Pictures/nova_streaming/MASKS/mask_dog1.jpg", COLOR_BGR2RGB);
        }break;

        case 2:{
            mask_mat = imread("/storage/emulated/0/Pictures/nova_streaming/MASKS/mask_dog2.jpg", COLOR_BGR2RGB);
        }break;

        case 3:{
            mask_mat = imread("/storage/emulated/0/Pictures/nova_streaming/MASKS/mask_cat1.jpg", COLOR_BGR2RGB);
        }break;

        case 4:{
            mask_mat = imread("/storage/emulated/0/Pictures/nova_streaming/MASKS/mask_cat2.jpg", COLOR_BGR2RGB);
        }break;

        case 5:{
            mask_mat = imread("/storage/emulated/0/Pictures/nova_streaming/MASKS/mask_rabbit.jpg", COLOR_BGR2RGB);
        }break;

        case 6:{
            mask_mat = imread("/storage/emulated/0/Pictures/nova_streaming/MASKS/mask_mice.jpg", COLOR_BGR2RGB);
        }break;

    }
    return mask_mat;
}
}

extern "C"{
Mat putMask(Mat src, Point center, Size face_size, int mask_type){

    Mat mask_mat = Mat();
    Mat mask = load_masks(mask_mat, mask_type);
    Mat mask1;

    Mat src1;

    cvtColor(src, src, IMREAD_COLOR);
    resize(mask, mask1, face_size);

    int center_x;
    int center_y;


    switch(mask_type){
        case 1:{
            center_x = center.x - face_size.width / 2;
            center_y = center.y - face_size.width * 0.48;
        }break;

        case 2:  case 3: case 4:case 5: {
            center_x = center.x - face_size.width * 0.48;
            center_y = center.y - face_size.width * 0.7;
        }break;

        case 6:{
            center_x = center.x - face_size.width / 2;
            center_y = center.y - face_size.width * 0.65;
        }break;
    }


    Rect roi( center_x, center_y, face_size.width, face_size.height );
    src(roi).copyTo(src1);

    cvtColor(src1, src1, COLOR_BGR2RGB);

    Mat mask2, m, m1;
    cvtColor( mask1, mask2, COLOR_BGR2GRAY);
    threshold( mask2, mask2, 230, 255, THRESH_BINARY_INV );

    vector<Mat> maskChannels( 3 ), result_mask( 3 );
    split( mask1, maskChannels );
    bitwise_and( maskChannels[0], mask2, result_mask[0] );
    bitwise_and( maskChannels[1], mask2, result_mask[1] );
    bitwise_and( maskChannels[2], mask2, result_mask[2] );
    merge( result_mask, m );

    mask2 = 255 - mask2;
    vector<Mat> srcChannels( 3 );
    split(src1, srcChannels);
    bitwise_and( srcChannels[0], mask2, result_mask[0] );
    bitwise_and( srcChannels[1], mask2, result_mask[1] );
    bitwise_and( srcChannels[2], mask2, result_mask[2] );
    merge( result_mask, m1 );

    addWeighted( m, 1, m1, 1, 0, m1);

    Mat m2;
    cvtColor(m1, m2, COLOR_BGR2RGB);

    m2.copyTo( src (roi));

    return src;
}
}

float resize_mat(Mat &img_src, Mat &img_resize, int resize_width) {

    float scale = resize_width / (float) img_src.cols;

    if (img_src.cols > resize_width) {
        int new_height = cvRound(img_src.rows * scale);
        resize(img_src, img_resize, Size(resize_width, new_height));
    } else {
        img_resize = img_src;
    }
    return scale;
}





JNIEXPORT void JNICALL
Java_com_skku_jiwon_1hae_graduate_1project_1android_1streaming_vlog_record_vlog_1recording_captureImage(
        JNIEnv *env, jobject instance, jlong matAddrInput, jstring imageName_) {
    const char *imageName = env->GetStringUTFChars(imageName_, 0);

    // TODO
    env->ReleaseStringUTFChars(imageName_, imageName);

    Mat &img_input = *(Mat *) matAddrInput;
    Mat img_save;

    cvtColor(img_input, img_save, COLOR_BGR2RGB);
    imwrite(imageName, img_save);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_skku_jiwon_1hae_graduate_1project_1android_1streaming_image_filter_image_1filters_loadImage(
        JNIEnv *env, jobject instance, jstring imageFileName, jlong addrImage) {

    // TODO
    Mat &img_input = *(Mat *) addrImage;
    const char *pathDir = env->GetStringUTFChars(imageFileName, JNI_FALSE);

    img_input = imread(pathDir, IMREAD_COLOR);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_skku_jiwon_1hae_graduate_1project_1android_1streaming_image_filter_image_1filters_saveImage(
        JNIEnv *env, jobject instance, jlong inputImage, jstring imageName_, jboolean convert) {
    const char *imageName = env->GetStringUTFChars(imageName_, 0);
    env->ReleaseStringUTFChars(imageName_, imageName);

    // TODO
    Mat &img_input = *(Mat *) inputImage;
    Mat img_save;

    if(convert){
        cvtColor(img_input, img_save, COLOR_BGR2RGB);
    }else{
        img_save = img_input;
    }
    imwrite( imageName, img_save );
}





extern "C"
JNIEXPORT void JNICALL
Java_com_skku_jiwon_1hae_graduate_1project_1android_1streaming_image_filter_image_1filters_filter_1original(
        JNIEnv *env, jobject instance, jlong inputImage, jlong outputImage) {

    // TODO
    Mat &img_input = *(Mat *) inputImage;
    Mat &img_output = *(Mat *) outputImage;

    cvtColor(img_input, img_output, COLOR_BGR2RGB);

}extern "C"
JNIEXPORT void JNICALL
Java_com_skku_jiwon_1hae_graduate_1project_1android_1streaming_image_filter_image_1filters_filter_1canny(
        JNIEnv *env, jobject instance, jlong addrInputImage, jlong addrOutputImage) {

    Mat &img_input = *(Mat *) addrInputImage;
    Mat &img_output = *(Mat *) addrOutputImage;

    cvtColor( img_input, img_output, COLOR_RGB2GRAY);

    blur( img_output, img_output, Size(10,10) );
    Canny( img_output, img_output, 10, 150, 5 );

}extern "C"
JNIEXPORT void JNICALL
Java_com_skku_jiwon_1hae_graduate_1project_1android_1streaming_image_filter_image_1filters_filter_1negative(
        JNIEnv *env, jobject instance, jlong addrInputImage, jlong addrOutputImage) {

    // TODO
    Mat &img_input = *(Mat *) addrInputImage;
    Mat &img_output = *(Mat *) addrOutputImage;

    cvtColor( img_input, img_output, COLOR_RGBA2GRAY);

    Mat_<uchar>image(img_output);
    Mat_<uchar>destImage(img_output.size());

    for(int y = 0 ; y < image.rows ; y++){
        for(int x = 0  ; x < image.cols; x++){
            uchar r = image(y,x);
            destImage(y,x) = 255 -r;
        }
    }
    img_output = destImage.clone();

}extern "C"
JNIEXPORT void JNICALL
Java_com_skku_jiwon_1hae_graduate_1project_1android_1streaming_image_filter_image_1filters_filter_1blur(
        JNIEnv *env, jobject instance, jlong addrInputImage, jlong addrOutputImage) {

    // TODO
    Mat &img_input = *(Mat *) addrInputImage;
    Mat &img_output = *(Mat *) addrOutputImage;

    cvtColor(img_input, img_output, COLOR_BGR2RGB);
    blur( img_output, img_output, Size(100,100) );

}extern "C"
JNIEXPORT void JNICALL
Java_com_skku_jiwon_1hae_graduate_1project_1android_1streaming_image_filter_image_1filters_filter_1erode(
        JNIEnv *env, jobject instance, jlong addrInputImage, jlong addrOutputImage) {

    // TODO
    Mat &img_input = *(Mat *) addrInputImage;
    Mat &img_output = *(Mat *) addrOutputImage;

    int erosion_type = MORPH_RECT;
    int erosion_size = 3;

    Mat element = getStructuringElement( erosion_type,
                                         Size( 2*erosion_size + 1, 2*erosion_size+1 ),
                                         Point( erosion_size, erosion_size ) );

    cvtColor(img_input, img_output, COLOR_BGR2RGB);
    erode( img_output, img_output, element );

}extern "C"
JNIEXPORT void JNICALL
Java_com_skku_jiwon_1hae_graduate_1project_1android_1streaming_image_filter_image_1filters_filter_1dilate(
        JNIEnv *env, jobject instance, jlong addrInputImage, jlong addrOutputImage) {

    // TODO
    Mat &img_input = *(Mat *) addrInputImage;
    Mat &img_output = *(Mat *) addrOutputImage;

    int dilation_type = MORPH_RECT;
    int dilation_size = 5;

    Mat element = getStructuringElement( dilation_type,
                                         Size( 2*dilation_size + 1, 2*dilation_size+1 ),
                                         Point( dilation_size, dilation_size ) );

    cvtColor(img_input, img_output, COLOR_BGR2RGB);
    dilate( img_output, img_output, element );

}


extern "C"
JNIEXPORT void JNICALL
Java_com_skku_jiwon_1hae_graduate_1project_1android_1streaming_image_filter_image_1filters_changeBrightness(
        JNIEnv *env, jobject instance, jlong inputImage, jlong outputImage, double value) {

    // TODO

    Mat &img_input = *(Mat *) inputImage;
    Mat &img_output = *(Mat *) outputImage;

    img_output = Mat(img_input.rows, img_input.cols, img_input.type());
    img_input.convertTo(img_output, -1, value, 0);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_skku_jiwon_1hae_graduate_1project_1android_1streaming_vlog_record_vlog_1recording_loadCascade(
        JNIEnv *env, jobject instance, jstring cascadeFileName_) {
    const char *nativeFileNameString = env->GetStringUTFChars(cascadeFileName_, 0);

    string baseDir("/storage/emulated/0/");
    baseDir.append(nativeFileNameString);
    const char *pathDir = baseDir.c_str();

    jlong ret = 0;
    ret = (jlong) new CascadeClassifier(pathDir);

    env->ReleaseStringUTFChars(cascadeFileName_, nativeFileNameString);

    return ret;
}extern "C"
JNIEXPORT void JNICALL
Java_com_skku_jiwon_1hae_graduate_1project_1android_1streaming_image_filter_image_1filters_filter_1grey(
        JNIEnv *env, jobject instance, jlong addrInputImage, jlong addrOutputImage) {

    // TODO
    Mat &img_input = *(Mat *) addrInputImage;
    Mat &img_output = *(Mat *) addrOutputImage;

    cvtColor( img_input, img_output, COLOR_RGB2GRAY);

}extern "C"
JNIEXPORT void JNICALL
Java_com_skku_jiwon_1hae_graduate_1project_1android_1streaming_vlog_record_vlog_1recording_maskOn(
        JNIEnv *env, jobject instance, jint mask_type, jlong cascadeClassifier_face,
        jlong cascadeClassifier_eye, jlong matAddrInput, jlong matAddrResult) {

    // TODO
    Mat &img_input = *(Mat *) matAddrInput;
    Mat &img_result = *(Mat *) matAddrResult;

    std::vector<Rect> faces;
    Mat img_gray;

    cvtColor(img_input, img_gray, COLOR_BGR2GRAY);
    equalizeHist(img_gray, img_gray);

    Mat img_resize;
    float resizeRatio = resize_mat(img_gray, img_resize, 160);

    img_input.copyTo(img_result);

    ((CascadeClassifier *) cascadeClassifier_face)->detectMultiScale(img_resize, faces, 1.1, 3,
                                                                     0 | CASCADE_SCALE_IMAGE,
                                                                     Size(30, 30));

    if (mask_type != 0 && mask_type != 7) {
        for (int i = 0; i < faces.size(); i++) {
            double real_facesize_x = faces[i].x / resizeRatio;
            double real_facesize_y = faces[i].y / resizeRatio;
            double real_facesize_width = faces[i].width / resizeRatio;
            double real_facesize_height = faces[i].height / resizeRatio;

            Point center(real_facesize_x + real_facesize_width / 2,
                         real_facesize_y + real_facesize_height / 2);

            img_result = putMask(img_input, center, Size(real_facesize_width, real_facesize_height), mask_type);

            /*
            if(mask_type == 6){
                img_result = putMask(img_input, center, Size(real_facesize_width * 0.6 , real_facesize_height * 1.1), mask_type);
            }else if(mask_type == 3){
                img_result = putMask(img_input, center, Size(real_facesize_width , real_facesize_height), mask_type);
            }else if(mask_type == 5){
                img_result = putMask(img_input, center, Size(real_facesize_width * 1.3 , real_facesize_height * 1.1), mask_type);
            }else{
                img_result = putMask(img_input, center, Size(real_facesize_width * 1.3 , real_facesize_height*1.28), mask_type);
            }

             */

        }

    } else if (mask_type == 7) {
        //-- Detect faces
        img_input.copyTo(img_result);

        for (int i = 0; i < faces.size(); i++) {
            double real_facesize_x = faces[i].x / resizeRatio;
            double real_facesize_y = faces[i].y / resizeRatio;
            double real_facesize_width = faces[i].width / resizeRatio;
            double real_facesize_height = faces[i].height / resizeRatio;

            Point center(real_facesize_x + real_facesize_width / 2,
                         real_facesize_y + real_facesize_height / 2);

            ellipse(img_result, center, Size(real_facesize_width / 2, real_facesize_height / 2), 0,
                    0, 360,
                    Scalar(255, 0, 255), 30, 8, 0);

            Rect face_area(real_facesize_x, real_facesize_y, real_facesize_width,
                           real_facesize_height);
            Mat faceROI = img_gray(face_area);

            std::vector<Rect> eyes;

            //-- In each face, detect eyes
            ((CascadeClassifier *) cascadeClassifier_eye)->detectMultiScale(faceROI, eyes, 1.1, 2,
                                                                            0 | CASCADE_SCALE_IMAGE,
                                                                            Size(30, 30));

            for (size_t j = 0; j < eyes.size(); j++) {
                Point eye_center(real_facesize_x + eyes[j].x + eyes[j].width / 2,
                                 real_facesize_y + eyes[j].y + eyes[j].height / 2);
                int radius = cvRound((eyes[j].width + eyes[j].height) * 0.25);
                circle(img_result, eye_center, radius, Scalar(255, 0, 0), 30, 8, 0);
            }
        }
    } else {
        img_result = img_input;
    }

}
