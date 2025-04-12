package dev.booky.cloudcore.config;
// Created by booky10 in CloudCore (15:55 31.03.23)

import org.bukkit.util.Vector;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

@NullMarked
public final class VectorSerializer implements TypeSerializer<Vector> {

    public static final TypeSerializer<Vector> INSTANCE = new VectorSerializer();

    private VectorSerializer() {
    }

    @Override
    public @Nullable Vector deserialize(Type type, ConfigurationNode node) {
        if (node.virtual()) {
            return null;
        }
        return new Vector(
                node.node("x").getDouble(),
                node.node("y").getDouble(),
                node.node("z").getDouble()
        );
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
