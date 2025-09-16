plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "yuu.deeplink"

    signingConfigs {
        create("testRelease") {
            val keystorePropertiesFile = rootProject.file("app/deeplink.jks")
            storeFile = keystorePropertiesFile
            storePassword = "deeplink"
            keyAlias = "2333"
            keyPassword = "deeplink"
        }
        create("testDebug") {
            storeFile = file("app/deeplink.jks")
            storePassword = "deeplink"
            keyAlias = "2333"
            keyPassword = "deeplink"
        }
    }


    compileSdk = 34

    defaultConfig {
        applicationId = "yuu.deeplink"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}