plugins {
    alias(libs.plugins.run.velocity)
    alias(libs.plugins.shadow)
    alias(libs.plugins.blossom)
}

dependencies {
    api(projects.cloudCoreCommon)

    compileOnly(libs.velocity.api)
    annotationProcessor(libs.velocity.api)

    // metrics
    implementation(libs.bstats.velocity)
}

sourceSets {
    main {
        blossom {
            javaSources {
                property("version", project.version.toString())
            }
        }
    }
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
}
