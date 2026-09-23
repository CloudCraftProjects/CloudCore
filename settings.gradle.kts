enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "CloudCore"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include("common", "velocity", "bukkit")
