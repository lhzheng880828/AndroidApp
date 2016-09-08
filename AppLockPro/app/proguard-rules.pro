# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/guoguo/workspace/android-sdk-linux/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

############# 微信SDK ###################
-keep class com.tencent.mm.sdk.** {
   *;
}

############# 支付宝SDK ##################
-keep class  com.alipay.share.sdk.** {
   *;
}

############ 百度地图 ##################
-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}
-dontwarn com.baidu.**

-ignorewarnings

############## BMOB #####################
# 这里根据具体的SDK版本修改

-keepattributes Signature
-keep class cn.bmob.v3.** {*;}

# 保证继承自BmobObject、BmobUser类的JavaBean不被混淆
-keep class com.eeontheway.android.applocker.feedback.BmobFeedBackInfo{*;}
-keep class com.eeontheway.android.applocker.login.BmobUserInfo{*;}
-keep class com.eeontheway.android.applocker.updater.BmobUpdateInfo{*;}
-keep class com.eeontheway.android.applocker.updater.BmobUpdateLog{*;}
