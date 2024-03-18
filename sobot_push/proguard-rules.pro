# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#小米推送
-keep class org.android.agoo.xiaomi.MiPushBroadcastReceiver {*;}
-dontwarn com.xiaomi.push.**
#华为推送
-ignorewarnings
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keep class com.huawei.hianalytics.**{*;}
-keep class com.huawei.updatesdk.**{*;}
-keep class com.huawei.hms.**{*;}
#魅族
-keep class com.meizu.cloud.** {*;}
-dontwarn com.meizu.cloud.**
#oppo
-keep public class * extends android.app.Service
#vivo
-dontwarn com.vivo.push.**
-keep class com.vivo.push.** {*;}
-keep class com.vivo.vms.** {*;}
#荣耀
-keep class com.hihonor.android.push.** {*;}
-keep class com.hihonor.push.** {*;}
-keep class org.android.agoo.honor.* {*;}
#FCM通道 Firebase
-keep class org.android.agoo.fcm.* {*;}
