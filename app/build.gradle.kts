plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.gninecs460"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.gninecs460"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
    buildFeatures{
        viewBinding=true
    }
}

dependencies {
    implementation(libs.imagepicker)
    implementation(libs.firebase.firestore)
    implementation(libs.okhttp)
    implementation(libs.glide)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.ui.firestore)
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation(libs.firebase.auth)
    implementation(libs.ccp)
    implementation(libs.firebase.messaging.v2403)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.messaging)
    implementation(libs.play.services.cast.framework)
    implementation(libs.media3.common)
    implementation(libs.firebase.appcheck.playintegrity)
    implementation(libs.firebase.appcheck.debug)
    implementation(libs.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}