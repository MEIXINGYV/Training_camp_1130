plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler) // 启用 Compose 编译器（自动匹配 Kotlin 2.0.21）
}

android {
    namespace = "com.example.douyin1120"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.duoyin1120"
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

    // ⚠️ 不要写 composeOptions！Compose 编译器由 Kotlin 插件自动管理
}

dependencies {
    // 基础库
    implementation(libs.androidx.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.foundation)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.coil.compose)

    debugImplementation(libs.compose.ui.tooling)

    // Material & AppCompat
    implementation(libs.androidx.appcompat)
    implementation(libs.material)


    // Accompanist SwipeRefresh（稳定版）
    implementation("com.google.accompanist:accompanist-swiperefresh:0.30.1")

    // Coroutines（支持 snapshotFlow）
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.34.0")

}
