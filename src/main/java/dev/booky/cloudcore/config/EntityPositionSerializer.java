package dev.booky.cloudcore.config;
// Created by booky10 in CloudCore (03:54 15.07.23)

import dev.booky.cloudcore.util.EntityPosition;
import io.papermc.paper.math.Position;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Objects;

public final class EntityPositionSerializer implements TypeSerializer<EntityPosition> {

    public static final EntityPositionSerializer INSTANCE = new EntityPositionSerializer();

    private EntityPositionSerializer() {
    }

    @Override
    public EntityPosition deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (node.virtual()) {
            return null;
        }

        float yaw = node.node("yaw").getFloat();
        float pitch = node.node("pitch").getFloat();

        Position pos = Objects.requireNonNull(node.get(Position.class));
        return EntityPosition.entity(pos, yaw, pitch);
    }

    @Override
    public void serialize(Type type, @Nullable EntityPosition obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.set(null);
            return;
        }

        node.set(Position.class, obj);
        if (obj.yaw() != 0f || obj.pitch() != 0f) {
            node.node("yaw").set(obj.yaw());
            node.node("pitch").set(obj.pitch());
        }
    }
}
