@file:Suppress("UnstableApiUsage")

include(":module-nodejs")


include(":common")
include(":library-merminal")
include(":module-api")
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