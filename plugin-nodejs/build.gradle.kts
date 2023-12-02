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

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.kotlinCompilerExtensionVersion
    }

}

dependencies {
    compileOnly(project(":common"))
    compileOnly(project(":plugin-api"))

    implementation(project(":compose-ui"))
    implementation(platform(libs.compose.bom))
    implementation(libs.activity.compose)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)

    implementation(libs.core.ktx)
    compileOnly(libs.appcompat)
    compileOnly(libs.material)
    compileOnly(libs.constraintlayout)
    compileOnly(libs.navigation.fragment.ktx)
    compileOnly(libs.navigation.ui.ktx)
    compileOnly(libs.preference.ktx)
    compileOnly(libs.toasty)
}