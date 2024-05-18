# CloudCore

Minecraft paper/velocity library plugin which most new plugins of CloudCraft depend on.

## Content

- Configurate based configuration loading with some default serializers
- Simple adventure translation loader

Bukkit specific:

- Block bounding box (with a defined world)
- EntityPosition, extends Paper's new (experimental) Position API with yaw and pitch
- Useful custom arguments when working with Paper's brigadier api

## Download

https://dl.cloudcraftmc.de/CloudCore

## Usage

Add the following to your `build.gradle.kts`:

```kotlin
repositories {
    maven("https://repo.cloudcraftmc.de/releases/")
}

dependencies {
    // bukkit dependency
    compileOnly("dev.booky:cloudcore:1.1.0-SNAPSHOT")
    // velocity dependency
    compileOnly("dev.booky:cloudcore-velocity:1.1.0-SNAPSHOT")
}
```

## License

Licensed under GPL-3.0, see [LICENSE](./LICENSE) for further information.
