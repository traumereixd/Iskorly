pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    plugins {
        kotlin("android") version "1.8.22"
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") } // For uCrop
    }
}

rootProject.name = "ItemScoreAnalysisProgram"
include(":app")