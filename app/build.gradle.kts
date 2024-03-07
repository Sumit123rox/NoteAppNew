plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.navigation.safeargs)
    alias(libs.plugins.hilt.androidplugin)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.sumit.noteappnew"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sumit.noteappnew"
        minSdk = 24
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
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Navigation Components
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    //Architecture
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.runtime.ktx)

    //Room
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.room.runtime)

    //Dagger - Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit.converter.scalars)
    implementation(libs.logging.interceptor)
    implementation(libs.okhttp.urlconnection)

    //Ktor Client
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.logging)
//    implementation(libs.ktor.client.serialization)
//    implementation(libs.logback.classic)
//    implementation(libs.kotlinx.serialization.json)
    implementation(libs.content.nagotiable)
    implementation(libs.ktor.serialization.kotlinx.json)

    //Paging
    implementation(libs.paging.runtime.ktx)

    //DataStore
    implementation(libs.datastore.preferences)

    //SwipeLayout
    implementation(libs.swiperefreshlayout)

    //Timber
    implementation(libs.timber)

}