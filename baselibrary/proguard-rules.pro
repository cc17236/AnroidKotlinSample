# 代码混淆压缩比，在0~7之间，默认为5,一般不下需要修改
-optimizationpasses 5

# 混淆时不使用大小写混合，混淆后的类名为小写
# windows下的同学还是加入这个选项吧(windows大小写不敏感)
-dontusemixedcaseclassnames

# 指定不去忽略非公共的库的类
# 默认跳过，有些情况下编写的代码与类库中的类在同一个包下，并且持有包中内容的引用，此时就需要加入此条声明
-dontskipnonpubliclibraryclasses

# 指定不去忽略非公共的库的类的成员
-dontskipnonpubliclibraryclassmembers

# 不做预检验，preverify是proguard的四个步骤之一
# Android不需要preverify，去掉这一步可以加快混淆速度
-dontpreverify

# 有了verbose这句话，混淆后就会生成映射文件
# 包含有类名->混淆后类名的映射关系
# 然后使用printmapping指定映射文件的名称
-verbose
-printmapping proguardMapping.txt
# 指定混淆时采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不改变
-optimizations !code/simplification/artithmetic,!field/*,!class/merging/*

#泛型，解决出现类型转换错误的问题
-keepattributes Signature
#注解
-keepattributes *Annotation*
# 抛出异常时保留代码行号
# ------------------ Keep LineNumbers and properties ---------------- #
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
# --------------------------------------------------------------------------

#-keepattributes SourceFile,LineNumberTable

##下面是自定义的
-keepclassmembers class okhttp3.OkHttpClient {
    final int connectTimeout;
}
-keep class com.example.applicationkotlinsample.communication.HttpService$Companion{
 *;
}

#-keepclassmembers class com.example.applicationkotlinsample.base.entity.MultipartBean**
-keepclassmembers class com.example.applicationkotlinsample.base.entity.MultipartBean** {
    *;
 }
#-keepclassmembers class com.example.applicationkotlinsample.base.entity.BaseBody**
-keepclassmembers class com.example.applicationkotlinsample.base.entity.BaseBody** {
    *;
 }
#-keepclassmembers class com.example.applicationkotlinsample.base.entity.BaseStruct**
-keepclassmembers class com.example.applicationkotlinsample.base.entity.BaseStruct** {
   *;
 }


-keepclassmembers class  com.example.applicationkotlinsample.communication.request.**{
          *;
 }
-keepclassmembers class  com.example.applicationkotlinsample.communication.response.**{
          *;
}




-keep class com.prolificinteractive.materialcalendarview.** {*;}
#-keep class com.example.applicationkotlinsample.communication.** {*;}
#-keep class android.webkit.WebResourceResponse.** {*;}
#-keepclassmembernames class  * extends com.example.applicationkotlinsample.base.entity.MultipartBean{*;}
#-keepclassmembernames class  * extends com.huawen.baselibrary.adapter.entity.MultiItemEntity{*;}
##下面中括号的地方需要要填你的包名
#-keep public class com.example.applicationkotlinsample.R$*{
#    public static final int *;
#}


## 友盟自动更新 2.6.0.1
-keepclassmembers class * { public <init>(org.json.JSONObject); }
-keep public class cn.irains.parking.cloud.pub.R$*{ public static final int *; }
-keep public class * extends com.umeng.**
-keep class com.umeng.** { *; }

##友盟统计分析 5.5.3
-dontwarn u.aly.**
-keepclassmembers class * { public <init>(org.json.JSONObject); }
-keepclassmembers enum com.umeng.analytics.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}


#百家云混淆规则
#-keepclassmembernames class class com.baijiahulian$** { *; }
#-keepclassmembernames class class com.bjhl.**.$** { *; }
#-keepclassmembernames class class com.baijiahulian$** { *; }
#-keepclassmembernames class class com.baijiahulian$** { *; }
-dontwarn com.baijiahulian.**
-dontwarn com.bjhl.**
-keep  class com.baijiahulian.**{*;}
-keep  class com.bjhl.**{*;}
-keep  class com.baijia.**{*;}
-keep  class com.baijiayun.**{*;}
#点播SDK
-keep class tv.danmaku.ijk.**{*;}

#ReactiveNetwork
-keep class com.github.pwittchen.reactivenetwork.**{*;}
-dontwarn com.github.pwittchen.reactivenetwork.library.ReactiveNetwork
-dontwarn io.reactivex.functions.Function

#RxJava混淆规则
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}



#PictureSelector 2.0
-keep class com.luck.picture.lib.** { *; }

-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }

 #rxjava
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
 long producerIndex;
 long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

#rxandroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
##Glide
-dontwarn com.bumptech.glide.**
-keep class com.bumptech.glide.**{*;}
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}


# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
-dontpreverify
# Note that if you want to enable optimization, you cannot just
# include optimization flags in your own project configuration file;
# instead you will need to point to the
# "proguard-android-optimize.txt" file instead of this one from your
# project.properties file.

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
-keep class com.huawen.baselibrary.jni.AppVerify {*;}


# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**

-overloadaggressively #混淆时应用侵入式重载
-obfuscationdictionary ../obfs_dic.txt
-classobfuscationdictionary ../obfs_dic.txt
-packageobfuscationdictionary ../obfs_dic.txt




#Serializable序列化
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}




-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

-keep class retrofit2.**{*;}
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }

-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.** {*;}
-dontwarn com.tencent.smtt.**
-keep class com.tencent.tbs.** {*;}
-keep class com.tencent.smtt.** {*;}
-keep class com.tencent.mtt.** {*;}

-keep class MTT.ThirdAppInfoNew {
	*;
}

