import org.gradle.internal.impldep.bsh.commands.dir

plugins {
    id ("kotlin-android")
    id ("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.clebs.celerity_admin"
    compileSdk = 34

    defaultConfig {
//        applicationId = "com.clebs.celerity_admin"
        minSdk = 26
        targetSdk = 34

//        versionCode = 1
//        versionName = "0.1.0"

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
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
        kotlinOptions {
            jvmTarget = "1.8"
        }
        buildFeatures {
            dataBinding = true
            viewBinding = true
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    dependencies {
        implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
        implementation("androidx.core:core-ktx:1.13.1")
        implementation("androidx.appcompat:appcompat:1.6.1")
        implementation("com.github.clearquotetech:cq-android-sdk:2.2.2")
        implementation("com.google.android.material:material:1.6.1")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.0")
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.0")
        implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
        implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
        testImplementation("junit:junit:4.13.2")
        implementation("de.hdodenhof:circleimageview:3.1.0")
        implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
        implementation("com.squareup.retrofit2:retrofit:2.9.0")
        implementation("com.squareup.okhttp3:okhttp:4.10.0")
        implementation("com.github.AppIntro:AppIntro:6.3.1")
        implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
        implementation("com.squareup.retrofit2:converter-gson:2.9.0")
        androidTestImplementation("androidx.test.ext:junit:1.1.5")
        implementation("androidx.room:room-ktx:2.6.1")
        kapt("androidx.room:room-compiler:2.6.1")
        implementation ("com.airbnb.android:lottie:3.4.0")

        androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
        implementation(platform("com.google.firebase:firebase-bom:32.6.0"))
    }
    kapt {
        correctErrorTypes = true
    }
}
dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
    implementation("androidx.activity:activity-compose:1.9.1")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

