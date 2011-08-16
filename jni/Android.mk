LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

OPENCV_CAMERA_MODULES:=off
# include ../includeOpenCV.mk
include jni/includeOpenCV.mk
ifeq ("$(wildcard $(OPENCV_MK_PATH))","")
	#try to load OpenCV.mk from default install location
	include $(TOOLCHAIN_PREBUILT_ROOT)/user/share/OpenCV/OpenCV.mk
else
	include $(OPENCV_MK_PATH)
endif

# LOCAL_MODULE    := mixed_sample
LOCAL_MODULE    := beethelionmatching
# LOCAL_SRC_FILES := jni_part.cpp
LOCAL_SRC_FILES := matchingDesc.cpp
LOCAL_LDLIBS +=  -llog -ldl

include $(BUILD_SHARED_LIBRARY)
