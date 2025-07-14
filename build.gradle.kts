// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.realm.io")
    }
    dependencies {
        classpath("io.realm:realm-gradle-plugin:10.18.0")
    }
}