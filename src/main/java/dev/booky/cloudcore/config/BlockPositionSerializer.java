package dev.booky.cloudcore.config;
// Created by booky10 in CloudCore (03:46 15.07.23)

import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.Position;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public final class BlockPositionSerializer implements TypeSerializer<BlockPosition> {

    public static final BlockPositionSerializer INSTANCE = new BlockPositionSerializer();

    private BlockPositionSerializer() {
    }

    @Override
    public BlockPosition deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (node.virtual()) {
            return null;
        }

        int x = node.node("x").getInt();
        int y = node.node("y").getInt();
        int z = node.node("z").getInt();
        return Position.block(x, y, z);
    }

    @Override
    public void serialize(Type type, @Nullable BlockPosition obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.set(null);
            return;
        }

        node.node("x").set(obj.blockX());
        node.node("y").set(obj.blockY());
        node.node("z").set(obj.blockZ());
    }
}
