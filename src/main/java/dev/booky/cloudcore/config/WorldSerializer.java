package dev.booky.cloudcore.config;
// Created by booky10 in CloudCore (20:21 31.03.23)

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Objects;

public class WorldSerializer implements TypeSerializer<World> {

    public static final WorldSerializer INSTANCE = new WorldSerializer();

    private WorldSerializer() {
    }

    @Override
    public World deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (node.virtual()) {
            return null;
        }

        NamespacedKey dimKey = node.get(NamespacedKey.class);
        return Bukkit.getWorld(Objects.requireNonNull(dimKey));
    }

    @Override
    public void serialize(Type type, @Nullable World obj, ConfigurationNode node) throws SerializationException {
        node.set(obj == null ? null : obj.getKey());
    }
}
