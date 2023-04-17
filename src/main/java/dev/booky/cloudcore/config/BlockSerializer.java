package dev.booky.cloudcore.config;
// Created by booky10 in CloudCore (14:56 14.03.23)

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;
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

        BlockVector pos = node.get(BlockVector.class);
        Objects.requireNonNull(pos);

        World world = node.node("dimension").get(World.class);
        Objects.requireNonNull(world);

        return world.getBlockAt(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
    }

    @Override
    public void serialize(Type type, @Nullable Block obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.set(null);
            return;
        }

        node.set(new BlockVector(obj.getX(), obj.getY(), obj.getZ()));
        node.node("dimension").set(obj.getWorld());
    }
}
