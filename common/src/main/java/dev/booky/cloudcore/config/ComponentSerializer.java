package dev.booky.cloudcore.config;
// Created by booky10 in CloudCore (04:28 10.05.2024.)

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.function.Predicate;

@NullMarked
public final class ComponentSerializer extends ScalarSerializer<Component> {

    public static final TypeSerializer<Component> INSTANCE = new ComponentSerializer();

    private ComponentSerializer() {
        super(Component.class);
    }

    @Override
    public Component deserialize(Type type, Object obj) throws SerializationException {
        return MiniMessage.miniMessage().deserialize(String.valueOf(obj));
    }

    @Override
    protected Object serialize(Component item, Predicate<Class<?>> typeSupported) {
        // won't output shortcuts such as rainbow tags, not possible
        return MiniMessage.miniMessage().serialize(item);
    }
}
