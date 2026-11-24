plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }
android {
    namespace = "com.jarvis.p30"
    compileSdk = 35
    defaultConfig { applicationId = "com.jarvis.p30"; minSdk = 26; targetSdk = 34; versionCode = 2; versionName = "2.0" }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    kotlinOptions { jvmTarget = "17" }
}

dependencies { implementation("com.alphacephei:vosk-android:0.3.47@aar"); implementation("net.java.dev.jna:jna:5.13.0@aar") }
