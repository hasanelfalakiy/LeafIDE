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

    packaging {
        resources.excludes.addAll(setOf("assets/.gitattributes"))
    }
}

dependencies {
    implementation(project(":common"))
    implementation(project(":plugin-api"))
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.core.ktx)
    implementation(libs.kotlin.reflect)
    implementation(libs.material)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.preference.ktx)
    implementation(libs.toasty)
}