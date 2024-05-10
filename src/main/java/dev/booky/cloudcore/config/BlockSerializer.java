package dev.booky.cloudcore.config;
// Created by booky10 in CloudCore (14:56 14.03.23)

import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.Position;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Objects;

public final class BlockSerializer implements TypeSerializer<Block> {

    public static final TypeSerializer<Block> INSTANCE = new BlockSerializer();

    private BlockSerializer() {
    }

    @Override
    public Block deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (node.virtual()) {
            return null;
        }

        BlockPosition pos = node.hasChild("pos")
                ? node.node("pos").get(BlockPosition.class, Position.BLOCK_ZERO)
                : node.get(BlockPosition.class, Position.BLOCK_ZERO);

        World world = node.node("dimension").get(World.class);
        if (world == null) {
            world = node.node("world").get(World.class);
        }
        Objects.requireNonNull(world, "No dimension/world found");

        return world.getBlockAt(pos.blockX(), pos.blockY(), pos.blockZ());
    }

    @Override
    public void serialize(Type type, @Nullable Block obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.set(null);
            return;
        }

        node.node("pos").set(BlockPosition.class, Position.block(obj.getX(), obj.getY(), obj.getZ()));
        node.node("dimension").set(World.class, obj.getWorld());
    }
}
