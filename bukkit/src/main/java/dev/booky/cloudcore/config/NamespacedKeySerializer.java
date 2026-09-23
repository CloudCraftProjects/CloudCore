package dev.booky.cloudcore.config;
// Created by booky10 in CraftAttack (13:23 05.10.22)

import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.function.Predicate;

@NullMarked
public final class NamespacedKeySerializer extends ScalarSerializer<NamespacedKey> {

    public static final TypeSerializer<NamespacedKey> INSTANCE = new NamespacedKeySerializer();

    private NamespacedKeySerializer() {
        super(NamespacedKey.class);
    }

    @Override
    public NamespacedKey deserialize(Type type, Object obj) throws SerializationException {
        NamespacedKey key = NamespacedKey.fromString(String.valueOf(obj));
        if (key == null) {
            throw new SerializationException("Invalid namespaced key: " + obj);
        }
        return key;
    }

    @Override
    protected Object serialize(NamespacedKey item, Predicate<Class<?>> typeSupported) {
        return item.asString();
    }
}
