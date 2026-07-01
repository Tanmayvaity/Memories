@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.devtools.ksp")
    alias(libs.plugins.dagger.hilt.android)

}

android {
    namespace = "com.example.memories"
    compileSdk = 36

    val file = rootProject.file("local.properties")
    val properties = Properties()
    properties.load(FileInputStream(file))

    defaultConfig {
        applicationId = "com.example.memories"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String","apiKey",properties.getProperty("API_KEY"))

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    testOptions{
        unitTests{
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }

    packaging {
        resources {
            // Exclude duplicate license files from the final build packaging
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/LICENSE-notice.md"

            // Alternatively, if you hit more JUnit metadata conflicts:
            // pickFirst 'META-INF/LICENSE.md'
        }
    }

}
androidComponents{
    ksp{
        arg("room.schemaLocation", "$projectDir/schemas")
    }
    kotlin{
        target {
            compilerOptions{
                jvmTarget.set(JvmTarget.JVM_17)
            }
        }
        compilerOptions{
            freeCompilerArgs.add("-Xexplicit-backing-fields")
            freeCompilerArgs.add("-XXLanguage:+ExplicitBackingFields")
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material3.expressvie)
    implementation(libs.androidx.material3.icons)
    implementation(libs.core.ktx)
    testImplementation(libs.junit)
//    testImplementation(libs.junit.jupiter)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Serialization
    implementation(libs.kotlinx.serialization.json)
    // Compose navigation
    implementation(libs.androidx.navigation)

    // camerax
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.compose)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.extensions)
    implementation(libs.androidx.camera.video)

    // coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network)
    implementation(libs.coil.video)

    // hilt
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.android)
    // hilt-navigation compose
    implementation(libs.hilt.navigation.compose)

    // exoplayer
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.exoplayer.dash)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.compose)

    // paging 3
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

    // datastore
    implementation(libs.androidx.datastore.preferences)

    // room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)

    implementation(libs.dotlottie.android)


    // work manager

    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.hilt.work)

    ksp(libs.androidx.hilt.work.compiler)


    // retrofit
    implementation(libs.squareup.retrofit)
    implementation(libs.retrofit.gson.convertor)

    // biometric
    implementation(libs.androidx.biometric)

    // adaptive
    implementation(libs.androidx.material3.adaptive)
    implementation(libs.androidx.material3.adaptive.layout)
    implementation(libs.androidx.material3.adaptive.navigation)
    implementation(libs.androidx.window)


    // mlkit
    implementation(libs.mlkit.image.labeling)


    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.io.mockk)
    testImplementation(libs.androidx.paging.testing)
    testImplementation(libs.squareup.mockwebserver)


    testImplementation("org.robolectric:robolectric:4.12")

//    testImplementation(platform(libs.junit.bom))
//    testRuntimeOnly(libs.junit.platform.launcher)

    androidTestImplementation("io.mockk:mockk-android:1.14.11")
}
