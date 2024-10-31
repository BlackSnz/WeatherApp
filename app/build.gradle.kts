plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.weatherapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.weatherapp"
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

    buildFeatures {
        viewBinding = true
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // For viewmodel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.activity.ktx)

    // Retrofit
    implementation(libs.retrofit)

    // Retrofit with Scalar Converter
    implementation(libs.converter.scalars)

    // Retrofit with GSON converter
    implementation(libs.converter.gson)

    // Glide libraries
    implementation(libs.github.glide)

    // Location form Google Play Services
    implementation(libs.google.play.services.location)

    // For preferences
    implementation(libs.androidx.preference)
    implementation(libs.androidx.preference.ktx)

    // Navigation for Views/Fragments Integration
    implementation(libs.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    // Feature module support for Fragments
    implementation(libs.androidx.navigation.dynamic.features.fragment)

    // Card view
    implementation(libs.androidx.cardview)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Coroutines work with Task
    implementation(libs.kotlinx.coroutines.play.services)

    // Lottie animations
    implementation("com.airbnb.android:lottie:3.5.0")

    // Room
    val room_version = "2.6.1"

    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")

    // To use Kotlin annotation processing tool (kapt)
    kapt("androidx.room:room-compiler:$room_version")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")

    testImplementation(libs.junit)
    testImplementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.test2)
    testImplementation(libs.org.mockito.mockito.core2)
    testImplementation(libs.androidx.core.testing)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.core.testing)
    annotationProcessor(libs.glide.compiler)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.test2)
    androidTestImplementation(libs.org.mockito.mockito.core2)
    androidTestImplementation(libs.androidx.core.testing)

    // Testing Navigation
    androidTestImplementation(libs.androidx.navigation.testing)


}

kapt {
    correctErrorTypes = true
}

