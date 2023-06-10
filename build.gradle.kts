plugins {
    id("java-library")
    id("maven-publish")

    id("net.minecrell.plugin-yml.bukkit") version "0.5.3"
    id("xyz.jpenilla.run-paper") version "1.0.6"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "dev.booky"
version = "1.0.0"

val plugin: Configuration by configurations.creating {
    isTransitive = false
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

val commandApiVersion = "9.0.2"

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20-R0.1-SNAPSHOT")

    // command library
    compileOnlyApi("dev.jorel:commandapi-bukkit-core:$commandApiVersion")
    compileOnlyApi("com.mojang:brigadier:1.0.18") // required for cmd api to compile

    // config library, included in paper
    compileOnlyApi("org.spongepowered:configurate-yaml:4.1.2")

    // metrics
    implementation("org.bstats:bstats-bukkit:3.0.2")

    // testserver dependency plugins
    plugin("dev.jorel:commandapi-bukkit-plugin:$commandApiVersion")
}

java {
    withSourcesJar()
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

publishing {
    publications.create<MavenPublication>("maven") {
        artifactId = project.name.lowercase()
        from(components["java"])
    }
}

bukkit {
    main = "$group.cloudcore.CloudCoreMain"
    apiVersion = "1.19"
    authors = listOf("booky10")
    depend = listOf("CommandAPI")
    load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.POSTWORLD
}

tasks {
    runServer {
        minecraftVersion("1.20")
        pluginJars.from(plugin.resolve())
    }

    shadowJar {
        relocate("org.bstats", "dev.booky.cloudcore.bstats")
    }

    assemble {
        dependsOn(shadowJar)
    }
}
