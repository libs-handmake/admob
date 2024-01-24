import compose_config.composeImplementations
import core_configs.coreAppImplementations
import core_configs.jetpackComponentImplementation
import firebase_configs.firebaseCoreImplementation

plugins {
    id(Plugins.ANDROID_LIBS)
    kotlin("android")
    id("kotlin-kapt")
    id(Plugins.HILT)
    id("maven-publish")
}

android {
    namespace = "common.hoangdz.admob"
    compileSdk = Configs.TARGET_SDK

    defaultConfig {
        minSdk = Configs.MIN_SUPPORTED_SDK
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = Configs.JAVA_TARGET
        targetCompatibility = Configs.JAVA_TARGET
    }
    kotlinOptions {
        jvmTarget = Configs.JVM_TARGET
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = PluginsVer.COMPOSE_COMPILER
    }
}

dependencies {
    coreAppImplementations()
    jetpackComponentImplementation()
    composeImplementations()
    firebaseCoreImplementation()
    implementation(project(":base:android-common"))
    implementation(Deps.ADMOB)
    implementation(Deps.SHIMMER_COMPOSE)
    implementation(AdmobDeps.USER_MESSAGE_PLATFORM)
}

kapt {
    correctErrorTypes = true
}