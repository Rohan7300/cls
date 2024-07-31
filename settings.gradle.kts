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

    }
}

rootProject.name = "CLS DA"
include(":app","library")

 