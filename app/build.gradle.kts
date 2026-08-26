plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }
android { namespace="com.dangi.app"; compileSdk=35
 defaultConfig { applicationId="com.dangi.app"; minSdk=23; targetSdk=35; versionCode=4; versionName="4.0" }
}
dependencies { implementation("androidx.appcompat:appcompat:1.7.0"); implementation("androidx.biometric:biometric:1.1.0"); implementation("androidx.core:core-ktx:1.15.0") }
