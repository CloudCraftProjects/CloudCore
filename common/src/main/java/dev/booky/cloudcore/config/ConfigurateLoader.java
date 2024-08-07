package dev.booky.cloudcore.config;
// Created by booky10 in CloudCore (04:36 10.05.2024.)

import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.AbstractConfigurationLoader;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static dev.booky.cloudcore.config.IConfigurateLoaderPlatform.PLATFORM;

public class ConfigurateLoader<
        L extends AbstractConfigurationLoader<?>,
        B extends AbstractConfigurationLoader.Builder<B, L>
        > {

    private static final boolean SAVE_BY_DEFAULT = true;

    private final Supplier<B> builder;
    private final Function<B, B> configurator;

    private ConfigurateLoader(Supplier<B> builder, Function<B, B> configurator) {
        this.builder = builder;
        this.configurator = configurator;
    }

    public static Builder<YamlConfigurationLoader, YamlConfigurationLoader.Builder> yamlLoader() {
        return loader(YamlConfigurationLoader::builder)
                .configure(builder -> builder.nodeStyle(NodeStyle.BLOCK).indent(2));
    }

    public static <
            L extends AbstractConfigurationLoader<?>,
            B extends AbstractConfigurationLoader.Builder<B, L>
            > Builder<L, B> loader(Supplier<B> builder) {
        return new Builder<>(builder);
    }

    private static <T> Supplier<T> buildConstructor(Class<T> clazz) {
        return () -> {
            // use reflection to create default instance, may not work
            try {
                Constructor<T> ctor = clazz.getDeclaredConstructor();
                ctor.setAccessible(true);
                return ctor.newInstance();
            } catch (ReflectiveOperationException exception) {
                throw new RuntimeException("Can't create default instance of "
                        + clazz.getName() + ", please specify constructor", exception);
            }
        };
    }

    // object loading

    public <T> T loadObject(Path path, Class<T> clazz) {
        return this.loadObject(path, clazz, buildConstructor(clazz), SAVE_BY_DEFAULT);
    }

    public <T> T loadObject(Path path, Class<T> clazz, Supplier<T> constructor) {
        return this.loadObject(path, TypeToken.get(clazz), constructor, SAVE_BY_DEFAULT);
    }

    public <T> T loadObject(Path path, TypeToken<T> type, Supplier<T> constructor) {
        return this.loadObject(path, type, constructor, SAVE_BY_DEFAULT);
    }

    public <T> T loadObject(Path path, Class<T> clazz, boolean save) {
        return this.loadObject(path, TypeToken.get(clazz), buildConstructor(clazz), save);
    }

    public <T> T loadObject(Path path, Class<T> clazz, Supplier<T> constructor, boolean save) {
        return this.loadObject(path, TypeToken.get(clazz), constructor, save);
    }

    public <T> T loadObject(Path path, TypeToken<T> type, Supplier<T> constructor, boolean save) {
        return this.loadObject(getLoader(path), type, constructor, save);
    }

    public <T> T loadObject(L loader, Class<T> clazz) {
        return this.loadObject(loader, clazz, buildConstructor(clazz), SAVE_BY_DEFAULT);
    }

    public <T> T loadObject(L loader, Class<T> clazz, Supplier<T> constructor) {
        return this.loadObject(loader, TypeToken.get(clazz), constructor, SAVE_BY_DEFAULT);
    }

    public <T> T loadObject(L loader, TypeToken<T> type, Supplier<T> constructor) {
        return this.loadObject(loader, type, constructor, SAVE_BY_DEFAULT);
    }

    public <T> T loadObject(L loader, Class<T> clazz, boolean save) {
        return this.loadObject(loader, TypeToken.get(clazz), buildConstructor(clazz), save);
    }

    public <T> T loadObject(L loader, Class<T> clazz, Supplier<T> constructor, boolean save) {
        return this.loadObject(loader, TypeToken.get(clazz), constructor, save);
    }

    public <T> T loadObject(L loader, TypeToken<T> type, Supplier<T> constructor, boolean save) {
        try {
            T initialObject = loader.load().get(type);
            T defaultedObject = Objects.requireNonNullElseGet(initialObject, constructor);

            if (save) {
                this.saveObject(loader, defaultedObject, type);
            }
            return defaultedObject;
        } catch (IOException exception) {
            throw new RuntimeException("Error while loading object from "
                    + this + " (loader: " + loader + ") with type " + type.getType(), exception);
        }
    }

    // object saving

    @SuppressWarnings("unchecked") // not unchecked
    public <T> void saveObject(Path path, T object) {
        Class<T> clazz = (Class<T>) object.getClass();
        this.saveObject(path, object, clazz);
    }

    public <T> void saveObject(Path path, T object, Class<T> clazz) {
        this.saveObject(path, object, TypeToken.get(clazz));
    }

    public <T> void saveObject(Path path, T object, TypeToken<T> type) {
        this.saveObject(this.getLoader(path), object, type);
    }

    @SuppressWarnings("unchecked") // not unchecked
    public <T> void saveObject(L loader, T object) {
        Class<T> clazz = (Class<T>) object.getClass();
        this.saveObject(loader, object, clazz);
    }

    public <T> void saveObject(L loader, T object, Class<T> clazz) {
        this.saveObject(loader, object, TypeToken.get(clazz));
    }

    public <T> void saveObject(L loader, T object, TypeToken<T> type) {
        try {
            ConfigurationNode node = loader.createNode();
            node.set(type, object);
            loader.save(node);
        } catch (IOException exception) {
            throw new RuntimeException("Error while saving object to "
                    + this + " (loader: " + loader + ") with type " + type.getType(), exception);
        }
    }

    // loader building

    public L getLoader(
            @Nullable Callable<BufferedReader> reader,
            @Nullable Callable<BufferedWriter> writer
    ) {
        return this.getLoader(builder -> builder.source(reader).sink(writer));
    }

    public L getLoader(Path path) {
        return this.getLoader(builder -> builder.path(path));
    }

    public L getLoader() {
        return this.getLoader(Function.identity());
    }

    public L getLoader(Function<B, B> configurator) {
        return this.configurator.andThen(configurator)
                .apply(this.builder.get()).build();
    }

    public static final class Builder<
            L extends AbstractConfigurationLoader<?>,
            B extends AbstractConfigurationLoader.Builder<B, L>
            > {

        private static final TypeSerializerCollection PLATFORM_SERIALIZERS
                = PLATFORM.buildDefaultSerializers(TypeSerializerCollection.builder()).build();

        private final Supplier<B> builder;
        private Function<B, B> configurator = builder -> builder.defaultOptions(
                opts -> opts.implicitInitialization(false));

        private Builder(Supplier<B> builder) {
            this.builder = builder;
        }

        public ConfigurateLoader<L, B> build() {
            return new ConfigurateLoader<>(this.builder, this.configurator);
        }

        public Builder<L, B> withAllDefaultSerializers() {
            return this.withSerializers(PLATFORM_SERIALIZERS);
        }

        public Builder<L, B> withSerializers(TypeSerializerCollection serializers) {
            return this.withSerializers(builder -> builder.registerAll(serializers));
        }

        public Builder<L, B> withSerializers(Consumer<TypeSerializerCollection.Builder> serializers) {
            return this.configure(loader -> loader.defaultOptions(opts ->
                    opts.serializers(serializers)));
        }

        public Builder<L, B> configure(Function<B, B> configurator) {
            this.configurator = this.configurator.andThen(configurator);
            return this;
        }
    }
}
