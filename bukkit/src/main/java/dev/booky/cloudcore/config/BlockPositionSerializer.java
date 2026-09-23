package dev.booky.cloudcore.config;
// Created by booky10 in CloudCore (03:46 15.07.23)

import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.Position;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Arrays;

import static java.lang.Integer.parseInt;

@NullMarked
public final class BlockPositionSerializer implements TypeSerializer<BlockPosition> {

    public static final TypeSerializer<BlockPosition> INSTANCE = new BlockPositionSerializer();

    private BlockPositionSerializer() {
    }

    @Override
    public @Nullable BlockPosition deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (node.virtual()) {
            return null;
        }
        if (node.isMap()) {
            int x = node.node("x").getInt();
            int y = node.node("y").getInt();
            int z = node.node("z").getInt();
            return Position.block(x, y, z);
        }
        String[] coords = StringUtils.split(String.valueOf(node.raw()), ':');
        if (coords.length != 3) {
            throw new SerializationException("Illegal block position coords: " + Arrays.toString(coords));
        }
        return Position.block(parseInt(coords[0]), parseInt(coords[1]), parseInt(coords[2]));
    }

    @Override
    public void serialize(Type type, @Nullable BlockPosition obj, ConfigurationNode node) throws SerializationException {
        node.set(obj == null ? null : obj.blockX() + ":" + obj.blockY() + ":" + obj.blockZ());

    }
}
