package dev.booky.cloudcore.config;
// Created by booky10 in TJCUpdater (12:55 27.06.22)

import io.leangen.geantyref.TypeToken;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @deprecated use {@link ConfigurateLoader} instead
 */
@NullMarked
@Deprecated(forRemoval = true)
public final class ConfigLoader {

    private static final ConfigurateLoader<?, ?> STATIC_LOADER = buildLoader($ -> { /**/ });

    private ConfigLoader() {
    }

    private static ConfigurateLoader<?, ?> buildLoader(Consumer<TypeSerializerCollection.Builder> serializers) {
        return ConfigurateLoader.yamlLoader().withSerializers(serializers).withAllDefaultSerializers().build();
    }

    // object loading

    public static <T> T loadObject(Path path, Class<T> clazz) {
        return STATIC_LOADER.loadObject(path, clazz);
    }

    public static <T> T loadObject(
            Path path, Class<T> clazz,
            Consumer<TypeSerializerCollection.Builder> serializers
    ) {
        return buildLoader(serializers).loadObject(path, clazz);
    }

    public static <T> T loadObject(Path path, TypeToken<T> type, Supplier<T> constructor) {
        return STATIC_LOADER.loadObject(path, type, constructor);
    }

    public static <T> T loadObject(
            Path path, TypeToken<T> type, Supplier<T> defSupplier,
            Consumer<TypeSerializerCollection.Builder> serializers
    ) {
        return buildLoader(serializers).loadObject(path, type, defSupplier);
    }

    // object saving

    public static <T> void saveObject(Path path, T obj) {
        STATIC_LOADER.saveObject(path, obj);
    }

    public static <T> void saveObject(
            Path path, T obj,
            Consumer<TypeSerializerCollection.Builder> serializers
    ) {
        buildLoader(serializers).saveObject(path, obj);
    }

    public static <T> void saveObject(Path path, TypeToken<T> type, T obj) {
        STATIC_LOADER.saveObject(path, obj, type);
    }

    public static <T> void saveObject(
            Path path, TypeToken<T> type, T obj,
            Consumer<TypeSerializerCollection.Builder> serializers
    ) {
        buildLoader(serializers).saveObject(path, obj, type);
    }
}
