LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional
LOCAL_CERTIFICATE := platform


LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
jacksonall:lib/jackson-all-1.9.11.jar \



include $(BUILD_MULTI_PREBUILT)
include $(CLEAR_VARS)
LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_MODULE_TAGS := optional
LOCAL_PROGUARD_ENABLED := disabled
LOCAL_CERTIFICATE := platform
LOCAL_PACKAGE_NAME := Market

LOCAL_STATIC_JAVA_LIBRARIES :=  android-support-v13 \
jacksonall \


include $(BUILD_PACKAGE)
include $(call all-makefiles-under,$(LOCAL_PATH))
