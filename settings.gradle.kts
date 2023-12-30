@file:Suppress("UnstableApiUsage")

include(":merminal")


include(":plugin-nodejs")
include(":plugin-api")
include(":app")
include(":common")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://androidx.dev/storage/compose-compiler/repository") }
    }
}

rootProject.name = "LeafIDE"