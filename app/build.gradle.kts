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
        versionName = "1.3"

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
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
}