package dev.booky.cloudcore.config;
// Created by booky10 in CloudCore (03:54 15.07.23)

import dev.booky.cloudcore.util.EntityPosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

@NullMarked
public final class EntityPositionSerializer implements TypeSerializer<EntityPosition> {

    public static final TypeSerializer<EntityPosition> INSTANCE = new EntityPositionSerializer();

    private EntityPositionSerializer() {
    }

    @Override
    public @Nullable EntityPosition deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (node.virtual()) {
            return null;
        }
        return EntityPosition.entity(
                node.node("x").getDouble(),
                node.node("y").getDouble(),
                node.node("z").getDouble(),
                node.node("yaw").getFloat(),
                node.node("pitch").getFloat()
        );
    }

    @Override
    public void serialize(Type type, @Nullable EntityPosition obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.set(null);
            return;
        }
        node.node("x").set(obj.x());
        node.node("y").set(obj.y());
        node.node("z").set(obj.z());
        if (obj.yaw() != 0f || obj.pitch() != 0f) {
            node.node("yaw").set(obj.yaw());
            node.node("pitch").set(obj.pitch());
        }
    }
}
