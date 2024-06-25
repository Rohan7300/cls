plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.clebs.celerity_admin"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.clebs.celerity_admin"
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
    packaging {
        resources {
            merges += "META-INF/LICENSE*.md"
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

    dependencies {

        implementation("androidx.core:core-ktx:1.13.1")
        implementation("androidx.appcompat:appcompat:1.6.1")
        implementation("com.github.clearquotetech:cq-android-sdk:2.2.0")
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
        androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
        implementation(platform("com.google.firebase:firebase-bom:32.6.0"))
    }
}
dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
}
