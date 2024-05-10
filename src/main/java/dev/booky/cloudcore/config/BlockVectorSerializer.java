package dev.booky.cloudcore.config;
// Created by booky10 in CloudCore (18:38 17.04.23)

import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public final class BlockVectorSerializer implements TypeSerializer<BlockVector> {

    public static final TypeSerializer<BlockVector> INSTANCE = new BlockVectorSerializer();

    private BlockVectorSerializer() {
    }

    @Override
    public BlockVector deserialize(Type type, ConfigurationNode node) {
        if (node.virtual()) {
            return null;
        }

        BlockVector vector = new BlockVector();
        vector.setX(node.node("x").getInt(0));
        vector.setY(node.node("y").getInt(0));
        vector.setZ(node.node("z").getInt(0));
        return vector;
    }

    @Override
    public void serialize(Type type, @Nullable BlockVector obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.set(null);
            return;
        }

        node.node("x").set(obj.getBlockX());
        node.node("y").set(obj.getBlockY());
        node.node("z").set(obj.getBlockZ());
    }
}
