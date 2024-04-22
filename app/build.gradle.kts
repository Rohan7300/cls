import org.gradle.internal.impldep.bsh.commands.dir

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.clebs.celerity"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.clebs.celerity"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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

    packaging {
        resources {
            merges += "META-INF/LICENSE*.md"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

//    splits {
//        abi {
//            isEnable = true
//            reset()
//            include("x86", "x86_64", "armeabi", "armeabi-v7a", "mips", "mips64", "arm64-v8a")
//            isUniversalApk = false
//        }
//    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"

        }
    }

}

dependencies {
//    implementation fileTree(dir: 'libs', include: ['*.jar'])
//    implementation(files("libs/android-support-v4.jar"))
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
//    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("id.zelory:compressor:3.0.1")


implementation("com.elconfidencial.bubbleshowcase:bubbleshowcase:1.3.1")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")
    implementation("de.hdodenhof:circleimageview:3.1.0")

    implementation("androidx.multidex:multidex:2.0.1")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.14.2")
    implementation("com.google.android.gms:play-services-vision:20.0.0")
    implementation("com.github.tapadoo:alerter:7.2.4")
    implementation("com.google.firebase:firebase-ml-vision:24.0.3")
//    implementation("com.google.firebase:firebase-core:21.1.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    val camerax_version = "1.0.0-rc01"
    implementation("androidx.camera:camera-camera2:$camerax_version")
    implementation( "androidx.camera:camera-lifecycle:$camerax_version")
    implementation("androidx.camera:camera-view:1.0.0-alpha20")
    testImplementation("junit:junit:4.13.2")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.ncorti:slidetoact:0.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    //Coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
//    implementation("com.github.AnyChart:AnyChart-Android:1.1.5")

    implementation("com.google.code.gson:gson:2.10.1")


    implementation(platform("com.google.firebase:firebase-bom:32.6.0"))

    // CQ SDK
    /*implementation ("com.github.clearquotetech:cq-android-sdk:2.0.9-test")*/
    implementation ("com.github.clearquotetech:cq-android-sdk:2.1.3-test")


    //viewModel
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //implementation("com.github.quickpermissions:quickpermissions-kotlin:0.4.0")
    implementation ("com.github.quickpermissions:quickpermissions-kotlin:0.4.0")
    implementation("ru.superjob:kotlin-permissions:1.0.3")
    implementation("org.jetbrains.anko:anko-commons:0.10.4")

    implementation("com.github.tapadoo:alerter:7.2.4")
    implementation("com.github.oky2abbas:chainChart:v0+")

    val room_version = "2.6.1"
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
//    implementation("org.mozilla.geckoview:geckoview-nightly:100.0.20220308100756")
    implementation ("com.github.clearquotetech:cq-android-sdk:2.0.4-test")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.andkulikov:transitionseverywhere:1.8.1")

    kapt("androidx.room:room-compiler:2.6.1")



}