LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
OPENCV_SDK_JNI = C:\\OpenCV-2.4.11-android-sdk\\OpenCV-android-sdk\\sdk\\native\\jni
include $(OPENCV_SDK_JNI)/OpenCV.mk

LOCAL_MODULE    := SimpleCvCam
LOCAL_SRC_FILES := SimpleCvCam.cpp

LOCAL_C_INCLUDES += $(OPENCV_SDK_JNI)/include

include $(BUILD_SHARED_LIBRARY)
