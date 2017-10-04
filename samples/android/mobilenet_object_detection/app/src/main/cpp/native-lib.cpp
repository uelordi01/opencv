
#include <string>
#include "native-lib.h"


#include <string>
#include <vector>

#include <android/log.h>
#include "opencv2/highgui.hpp"
#include <opencv2/dnn.hpp>
#include <opencv2/dnn/shape_utils.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/highgui.hpp>
#define LOG_TAG "JNI_DNN_LAYER"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))

using namespace std;
using namespace cv;
DnnConfiguration mDnnConfig;
dnn::Net neuralNetwork;
inline void vector_Rect_to_Mat(vector<Rect>& v_rect, Mat& mat)
{
    mat = Mat(v_rect, true);
}
#include <jni.h>

JNIEXPORT jlong JNICALL
Java_org_opencv_samples_mobilenet_ObjectDetectionHandler_createNativeObject(
        JNIEnv
        *env, jclass type,
        jstring filePath_,
        jstring neuralNet_,
        jstring deploy_)
{

    const char *neuralNet = env->GetStringUTFChars( neuralNet_, 0);
    const char *deploy =    env->GetStringUTFChars(deploy_, 0);
    const char *filePath =  env->GetStringUTFChars(filePath_, 0);
    std::string netFile = std::string(filePath) +"/"+ std::string(neuralNet);
    std::string weigths = std::string(filePath) +"/"+ std::string(deploy);
// TODO
    LOGD("the current loading files: \n"
                 "%s \n"
                 "%s \n", netFile.c_str(), weigths.c_str());
    neuralNetwork = cv::dnn::readNetFromCaffe(netFile, weigths);
    LOGD("neural network is loaded succesfully");
    env->ReleaseStringUTFChars(neuralNet_, neuralNet);
    env->ReleaseStringUTFChars(deploy_, deploy);
    env->ReleaseStringUTFChars(filePath_, filePath);
    return 0;
}

JNIEXPORT void JNICALL
Java_org_opencv_samples_mobilenet_ObjectDetectionHandler_nativeStart(JNIEnv *env, jclass type,
                                                                     jlong thiz) {

    // TODO

}

JNIEXPORT void JNICALL
Java_org_opencv_samples_mobilenet_ObjectDetectionHandler_nativeStop(JNIEnv *env, jclass type,
                                                                    jlong thiz) {

    // TODO

}

JNIEXPORT void JNICALL
Java_org_opencv_samples_mobilenet_ObjectDetectionHandler_processFrame(JNIEnv *env, jclass type,
                                                                           jlong thiz,
                                                                           jlong inputFrame,
                                                                           jlong faces) {

    // TODO
    cv::Mat &inputImg = *(Mat *)inputFrame;
    Mat inputBlob = cv::dnn::blobFromImage(inputImg, mDnnConfig.inScaleFactor,
    Size(mDnnConfig.inWidth, mDnnConfig.inHeight), mDnnConfig.meanVal, false);

    Size cropSize;
    if (inputImg.cols / (float)inputImg.rows > mDnnConfig.WHRatio)
    {
        cropSize = Size(static_cast<int>(inputImg.rows * mDnnConfig.WHRatio),
                        inputImg.cols);
    }
    else
    {
        cropSize = Size(inputImg.cols,
                        static_cast<int>(inputImg.cols / mDnnConfig.WHRatio));
    }

    Rect crop(Point((inputImg.cols - cropSize.width) / 2,
                    (inputImg.rows - cropSize.height) / 2),
              cropSize);
    //! [Set input blob]
    neuralNetwork.setInput(inputBlob, "data"); //set the network input
    //! [Set input blob]

    //! [Make forward pass]
    Mat detection = neuralNetwork.forward("detection_out"); //compute output
    //! [Make forward pass]

    std::vector<double> layersTimings;
    double freq = getTickFrequency() / 1000;
    //TODO DEFINE YOUR TIME FRECUENCY USING TIMINGS:
    //    double time = neuralNetwork.getPerfProfile(layersTimings) / freq;
    //    neuralNetwork << "Inference time, ms: " << time << endl;
    Mat detectionMat(detection.size[2], detection.size[3], CV_32F, detection.ptr<float>());
    inputImg = inputImg(crop);

    float confidenceThreshold = 0.2f;
    for(int i = 0; i < detectionMat.rows; i++)
    {
        float confidence = detectionMat.at<float>(i, 2);

        if(confidence > confidenceThreshold)
        {
            size_t objectClass = (size_t)(detectionMat.at<float>(i, 1));

            int xLeftBottom = static_cast<int>(detectionMat.at<float>(i, 3) * inputImg.cols);
            int yLeftBottom = static_cast<int>(detectionMat.at<float>(i, 4) * inputImg.rows);
            int xRightTop = static_cast<int>(detectionMat.at<float>(i, 5) * inputImg.cols);
            int yRightTop = static_cast<int>(detectionMat.at<float>(i, 6) * inputImg.rows);

            ostringstream ss;
            ss << confidence;
            String conf(ss.str());

            Rect object((int)xLeftBottom, (int)yLeftBottom,
                        (int)(xRightTop - xLeftBottom),
                        (int)(yRightTop - yLeftBottom));

            rectangle(inputImg, object, Scalar(0, 255, 0));
            String label = String(mDnnConfig.classNames[objectClass]) + ": " + conf;
            int baseLine = 0;
            Size labelSize = getTextSize(label, FONT_HERSHEY_SIMPLEX, 0.5, 1, &baseLine);
            rectangle(inputImg, Rect(Point(xLeftBottom, yLeftBottom - labelSize.height),
                                  Size(labelSize.width, labelSize.height + baseLine)),
                      Scalar(255, 255, 255), CV_FILLED);
            putText(inputImg, label, Point(xLeftBottom, yLeftBottom),
                    FONT_HERSHEY_SIMPLEX, 0.5, Scalar(0,0,0));
        }
    }
}

JNIEXPORT jlong JNICALL
Java_org_opencv_samples_mobilenet_ObjectDetectionHandler_destroyNativeObject(JNIEnv *env,
                                                                             jclass type,
                                                                             jlong thiz) {

    // TODO

}
JNIEXPORT void JNICALL
Java_org_opencv_samples_mobilenet_ObjectDetectionHandler_setConfiguration(JNIEnv *env, jclass type,
                                                                          jint inWidth,
                                                                          jint inHeight,
                                                                          jfloat WHRatio,
                                                                          jfloat inScaleFactor,
                                                                          jfloat meanVal,
                                                                          jfloat minConfidence,
                                                                          jobjectArray dictionary) {
    mDnnConfig.inWidth = inWidth;
    mDnnConfig.inHeight = inHeight;
    mDnnConfig.WHRatio = WHRatio;
    mDnnConfig.inScaleFactor = inScaleFactor;
    mDnnConfig.meanVal = meanVal;
    mDnnConfig.minConfidence = minConfidence;
    int size = env->GetArrayLength(dictionary);
    mDnnConfig.classNames.clear();
    for (int i=0; i < size; ++i) {
        jstring string = (jstring) env->GetObjectArrayElement(dictionary, i);
        const char *myarray = env->GetStringUTFChars(string, 0);
        mDnnConfig.classNames.push_back(std::string(myarray));
        env->ReleaseStringUTFChars(string, myarray);
        env->DeleteLocalRef(string);
    }
}
//JNIEXPORT void JNICALL Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeDetect
//        (JNIEnv * jenv, jclass, jlong thiz, jlong imageGray, jlong faces)
//{
//    LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeDetect");
//
//    try
//    {
//        vector<Rect> RectFaces;
//        cv::Mat &gray =*(Mat *) imageGray;
//        ((DetectorAgregator*)thiz)->tracker->process(gray);
//        ((DetectorAgregator*)thiz)->tracker->getObjects(RectFaces);
//        *((Mat*)faces) = Mat(RectFaces, true);
//    }
//    catch(cv::Exception& e)
//    {
//        LOGD("nativeCreateObject caught cv::Exception: %s", e.what());
//        jclass je = jenv->FindClass("org/opencv/core/CvException");
//        if(!je)
//            je = jenv->FindClass("java/lang/Exception");
//        jenv->ThrowNew(je, e.what());
//    }
//    catch (...)
//    {
//        LOGD("nativeDetect caught unknown exception");
//        jclass je = jenv->FindClass("java/lang/Exception");
//        jenv->ThrowNew(je, "Unknown exception in JNI code DetectionBasedTracker.nativeDetect()");
//    }
//    LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeDetect END");
//}

//JNIEXPORT void JNICALL
//Java_org_opencv_samples_facedetect_FdActivity_rotateFlipImage(JNIEnv *env, jclass type,
//                                                              jint flip,
//                                                              jint transpose,
//                                                              jlong imgPointerIn) {
//    LOGD("ROTATING AND FLIPING IMAGE");
//    cv::Mat  &inMat =*(Mat *) imgPointerIn;
//    cv::Mat  outMat;
//    inMat.copyTo(outMat);
//
////    cv::transpose(inMat, inMat);
////    if((int) transpose == 1) {
//       cv::transpose(outMat, outMat);
////    }
//    cv::flip(outMat, outMat,(int) flip);
////    inMat = outMat;
//
