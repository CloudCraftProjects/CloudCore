package dev.booky.cloudcore.config;
// Created by booky10 in CloudCore (03:44 15.07.23)

import io.papermc.paper.math.Position;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Objects;

public final class PositionSerializer implements TypeSerializer<Position> {

    public static final PositionSerializer INSTANCE = new PositionSerializer();

    private PositionSerializer() {
    }

    @Override
    public Position deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (node.virtual()) {
            return null;
        }

        Vector pos = Objects.requireNonNull(node.get(Vector.class));
        return Position.fine(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public void serialize(Type type, @Nullable Position obj, ConfigurationNode node) throws SerializationException {
        node.set(obj == null ? null : obj.toVector());
    }
}
