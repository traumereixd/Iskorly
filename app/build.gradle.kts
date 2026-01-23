import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.bandecoot.itemscoreanalysisprogram"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bandecoot.itemscoreanalysisprogram"
        minSdk = 23
        targetSdk = 34
        versionCode = 3
        versionName = "1.8.1"

        // Inject Vision API key from local.properties or Gradle property
        val props = Properties().apply {
            val f = rootProject.file("local.properties")
            if (f.exists()) f.inputStream().use { load(it) }
        }
        val keyFromProp = (project.findProperty("GCLOUD_VISION_API_KEY") as? String)?.trim()
        val keyFromLocal = props.getProperty("GCLOUD_VISION_API_KEY")?.trim()
        val visionKey = when {
            !keyFromProp.isNullOrEmpty() -> keyFromProp
            !keyFromLocal.isNullOrEmpty() -> keyFromLocal
            else -> ""
        }
        buildConfigField("String", "GCLOUD_VISION_API_KEY", "\"$visionKey\"")
        
        // OCR.Space API key (optional fallback for handwriting)
        val ocrSpaceKeyFromProp = (project.findProperty("OCR_SPACE_API_KEY") as? String)?.trim()
        val ocrSpaceKeyFromLocal = props.getProperty("OCR_SPACE_API_KEY")?.trim()
        val ocrSpaceKey = when {
            !ocrSpaceKeyFromProp.isNullOrEmpty() -> ocrSpaceKeyFromProp
            !ocrSpaceKeyFromLocal.isNullOrEmpty() -> ocrSpaceKeyFromLocal
            else -> ""
        }
        buildConfigField("String", "OCR_SPACE_API_KEY", "\"$ocrSpaceKey\"")
        
        // OCR multi-variant configuration
        buildConfigField("int", "MAX_VARIANTS", "8")
        buildConfigField("float", "EARLY_EXIT_FILLED_THRESHOLD", "0.70f")
        
        // Optional AI re-parser endpoint for low-confidence results
        val reparseEndpoint = props.getProperty("REPARSE_ENDPOINT")?.trim() ?: ""
        buildConfigField("String", "REPARSE_ENDPOINT", "\"$reparseEndpoint\"")
        buildConfigField("float", "REPARSE_MIN_FILLED_THRESHOLD", "0.50f")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures { buildConfig = true }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug { }
    }

    testOptions { unitTests.isIncludeAndroidResources = true }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions { jvmTarget = "11" }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.6.1") // was 1.7.0
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    
    // uCrop for robust crop + arbitrary rotation and improved UI
    implementation("com.github.yalantis:ucrop:2.2.8")
    
    // CanHub Android Image Cropper for image cropping functionality (Feature 2.1)
    // Published on Maven Central - Supports rotation, flip, aspect ratios, and free-form cropping
    // This library provides robust arbitrary rotation equivalent to uCrop
    // Kept as fallback for SimpleCropActivity
    implementation("com.vanniktech:android-image-cropper:4.5.0")
    
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
}