# use this to select gcc instead of clang
APP_ABI := armeabi-v7a
NDK_TOOLCHAIN_VERSION := 4.9
APP_CPPFLAGS += -std=c++11
APP_STL := gnustl_shared
LOCAL_C_INCLUDES += ${ANDROID_NDK}/sources/cxx-stl/gnu-libstdc++/4.9/include
