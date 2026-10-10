import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // kapt and parcelize are sub-plugins bundled in kotlin-gradle-plugin (no standalone
    // plugin markers in repositories). They resolve from the classpath once
    // org.jetbrains.kotlin.android is applied — applying by id without version.
    id("org.jetbrains.kotlin.kapt")
    id("org.jetbrains.kotlin.plugin.parcelize")
}

val paperPropertiesFile = rootProject.file("../keys/paper.properties")
val properties = Properties().apply {
    if (paperPropertiesFile.exists()) {
        load(paperPropertiesFile.inputStream())
    }
}

android {
    compileSdk = 34
    buildToolsVersion = "34.0.0"

    defaultConfig {
        applicationId = "io.ak1.paper"
        minSdk = 21
        targetSdk = 34
        versionCode = 3
        versionName = "1.0.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
            }
        }
    }

    signingConfigs {
        // Configured only when ../keys/paper.properties exists — debug builds don't need a keystore
        if (paperPropertiesFile.exists()) {
            create("release") {
                storeFile = file(properties.getProperty("STORE_PATH"))
                storePassword = properties.getProperty("STORE_PASSWORD")
                keyAlias = properties.getProperty("KEY_ALIAS")
                keyPassword = properties.getProperty("KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            resValue("string", "app_name", "Paper")
            resValue("string", "paper_file_provider", "io.ak1.paper.file_provider")
            isMinifyEnabled = true
            isShrinkResources = true
            if (paperPropertiesFile.exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            resValue("string", "app_name", "Paper Debug")
            resValue("string", "paper_file_provider", "io.ak1.paper.debug.file_provider")
            applicationIdSuffix = ".debug"
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
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    sourceSets {
        getByName("androidTest") {
            java.srcDirs("src/test-common/java")
        }
        getByName("test") {
            java.srcDirs("src/test-common/java")
        }
    }
    namespace = "io.ak1.paper"
}

dependencies {
    //Core
    implementation(libs.androidx.core.ktx)

    //Design
    implementation(libs.compose.material3)

    //Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)

    // Multidex
    implementation(libs.multidex)

    // Image Size Compressor
    implementation(libs.compressor)

    // Room
    implementation(libs.room.runtime)
    kapt(libs.room.compiler)
    implementation(libs.room.ktx)

    //Koin
    implementation(libs.koin.androidx.compose)

    // LifeCycle
    implementation(libs.lifecycle.viewmodel.ktx)

    // LiveData
    implementation(libs.compose.runtime.livedata)
    implementation(libs.lifecycle.livedata.ktx)

    // Coroutines
    implementation(libs.coroutines.android)

    //Datastore Preferences
    implementation(libs.datastore.preferences)

    //Navigation
    implementation(libs.navigation.compose)

    // Coil
    implementation(libs.coil.compose)

    // Drawbox
    implementation(libs.drawbox)

    // Snapper
    implementation(libs.snapper)

    //RangVikalp
    implementation(libs.rang.vikalp)

    // Gson
    implementation(libs.gson)

    // Accompanist
    //Jetpack Navigation Compose Material
    implementation(libs.accompanist.navigation.material)
    //Permissions
    implementation(libs.accompanist.permissions)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.tooling)
}
