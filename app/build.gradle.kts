plugins {
    alias(libs.plugins.android.application)
}

apply(plugin = "realm-android")

android {
    namespace = "com.example.waterguard"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.waterguard"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Realm Database for Java
    implementation("io.realm:realm-gradle-plugin:10.19.0")

    // Circle Image View
    implementation("de.hdodenhof:circleimageview:3.1.0")
}