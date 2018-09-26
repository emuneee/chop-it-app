
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := mediakit
LOCAL_LDLIBS := -llog -ljnigraphics -lz -landroid
LOCAL_CFLAGS := -Wdeprecated-declarations
ANDROID_LIB := -landroid
LOCAL_CFLAGS := -I/Users/evan/Library/Android/sdk/ndk-bundle/sources/ffmpeg
LOCAL_SRC_FILES :=  mediakit.c ffmpeg.c ffmpeg_filter.c ffmpeg_opt.c cmdutils.c
LOCAL_SHARED_LIBRARIES := libavformat libavcodec libswscale libavutil libswresample libavfilter libavdevice

include $(BUILD_SHARED_LIBRARY)
$(call import-add-path,lib/)
$(call import-module,$(TARGET_ARCH_ABI))
