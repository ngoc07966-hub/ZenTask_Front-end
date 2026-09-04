plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.dh24tin04.zentask"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.dh24tin04.zentask"
        minSdk = 28
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

    // ĐOẠN MÃ MỚI ĐƯỢC THÊM VÀO Ở ĐÂY 👇
    sourceSets {
        getByName("main") {
            res.srcDirs(
                "src/main/res",
                "src/main/res-layouts/auth",
                "src/main/res-layouts/dany",
                "src/main/res-layouts/home",
                "src/main/res-layouts/hoso",
                "src/main/res-layouts/lichhoc",
                "src/main/res-layouts/napdulieuai",
                "src/main/res-layouts/welcome"
            )
        }
    }

}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}