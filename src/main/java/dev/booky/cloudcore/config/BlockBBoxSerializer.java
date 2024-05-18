package dev.booky.cloudcore.config;
// Created by booky10 in CraftAttack (13:15 05.10.22)

import dev.booky.cloudcore.util.BlockBBox;
import io.papermc.paper.math.BlockPosition;
import org.bukkit.World;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Objects;

public final class BlockBBoxSerializer implements TypeSerializer<BlockBBox> {

    public static final TypeSerializer<BlockBBox> INSTANCE = new BlockBBoxSerializer();

    private BlockBBoxSerializer() {
    }

    @Override
    public BlockBBox deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (node.virtual()) {
            return null;
        }

        World world = node.node("dimension").get(World.class);
        if (world == null) {
            world = node.node("world").get(World.class);
            if (world == null) {
                world = node.node("corner1", "dimension").get(World.class);
                if (world == null) {
                    world = node.node("corner1", "world").get(World.class);
                }
            }
        }
        Objects.requireNonNull(world, "No dimension/world found for bounding box");

        BlockPosition corner1 = node.node("corner1").get(BlockPosition.class);
        Objects.requireNonNull(corner1, "No first corner found for bounding box");

        BlockPosition corner2 = node.node("corner2").get(BlockPosition.class);
        Objects.requireNonNull(corner2, "No second corner found for bounding box");

        return new BlockBBox(world, corner1, corner2);
    }

    @Override
    public void serialize(Type type, @Nullable BlockBBox obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.set(null);
            return;
        }

        node.node("dimension").set(World.class, obj.getWorld());
        node.node("corner1").set(BlockPosition.class, obj.getMinPos());
        node.node("corner2").set(BlockPosition.class, obj.getMaxPos());
    }
}
