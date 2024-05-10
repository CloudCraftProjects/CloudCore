package dev.booky.cloudcore.config;
// Created by booky10 in TJCUpdater (12:55 27.06.22)

import dev.booky.cloudcore.util.BlockBBox;
import dev.booky.cloudcore.util.EntityPosition;
import io.leangen.geantyref.TypeToken;
import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.FinePosition;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.loader.AbstractConfigurationLoader;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ConfigLoader {

    private static final Map<Path, YamlConfigurationLoader> LOADER_CACHE = new HashMap<>();
    private static final Consumer<TypeSerializerCollection.Builder> NO_OP_SERIALIZERS = $ -> { /**/ };

    @ApiStatus.Internal
    public static <T extends AbstractConfigurationLoader.Builder<T, ?>> T setDefaultSerializers(T loaderBuilder, Consumer<TypeSerializerCollection.Builder> serializers) {
        return loaderBuilder.defaultOptions(opts -> opts.serializers(serializers.andThen(builder -> builder
                .register(new TypeToken<Enum<?>>() {}, EnumSerializer.INSTANCE)
                .register(NamespacedKey.class, NamespacedKeySerializer.INSTANCE)
                .register(Locale.class, LocaleSerializer.INSTANCE)
                .register(World.class, WorldSerializer.INSTANCE)
                .register(BlockVector.class, BlockVectorSerializer.INSTANCE)
                .register(Vector.class, VectorSerializer.INSTANCE)
                .register(Block.class, BlockSerializer.INSTANCE)
                .register(Location.class, LocationSerializer.INSTANCE)
                .register(BlockBBox.class, BlockBBoxSerializer.INSTANCE)
                .register(EntityPosition.class, EntityPositionSerializer.INSTANCE)
                .register(BlockPosition.class, BlockPositionSerializer.INSTANCE)
                .register(FinePosition.class, FinePositionSerializer.INSTANCE))));
    }

    @ApiStatus.Internal
    public static YamlConfigurationLoader createLoader(
            Path path, Consumer<TypeSerializerCollection.Builder> serializers
    ) {
        return LOADER_CACHE.computeIfAbsent(path, $ ->
                setDefaultSerializers(YamlConfigurationLoader.builder(), serializers)
                        .path(path).nodeStyle(NodeStyle.BLOCK).indent(2).build());
    }

    private static <T> Supplier<T> defSupplier(Class<T> clazz) {
        return () -> {
            try {
                Constructor<T> ctor = clazz.getDeclaredConstructor();
                ctor.setAccessible(true);
                return ctor.newInstance();
            } catch (ReflectiveOperationException exception) {
                throw new RuntimeException(exception);
            }
        };
    }

    public static <T> T loadObject(Path path, Class<T> clazz) {
        return loadObject(path, clazz, NO_OP_SERIALIZERS);
    }

    public static <T> T loadObject(
            Path path, Class<T> clazz, Consumer<TypeSerializerCollection.Builder> serializers
    ) {
        return loadObject(path, TypeToken.get(clazz), defSupplier(clazz), serializers);
    }

    public static <T> T loadObject(Path path, TypeToken<T> type, Supplier<T> defSupplier) {
        return loadObject(path, type, defSupplier, NO_OP_SERIALIZERS);
    }

    public static <T> T loadObject(
            Path path, TypeToken<T> type, Supplier<T> defSupplier,
            Consumer<TypeSerializerCollection.Builder> serializers
    ) {
        try {
            YamlConfigurationLoader loader = createLoader(path, serializers);
            T obj;

            if (Files.exists(path)) {
                obj = loader.load().get(type);
            } else {
                obj = defSupplier.get();
            }

            CommentedConfigurationNode node = loader.createNode();
            node.set(type, obj);

            loader.save(node);
            return obj;
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static <T> void saveObject(Path path, T obj) {
        saveObject(path, obj, NO_OP_SERIALIZERS);
    }

    @SuppressWarnings("unchecked") // idk how this is unchecked
    public static <T> void saveObject(
            Path path, T obj, Consumer<TypeSerializerCollection.Builder> serializers
    ) {
        Class<T> clazz = (Class<T>) obj.getClass();
        saveObject(path, TypeToken.get(clazz), obj, serializers);
    }

    public static <T> void saveObject(Path path, TypeToken<T> type, T obj) {
        saveObject(path, type, obj, NO_OP_SERIALIZERS);
    }

    public static <T> void saveObject(
            Path path, TypeToken<T> type, T obj,
            Consumer<TypeSerializerCollection.Builder> serializers
    ) {
        try {
            YamlConfigurationLoader loader = createLoader(path, serializers);
            CommentedConfigurationNode node = loader.createNode();
            node.set(type, obj);
            loader.save(node);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
