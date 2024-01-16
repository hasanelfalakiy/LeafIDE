plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)
}

android {
    namespace = "leaf.nodejs.module"

    viewBinding {
        enable = true
    }

}

dependencies {
    implementation(project(":common"))
    implementation(project(":module-api"))
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.core.ktx)
    implementation(libs.kotlin.reflect)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.material)
    implementation(libs.toasty)
}