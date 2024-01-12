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
    implementation(platform(libs.editor.bom))
    implementation(libs.editor)
    implementation(libs.kotlin.reflect)
    implementation(libs.material)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.preference.ktx)
    implementation(libs.recyclerview.adapter.helper)
    implementation(libs.toasty)
}