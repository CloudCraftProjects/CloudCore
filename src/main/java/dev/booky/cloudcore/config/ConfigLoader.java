package dev.booky.cloudcore.config;
// Created by booky10 in TJCUpdater (12:55 27.06.22)

import org.bukkit.Location;
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
import java.util.Map;
import java.util.function.Consumer;

public final class ConfigLoader {

    private static final Map<Path, YamlConfigurationLoader> LOADER_CACHE = new HashMap<>();
    private static final Consumer<TypeSerializerCollection.Builder> NO_OP_SERIALIZERS = $ -> { /**/ };

    @ApiStatus.Internal
    public static <T extends AbstractConfigurationLoader.Builder<T, ?>> T setDefaultSerializers(T loaderBuilder, Consumer<TypeSerializerCollection.Builder> serializers) {
        return loaderBuilder.defaultOptions(opts -> opts.serializers(serializers.andThen(builder -> builder
                .register(EnumSerializer.INSTANCE)
                .register(NamespacedKeySerializer.INSTANCE)
                .register(World.class, WorldSerializer.INSTANCE)
                .register(BlockVector.class, BlockVectorSerializer.INSTANCE)
                .register(Vector.class, VectorSerializer.INSTANCE)
                .register(Block.class, BlockSerializer.INSTANCE)
                .register(Location.class, LocationSerializer.INSTANCE))));
    }

    @ApiStatus.Internal
    public static YamlConfigurationLoader createLoader(Path path, Consumer<TypeSerializerCollection.Builder> serializers) {
        return LOADER_CACHE.computeIfAbsent(path, $ -> setDefaultSerializers(YamlConfigurationLoader.builder(), serializers)
                .path(path).nodeStyle(NodeStyle.BLOCK).indent(2).build());
    }

    public static <T> T loadObject(Path path, Class<T> clazz) {
        return loadObject(path, clazz, NO_OP_SERIALIZERS);
    }

    public static <T> T loadObject(Path path, Class<T> clazz, Consumer<TypeSerializerCollection.Builder> serializers) {
        try {
            YamlConfigurationLoader loader = createLoader(path, serializers);
            T obj;

            if (Files.exists(path)) {
                obj = loader.load().get(clazz);
            } else {
                try {
                    Constructor<T> ctor = clazz.getDeclaredConstructor();
                    ctor.setAccessible(true);
                    obj = ctor.newInstance();
                } catch (ReflectiveOperationException exception) {
                    throw new RuntimeException(exception);
                }
            }

            CommentedConfigurationNode node = loader.createNode();
            node.set(clazz, obj);

            loader.save(node);
            return obj;
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static <T> void saveObject(Path path, T obj) {
        saveObject(path, obj, NO_OP_SERIALIZERS);
    }

    public static <T> void saveObject(Path path, T obj, Consumer<TypeSerializerCollection.Builder> serializers) {
        try {
            YamlConfigurationLoader loader = createLoader(path, serializers);
            CommentedConfigurationNode node = loader.createNode();
            node.set(obj.getClass(), obj);
            loader.save(node);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
