enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "CloudCore"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

listOf("common", "velocity").forEach { name ->
    val projectName = "${rootProject.name}-${name.replaceFirstChar { it.titlecase() }}"
    include(projectName)
    findProject(":${projectName}")!!.projectDir = file(name)
}
