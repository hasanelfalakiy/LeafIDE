@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
}

android {
    namespace = "leaf.plugin.nodejs"

    defaultConfig {
        applicationId = "leaf.plugin.nodejs"
        versionCode = NodeJSPluginVersions.VERSION_CODE
        versionName = NodeJSPluginVersions.VERSION_NAME
    }

    viewBinding {
        enable = true
    }

}

dependencies {
    implementation(project(":common"))
    implementation(project(":plugin-api"))
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.preference.ktx)
    implementation(libs.toasty)
    implementation(libs.kotlin.reflect)
}