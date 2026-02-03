pluginManagement {
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
    }
}

rootProject.name = "MotoBridge-AAOS"

include(":core")
project(":core").projectDir = file("motobridge-core")

include(":driver-app-aaos")
project(":driver-app-aaos").projectDir = file("aaos-app")

include(":driver-app-mobile")
project(":driver-app-mobile").projectDir = file("driver-sim-app")

include(":aaos-policy-adapter")
include(":mobile-policy-adapter")
include(":vehicle")
