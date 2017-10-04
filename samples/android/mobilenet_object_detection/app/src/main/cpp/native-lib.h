//
// Created by uelordi on 2/10/17.
//
#include <jni.h>

#include <opencv2/core.hpp>
#include <opencv2/objdetect.hpp>
#ifndef FACEDETECT_NATIVE_LIB_H
#define FACEDETECT_NATIVE_LIB_H

typedef struct configuration {
    size_t inWidth;
    size_t inHeight;
    float WHRatio;
    float inScaleFactor;
    float meanVal;
    float minConfidence;
    std::vector<std::string> classNames;
} DnnConfiguration;

#ifdef __cplusplus
extern "C" {
#endif
JNIEXPORT jlong JNICALL
Java_org_opencv_samples_mobilenet_ObjectDetectionHandler_createNativeObject(
        JNIEnv *env, jclass type, jstring filePath_, jstring neuralNet_, jstring deploy_);

JNIEXPORT void JNICALL
Java_org_opencv_samples_mobilenet_ObjectDetectionHandler_nativeStart(JNIEnv *env, jclass type,
                                                                     jlong thiz);
JNIEXPORT void JNICALL
Java_org_opencv_samples_mobilenet_ObjectDetectionHandler_nativeStop(JNIEnv *env, jclass type,
                                                                    jlong thiz);
JNIEXPORT void JNICALL
Java_org_opencv_samples_mobilenet_ObjectDetectionHandler_processFrame(JNIEnv *env, jclass type,
                                                                           jlong thiz,
                                                                           jlong inputFrame,
                                                                           jlong faces);
JNIEXPORT jlong JNICALL
Java_org_opencv_samples_mobilenet_ObjectDetectionHandler_destroyNativeObject(JNIEnv *env,
                                                                             jclass type,
                                                                             jlong thiz);
JNIEXPORT void JNICALL
Java_org_opencv_samples_mobilenet_ObjectDetectionHandler_setConfiguration(JNIEnv *env, jclass type,
                                                                          jint inWidth,
                                                                          jint inHeight,
                                                                          jfloat WHRatio,
                                                                          jfloat inScaleFactor,
                                                                          jfloat meanVal,
                                                                          jfloat minConfidence,
                                                                          jobjectArray dictionary);
///*
// * Class:     org_opencv_samples_fd_DetectionBasedTracker
// * Method:    nativeCreateObject
// * Signature: (Ljava/lang/String;F)J
// */
//JNIEXPORT jlong JNICALL
//Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeCreateObject
//        (JNIEnv *, jclass, jstring, jint);
//
///*
// * Class:     org_opencv_samples_fd_DetectionBasedTracker
// * Method:    nativeDetect
// * Signature: (JJJ)V
// */
//JNIEXPORT void JNICALL
//Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeDetect
//        (JNIEnv *, jclass, jlong, jlong, jlong);


#ifdef __cplusplus
}
#endif
#endif //FACEDETECT_NATIVE_LIB_H

