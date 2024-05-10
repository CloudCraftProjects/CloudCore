package dev.booky.cloudcore.config;
// Created by booky10 in CloudCore (13:15 18.07.23)

import net.kyori.adventure.translation.Translator;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Locale;
import java.util.function.Predicate;

public final class LocaleSerializer extends ScalarSerializer<Locale> {

    public static final TypeSerializer<Locale> INSTANCE = new LocaleSerializer();

    private LocaleSerializer() {
        super(Locale.class);
    }

    @Override
    public Locale deserialize(Type type, Object obj) throws SerializationException {
        return Translator.parseLocale(String.valueOf(obj));
    }

    @Override
    protected Object serialize(Locale item, Predicate<Class<?>> typeSupported) {
        return String.valueOf(item);
    }
}
