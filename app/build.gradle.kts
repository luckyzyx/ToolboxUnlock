import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android") version "1.9.0"
    id("com.google.devtools.ksp") version "1.9.0-1.0.11"
}

android {
    namespace = "com.luckyzyx.toolboxunlock"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.luckyzyx.toolboxunlock"
        minSdk = 21
        //noinspection OldTargetApi
        targetSdk = 33
        versionCode = getVersionCode()
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    applicationVariants.all {
        val buildType = buildType.name
        println("buildType -> $buildType")
        outputs.all {
            @Suppress("DEPRECATION")
            if (this is com.android.build.gradle.api.ApkVariantOutput) {
                if (buildType == "release") outputFileName =
                    "ToolboxUnlock_v${versionName}(${versionCode}).apk"
                if (buildType == "debug") outputFileName =
                    "ToolboxUnlock_v${versionName}(${versionCode})_debug.apk"
                println("outputFileName -> $outputFileName")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    compileOnly("de.robv.android.xposed:api:82")
    //YukiHookAPI ksp
    implementation("com.highcapable.yukihookapi:api:1.1.11")
    ksp("com.highcapable.yukihookapi:ksp-xposed:1.1.11")

//    implementation("androidx.core:core-ktx:1.10.1")
//    implementation("androidx.appcompat:appcompat:1.6.1")
//    implementation("com.google.android.material:material:1.9.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

fun getVersionCode(): Int {
    val propsFile = file("version.properties")
    val properties = Properties()
    properties.load(FileInputStream(propsFile))
    var vCode = properties["versionCode"].toString().toInt()
    properties["versionCode"] = (++vCode).toString()
    properties.store(propsFile.writer(), null)
    return vCode
}