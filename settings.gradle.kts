pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    this.repositories {
        google()
        mavenCentral()
        // Add the JitPack repository
        maven {
            url = uri("https://jitpack.io")
        }
    }
}
rootProject.name = "GNINEcs460"
include(":app")