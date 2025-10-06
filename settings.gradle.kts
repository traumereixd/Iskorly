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
        maven { url = uri("https://jitpack.io") } // For other dependencies if needed
    }
}

rootProject.name = "ItemScoreAnalysisProgram"
include(":app")