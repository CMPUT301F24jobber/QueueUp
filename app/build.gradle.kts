plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.queueup"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.queueup"
        minSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    testOptions {
    }
}

dependencies {
    implementation("com.google.guava:guava:32.1.3-android")
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.android.gms:play-services-base:18.3.0")
    implementation(libs.play.services.vision)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.messaging)
    implementation(libs.places)
    implementation(libs.firebase.storage)
    implementation(libs.navigation.fragment)
    implementation(libs.camera.mlkit.vision)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    androidTestImplementation("androidx.test:core:1.6.1")
    implementation("com.google.firebase:firebase-firestore:25.1.0")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.android.material:material:1.9.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    implementation("com.google.zxing:core:3.5.1")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("androidx.fragment:fragment:1.8.5")
    implementation("com.google.firebase:firebase-messaging:23.1.2")
    implementation("com.google.mlkit:barcode-scanning:17.3.0")
    implementation("com.google.android.gms:play-services-mlkit-barcode-scanning:18.3.1 ")
    implementation("androidx.concurrent:concurrent-futures:1.2.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("org.osmdroid:osmdroid-android:6.1.10")
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.5")
    implementation(libs.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.video)
    implementation(libs.camera.view)
    implementation(libs.camera.extensions)

}


