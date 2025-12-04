# 保留kotlinx序列化与Hilt生成的类
-keep class kotlinx.** { *; }
-keep class com.google.dagger.hilt.** { *; }
-dontwarn kotlinx.serialization.**
-dontwarn dagger.hilt.internal.**
