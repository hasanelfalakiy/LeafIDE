plugins {
    alias(libs.plugins.com.android.library)
}

android {
    namespace = "com.termux.view"
}

dependencies {
    implementation(libs.annotation)
    implementation(project(":terminal-emulator"))
}