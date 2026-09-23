plugins {
    alias(libs.plugins.pluginyml.bukkit)
    alias(libs.plugins.run.paper)
}

dependencies {
    api(projects.common)
    compileOnly(libs.paper.api)

    // metrics
    implementation(libs.bstats.bukkit)
}

bukkit {
    name = "CloudCore"
    main = "$group.cloudcore.CloudCoreBukkitMain"
    apiVersion = "1.20"
    authors = listOf("booky10")
    website = "https://github.com/CloudCraftProjects/CloudCore"
    foliaSupported = true
}

tasks {
    runServer {
        minecraftVersion(libs.versions.paper.map { it.split("-")[0] }.get())

        downloadPlugins {
            github(
                "PaperMC", "Debuggery",
                "v${libs.versions.debuggery.get()}",
                "debuggery-bukkit-${libs.versions.debuggery.get()}.jar"
            )
        }
    }

    shadowJar {
        relocate("org.bstats", "${project.group}.cloudcore.bstats")
    }

    assemble {
        dependsOn(shadowJar)
    }

    withType<Jar> {
        manifest.attributes(
            "paperweight-mappings-namespace" to "mojang"
        )
    }
}
