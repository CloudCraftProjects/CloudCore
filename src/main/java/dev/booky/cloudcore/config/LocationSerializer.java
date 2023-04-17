package dev.booky.cloudcore.config;
// Created by booky10 in CraftAttack (13:15 05.10.22)

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Objects;

public final class LocationSerializer implements TypeSerializer<Location> {

    public static final LocationSerializer INSTANCE = new LocationSerializer();

    private LocationSerializer() {
    }

    @Override
    public Location deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (node.virtual()) {
            return null;
        }

        World world = node.node("dimension").get(World.class);
        if (world == null) {
            // old name for this
            world = node.node("world").get(World.class);
        }
        Objects.requireNonNull(world, "No dimension/world found");

        float yaw = node.node("yaw").getFloat(0f);
        float pitch = node.node("pitch").getFloat(0f);

        Vector pos = Objects.requireNonNull(node.get(Vector.class));
        return pos.toLocation(world, yaw, pitch);
    }

    @Override
    public void serialize(Type type, @Nullable Location obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.set(null);
            return;
        }

        node.set(obj.toVector());
        node.node("dimension").set(obj.getWorld());
        if (obj.getYaw() != 0f || obj.getPitch() != 0f) {
            node.node("yaw").set(obj.getYaw());
            node.node("pitch").set(obj.getPitch());
        }
    }
}
