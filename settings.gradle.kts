@file:Suppress("UnstableApiUsage")


include(":common")
include(":library-merminal")
include(":plugin-api")
include(":plugin-nodejs")
include(":app")

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