package dev.booky.cloudcore.config;
// Created by booky10 in CloudCore (03:44 15.07.23)

import io.papermc.paper.math.FinePosition;
import io.papermc.paper.math.Position;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

@NullMarked
public final class FinePositionSerializer implements TypeSerializer<FinePosition> {

    public static final TypeSerializer<FinePosition> INSTANCE = new FinePositionSerializer();

    private FinePositionSerializer() {
    }

    @Override
    public @Nullable FinePosition deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (node.virtual()) {
            return null;
        }
        return Position.fine(
                node.node("x").getDouble(),
                node.node("y").getDouble(),
                node.node("z").getDouble()
        );
    }

    @Override
    public void serialize(Type type, @Nullable FinePosition obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.set(null);
            return;
        }
        node.node("x").set(obj.x());
        node.node("y").set(obj.y());
        node.node("z").set(obj.z());
    }
}
