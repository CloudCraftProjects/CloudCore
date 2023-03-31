package dev.booky.cloudcore.config;
// Created by booky10 in CloudCore (15:55 31.03.23)

import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class VectorSerializer implements TypeSerializer<Vector> {

    public static final VectorSerializer INSTANCE = new VectorSerializer();

    private VectorSerializer() {
    }

    @Override
    public Vector deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (node.virtual()) {
            return null;
        }

        Vector vector = new Vector();
        vector.setX(node.node("x").getDouble(0d));
        vector.setY(node.node("y").getDouble(0d));
        vector.setZ(node.node("z").getDouble(0d));
        return vector;
    }

    @Override
    public void serialize(Type type, @Nullable Vector obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.set(null);
            return;
        }

        node.node("x").set(obj.getX());
        node.node("y").set(obj.getY());
        node.node("z").set(obj.getZ());
    }
}
