pluginManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenLocal()
        mavenCentral()
        jcenter()
        maven { url = uri("https://jitpack.io") }
        maven { url= uri("https://jcenter.bintray.com/") }
        maven { url = uri("https://europe-maven.pkg.dev/anyline-ttr-sdk/maven") }

    }
}

rootProject.name = "CLS DA"
include(":app","library")

 
