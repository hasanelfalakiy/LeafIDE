@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.com.android.library)
}

android {
    namespace = "com.termux.terminal"

    defaultConfig.externalNativeBuild {
        cmake {
            cppFlags("-std=c++17")
        }
    }

    externalNativeBuild {
        cmake {
            path("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
}

dependencies {
    implementation(libs.annotation)
}