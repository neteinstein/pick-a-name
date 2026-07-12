# Add project specific ProGuard rules here.
# Release builds currently ship with minification disabled (see app/build.gradle.kts),
# these rules are kept as a starting point for when it is enabled.

# Room
-keep class org.neteinstein.pickaname.data.local.database.** { *; }

# PDFBox-Android
-dontwarn org.bouncycastle.**
-dontwarn javax.imageio.**
-dontwarn org.osgi.**
