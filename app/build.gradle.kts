plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.discord.settings"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.discord.settings"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "3.2.6"
        resConfigs("en")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources=true
            isDebuggable=false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation ("com.squareup.okhttp3:okhttp:4.10.0"){
        exclude (module = "org.bouncycastle")
        exclude (module= "org.conscrypt")
        exclude (module= "org.openjsse")
        exclude(module = "okhttp-brotli")
        exclude(module = "okhttp-dnsoverhttps")
    }
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)}