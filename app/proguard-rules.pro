# Keep OpenCV public API and avoid warnings
-keep class org.opencv.** { *; }
-dontwarn org.opencv.**

# ML Kit keep rules
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.odml.** { *; }
-keep class com.google.android.gms.internal.mlkit_vision_** { *; }
-dontwarn com.google.mlkit.**
-dontwarn com.google.android.odml.**
-dontwarn com.google.android.gms.internal.mlkit_vision_**

# Keep CanHub Android Image Cropper (only if you turn on minify later)
-keep class com.canhub.cropper.** { *; }