package dev.booky.cloudcore.config;
// Created by booky10 in CloudCore (14:17 10.05.2024.)

import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.function.Predicate;

@NullMarked
public class KeySerializer extends ScalarSerializer<Key> {

    public static final TypeSerializer<Key> INSTANCE = new KeySerializer();

    protected KeySerializer() {
        super(Key.class);
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public Key deserialize(Type type, Object obj) throws SerializationException {
        return Key.key(String.valueOf(obj));
    }

    @Override
    protected Object serialize(Key item, Predicate<Class<?>> typeSupported) {
        return item.asString();
    }
}
