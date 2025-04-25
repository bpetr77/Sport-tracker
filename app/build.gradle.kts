import java.util.Properties


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("org.jetbrains.kotlin.kapt")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}
////////////
val properties = Properties()
val secretsFile = rootProject.file("secrets.properties")
if (secretsFile.exists()) {
    properties.load(secretsFile.inputStream())
}
////////////
android {
    namespace = "hu.bme.aut.android.sporttracker"
    compileSdk = 35 //34

    defaultConfig {
        applicationId = "hu.bme.aut.android.sporttracker"
        minSdk = 26 //24
        targetSdk = 35   //34
        versionCode = 1
        versionName = "1.0"

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
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    secrets {
        // To add your Maps API key to this project:
        // 1. If the secrets.properties file does not exist, create it in the same folder as the local.properties file.
        // 2. Add this line, where YOUR_API_KEY is your API key:
        //        MAPS_API_KEY=YOUR_API_KEY
        propertiesFileName = "secrets.properties"

        // A properties file containing default secret values. This file can be
        // checked in version control.
        defaultPropertiesFileName = "local.defaults.properties"
    }
    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField(
            "String",
            "MAPS_API_KEY",
            properties["MAPS_API_KEY"]?.toString()?.let { "\"$it\"" } ?: "\"\""
        )
    }
}

dependencies {
    val roomVersion = "2.7.0"
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    implementation("com.google.android.gms:play-services-maps:18.2.0")
    //implementation("com.google.maps.android:maps-compose:2.5.3")
    implementation(libs.play.services.location)
    implementation(libs.support.annotations)
    //implementation("com.google.accompanist:accompanist-permissions:0.32.0") // Engedélykéréshez
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    implementation(libs.androidx.navigation.runtime.android)
    implementation ("co.yml:ycharts:2.1.0")
    implementation(libs.androidx.navigation.compose)
    implementation ("io.coil-kt:coil-compose:2.4.0")
    implementation(libs.androidx.room.common.jvm)
    implementation(libs.firebase.crashlytics.buildtools)
    //implementation(libs.androidx.room.runtime.android)
    implementation(libs.androidx.media3.database)
    implementation("com.google.code.gson:gson:2.10.1") // Add Gson library
    implementation("com.google.guava:guava:32.1.2-jre")
    implementation("androidx.room:room-runtime:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation ("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.9.0")
    implementation ("androidx.room:room-paging:$roomVersion")

    implementation ("com.google.maps.android:maps-compose:6.6.0")

    // Optionally, you can include the Compose utils library for Clustering,
    // Street View metadata checks, etc.
    implementation ("com.google.maps.android:maps-compose-utils:6.6.0")

    // Optionally, you can include the widgets library for ScaleBar, etc.
    implementation ("com.google.maps.android:maps-compose-widgets:6.6.0")


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}