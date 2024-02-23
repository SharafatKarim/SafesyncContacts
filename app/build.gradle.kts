plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.flarestudio.safesynccontacts"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.flarestudio.safesynccontacts"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "0.3"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

//    implementation("androidx.appcompat:appcompat")
    // For loading and tinting drawables on older versions of the platform
//    implementation("androidx.appcompat:appcompat-resources")

    //bottom naviagation
    implementation("com.etebarian:meow-bottom-navigation:1.2.0")

    //circular image
    implementation("de.hdodenhof:circleimageview:3.1.0")

    //image crop dependency
    implementation("com.theartofdev.edmodo:android-image-cropper:2.8.+")


}