package dev.booky.cloudcore.config;
// Created by booky10 in CraftAttack (13:23 05.10.22)

import org.bukkit.NamespacedKey;
import org.spongepowered.configurate.serialize.ScalarSerializer;

import java.lang.reflect.Type;
import java.util.function.Predicate;

public final class NamespacedKeySerializer extends ScalarSerializer<NamespacedKey> {

    public static final NamespacedKeySerializer INSTANCE = new NamespacedKeySerializer();

    private NamespacedKeySerializer() {
        super(NamespacedKey.class);
    }

    @Override
    public NamespacedKey deserialize(Type type, Object obj) {
        return NamespacedKey.fromString(String.valueOf(obj));
    }

    @Override
    protected Object serialize(NamespacedKey item, Predicate<Class<?>> typeSupported) {
        return item.asString();
    }
}
