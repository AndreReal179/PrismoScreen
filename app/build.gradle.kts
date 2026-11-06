plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "br.andre.espelhocarro"
    compileSdk = 35

    defaultConfig {
        applicationId = "br.andre.espelhocarro"
        minSdk = 26
        targetSdk = 35
        versionCode = 4
        versionName = "0.3-sem-toque"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.media:media:1.7.0")
    implementation("androidx.car.app:app:1.4.0")
    implementation("androidx.lifecycle:lifecycle-common:2.8.7")
}
