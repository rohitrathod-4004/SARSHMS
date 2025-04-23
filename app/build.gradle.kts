plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.sarshms"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.sarshms"
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
    // Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.7.3"))

    // Firebase dependencies (no versions!)
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-inappmessaging-display") // Use display module directly

    // Material & AndroidX
    implementation("com.google.android.material:material:1.11.0")
    implementation(libs.recyclerview.v132)
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.emoji2:emoji2:1.3.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.gridlayout:gridlayout:1.0.0")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
