package dev.booky.cloudcore.config;
// Created by booky10 in CloudCore (14:56 14.03.23)

import org.bukkit.World;
import org.bukkit.block.Block;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Objects;

public final class BlockSerializer implements TypeSerializer<Block> {

    public static final BlockSerializer INSTANCE = new BlockSerializer();

    private BlockSerializer() {
    }

    @Override
    public Block deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (node.virtual()) {
            return null;
        }

        World world = node.node("dimension").get(World.class);
        int x = node.node("x").getInt();
        int y = node.node("y").getInt();
        int z = node.node("z").getInt();
        return Objects.requireNonNull(world).getBlockAt(x, y, z);
    }

    @Override
    public void serialize(Type type, @Nullable Block obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.set(null);
            return;
        }

        node.node("dimension").set(obj.getWorld());
        node.node("x").set(obj.getX());
        node.node("y").set(obj.getY());
        node.node("z").set(obj.getZ());
    }
}
