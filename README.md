# CloudCore

Minecraft paper library plugin which most new plugins of CloudCraft depend on.

## Content

- Configurate yaml based configuration loaded (+ some default serializers)
- Block bounding box (with a defined world)
- Simple adventure translation loader
- EntityPosition, extends Paper's new (experimental) Position API with yaw and pitch

## Download

https://nightly.link/CloudCraftProjects/CloudCore/workflows/build/master/CloudCore-Artifacts.zip

## Usage

Add the following to your `build.gradle.kts`:

```kotlin
repositories {
    maven("https://repo.cloudcraftmc.de/releases/")
}

dependencies {
    compileOnly("dev.booky:cloudcore:1.0.3-SNAPSHOT")
}
```

## License

Licensed under GPL-3.0, see [LICENSE](./LICENSE) for further information.
