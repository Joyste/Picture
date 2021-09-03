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


#PictureSelector 2.0
#图库选择混淆
-keep class com.luck.picture.lib.** { *; }

#Ucrop
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }

#Okio
-dontwarn org.codehaus.mojo.animal_sniffer.*


-keep class com.huantansheng.easyphotos.models.** { *; }

#指定代码的压缩级别
-optimizationpasses 5
 #预校验
-dontpreverify
# 指定混淆时采用的算法，后面的参数是一个过滤器
-optimizations !code/simplification/cast,!field/*,!class/merging/*
#保护注解
-keepattributes *Annotation*,InnerClasses
-keep class * extends java.lang.annotation.Annotation { *; }
#避免混淆泛型
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable

 #混淆时是否记录日志
-verbose
#包明不混合大小写
-dontusemixedcaseclassnames
#优化  不优化输入的类文件
-dontoptimize
#---------------------------------默认保留区---------------------------------
#继承activity,application,service,broadcastReceiver,contentprovider....不进行混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View

-keep class android.support.** {*;}

-keep public class * extends android.view.View{
*** get*();
void set*(***);
public <init>(android.content.Context);
public <init>(android.content.Context, android.util.AttributeSet);
public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
public <init>(android.content.Context, android.util.AttributeSet);
public <init>(android.content.Context, android.util.AttributeSet, int);
}
#所有要用容器显示的VIEW初始保留构造函数，以备混淆后找不到
-keepclassmembers class * {
public <init>(lifeexperience.tool.beautycamera.view.activity.BaseActivity);
}
#这个主要是在layout 中写的onclick方法android:onclick="onClick"，不进行混淆
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

#-----------实体类[Bean类]---------
#修改成你对应的包名
-keep public class lifeexperience.tool.beautycamera.model.AppInfo.**{*;}
-keep public class com.xinlan.imageeditlibrary.editimage.model.DrawPath.**{*;}
-keep public class com.xinlan.imageeditlibrary.editimage.model.appInfo.**{*;}
-keep public class com.xinlan.imageeditlibrary.editimage.model.RatioItem.**{*;}
-keep public class com.xinlan.imageeditlibrary.editimage.model.StickerBean.**{*;}

#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable
#保持 Serializable 不被混淆并且enum 类也不被混淆
-keepclassmembers class * implements java.io.Serializable {
static final long serialVersionUID;
private static final java.io.ObjectStreamField[] serialPersistentFields;
!static !transient <fields>;
!private <fields>;
!private <methods>;
private void writeObject(java.io.ObjectOutputStream);
private void readObject(java.io.ObjectInputStream);
java.lang.Object writeReplace();
java.lang.Object readResolve();
}

#保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
#Parcelable实现类除了不能混淆本身之外，为了确保类成员也能够被访问，类成员也不能被混淆
-keepclassmembers class * implements android.os.Parcelable {
 public <fields>;
 private <fields>;
}
#不混淆资源类
-keep class **.R$* {
 *;
}
#保持枚举 enum 类不被混淆
-keepclassmembers enum * {
public static **[] values();
public static ** valueOf(java.lang.String);
}
-keepclassmembers class * {
public void *ButtonClicked(android.view.View);
}
-keepclassmembers class * {
void *(*Event);
}
#// natvie 方法不混淆
-keepclasseswithmembernames class * {
native <methods>;
}
# support-v4
#https://stackoverflow.com/questions/18978706/obfuscate-android-support-v7-widget-gridlayout-issue
-dontwarn android.support.v4.**
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v4.** { *; }

-dontwarn android.support.v7.**
-keep class android.support.v7.app.** { *; }
-keep interface android.support.v7.app.** { *; }
-keep class android.support.v7.** { *; }

-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**

#不去忽略非公共的库类
-dontskipnonpubliclibraryclassmembers
-dontskipnonpubliclibraryclasses

-dontwarn  com.alipay.**
-dontwarn com.alibaba.**
-dontwarn  com.ut.**
-dontwarn  com.ta.**
-dontwarn  com.amap.**
-dontwarn com.netease.**
-dontwarn com.google.**
-keep class com.netease.** {*;}
-keep interface com.netease.** {*;}
-keep class com.amap..**{*;}
-keep interface com.amap.** {*;}
-keep class com.alipay.**{*;}
-keep interface com.alipay.** {*;}
-keep class com.alibaba.**{*;}
-keep interface com.alibaba.** {*;}
-keep class com.ut.**{*;}
-keep interface com.ut.** {*;}
-keep class com.ta.**{*;}
-keep interface com.ta.** {*;}
-keep class org.json.alipay.**{*;}
-keep class com.unionpay.**{*;}
-keep interface com.unionpay.** {*;}
-keep class com.tencent.**{*;}
-keep interface com.tencent.** {*;}
-keep class com.google.**{*;}
-keep interface com.google.** {*;}

-keep class com.ayetstudios.**{*;}
-keep interface com.ayetstudios.** {*;}
-keep class com.facebook.**{*;}
-keep interface com.facebook.** {*;}

-keep class com.qihoo.**{*;}
-keep interface com.qihoo.** {*;}
-dontwarn  u.aly.**
-dontwarn com.taobao.**
-dontwarn anet.channel.**
-dontwarn anetwork.channel.**
-dontwarn org.android.**
-keep class u.aly.** {*;}
-keep interface u.aly.** {*;}
-dontwarn org.apache.**
-keep class org.apache.** {*;}
-keep class com.firebase.** { *; }
-keep class com.ayetstudios.publishersdk.messages.** {*;}
-keep public class com.ayetstudios.publishersdk.AyetSdk
-keepclassmembers class com.ayetstudios.publishersdk.AyetSdk {
   public *;
}
-keep public interface com.ayetstudios.publishersdk.interfaces.UserBalanceCallback { *; }
-keep public interface com.ayetstudios.publishersdk.interfaces.DeductUserBalanceCallback { *; }

-keep class com.ayetstudios.publishersdk.models.VastTagReqData { *; }
# For communication with AdColony's WebView
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.mintegral.** {*; }
-keep interface com.mintegral.** {*; }
-keep interface androidx.** { *; }
-keep class androidx.** { *; }
-keep public class * extends androidx.** { *; }
-dontwarn com.mintegral.**
-keep class **.R$* { public static final int mintegral*; }
-keep class com.alphab.** {*; }
-keep interface com.alphab.** {*; }

-keep class com.tapjoy.** { *; }
-keep class com.moat.** { *; }
-keepattributes JavascriptInterface
-keepattributes *Annotation*
-keep class * extends java.util.ListResourceBundle {
protected Object[][] getContents();
}
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
public static final *** NULL;
}
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
@com.google.android.gms.common.annotation.KeepName *;
}
-keepnames class * implements android.os.Parcelable {
public static final ** CREATOR;
}
-keep class com.google.android.gms.ads.identifier.** { *; }
-dontwarn com.tapjoy.**

# BRVAH
-keep class com.chad.library.adapter.** { *; }
-keep public class * extends com.chad.library.adapter.base.BaseViewHolder
-keepclassmembers public class * extends com.chad.library.adapter.base.BaseViewHolder {
    <init>(android.view.View);
}

-dontwarn com.bumptech.glide.**
-keep class com.bumptech.glide.**{*;}
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep public class * extends com.bumptech.glide.request.RequestOptions
-keep public class * extends com.bumptech.glide.RequestBuilder
-keep public class * extends com.bumptech.glide.RequestManager

-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.** { *; }

-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes Signature


-dontwarn com.google.firebase.messaging.RemoteMessage
-keep class com.google.firebase.messaging.RemoteMessage
-dontwarn android.app.NotificationChannel
-dontwarn android.app.NotificationManager
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class uk.co.chrisjenx.calligraphy.** { *; }
-dontwarn uk.co.chrisjenx.calligraphy.**
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }
-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}
-dontwarn com.squareup.okhttp.**
-keepnames class com.squareup.picasso.Picasso
-keepclassmembers class com.squareup.picasso.Picasso {
    public com.squareup.picasso.Picasso with(android.content.Context);
}
