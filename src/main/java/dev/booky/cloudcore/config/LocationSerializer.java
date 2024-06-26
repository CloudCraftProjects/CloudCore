package dev.booky.cloudcore.config;
// Created by booky10 in CraftAttack (13:15 05.10.22)

import org.bukkit.Location;
import org.bukkit.World;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Objects;

public final class LocationSerializer implements TypeSerializer<Location> {

    public static final TypeSerializer<Location> INSTANCE = new LocationSerializer();

    private LocationSerializer() {
    }

    @Override
    public Location deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (node.virtual()) {
            return null;
        }

        World world = node.node("dimension").get(World.class);
        if (world == null) {
            world = node.node("world").get(World.class);
        }
        Objects.requireNonNull(world, "No dimension/world found");

        return new Location(
                world,
                node.node("x").getDouble(),
                node.node("y").getDouble(),
                node.node("z").getDouble(),
                node.node("yaw").getFloat(),
                node.node("pitch").getFloat()
        );
    }

    @Override
    public void serialize(Type type, @Nullable Location obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.set(null);
            return;
        }

        node.node("dimension").set(obj.getWorld());
        node.node("x").set(obj.getX());
        node.node("y").set(obj.getY());
        node.node("z").set(obj.getZ());
        if (obj.getYaw() != 0f || obj.getPitch() != 0f) {
            node.node("yaw").set(obj.getYaw());
            node.node("pitch").set(obj.getPitch());
        }
    }
}
