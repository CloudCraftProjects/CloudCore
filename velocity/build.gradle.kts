plugins {
    alias(libs.plugins.shadow)
}

dependencies {
    api(projects.cloudCoreCommon)
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }
}
