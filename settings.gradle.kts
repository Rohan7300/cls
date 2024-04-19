pluginManagement {
    repositories {
        google()
        jcenter()
        mavenCentral()
        mavenLocal()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        jcenter()
        mavenLocal()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://europe-maven.pkg.dev/anyline-ttr-sdk/maven") }
        maven {
            url =uri("https://maven.mozilla.org/maven2/")
        }

    }
}

rootProject.name = "Celerity"
include(":app")

 