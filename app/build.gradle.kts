plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.plotpoints"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.plotpoints"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.text.google.fonts)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    //user added
    //Navigation
    implementation(libs.androidx.navigation.compose)

    //Mapbox
    implementation("com.mapbox.maps:android:11.16.4")
    implementation("com.mapbox.extension:maps-compose:11.16.4")

    //Mapbox Searchbar
    implementation("com.mapbox.search:place-autocomplete:2.16.6")
    implementation("com.mapbox.search:discover:2.16.6")
    implementation("com.mapbox.search:autofill:2.16.6")
    implementation("com.mapbox.search:mapbox-search-android-ui:2.16.6")
    implementation("com.mapbox.search:mapbox-search-android:2.16.6")

    //Mapbox Navigation
    implementation("com.mapbox.navigationcore:android:3.17.0")
    implementation("com.mapbox.navigationcore:navigation:3.17.0")
    implementation("com.mapbox.navigationcore:tripdata:3.17.0")
    implementation("com.mapbox.navigationcore:ui-components:3.17.0")
    implementation("com.mapbox.navigationcore:ui-maps:3.17.0")


    //Gif splash
    implementation("androidx.core:core-splashscreen:1.0.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")

    //Room
    val roomVersion = "2.8.4"
    implementation("androidx.room:room-runtime:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:${roomVersion}")
    implementation("androidx.room:room-rxjava2:${roomVersion}")
    implementation("androidx.room:room-rxjava3:${roomVersion}")
    implementation("androidx.room:room-guava:${roomVersion}")
    testImplementation("androidx.room:room-testing:${roomVersion}")
    implementation("androidx.room:room-paging:${roomVersion}")

}