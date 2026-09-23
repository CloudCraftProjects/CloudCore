plugins {
    alias(libs.plugins.run.velocity)
    alias(libs.plugins.shadow)
}

dependencies {
    api(projects.cloudCoreCommon)

    compileOnly(libs.velocity.api)

    // metrics
    implementation(libs.bstats.velocity)
}

tasks {
    runVelocity {
        velocityVersion(libs.versions.velocity.get())
    }

    shadowJar {
        relocate("org.bstats", "${project.group}.cloudcore.bstats")
    }

    assemble {
        dependsOn(shadowJar)
    }

    processResources {
        inputs.property("version", project.version)
        filesMatching("velocity-plugin.json") {
            expand("version" to project.version)
        }
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}
