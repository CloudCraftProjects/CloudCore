package dev.booky.cloudcore.config;
// Created by booky10 in CloudCore (13:15 18.07.23)

import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.Locale;
import java.util.function.Predicate;

public class LocaleSerializer extends ScalarSerializer<Locale> {

    public static final LocaleSerializer INSTANCE = new LocaleSerializer();

    private LocaleSerializer() {
        super(Locale.class);
    }

    @Override
    public Locale deserialize(Type type, Object obj) throws SerializationException {
        String objStr = String.valueOf(obj);
        int separatorIndex = objStr.indexOf('_');
        if (separatorIndex == -1) {
            return new Locale(objStr);
        }

        if (separatorIndex != objStr.lastIndexOf('_')) {
            throw new UnsupportedOperationException("Unsupported locale string: " + objStr);
        }

        String lang = objStr.substring(0, separatorIndex);
        String country = objStr.substring(separatorIndex + 1);
        return new Locale(lang, country);
    }

    @Override
    protected Object serialize(Locale item, Predicate<Class<?>> typeSupported) {
        return String.valueOf(item);
    }
}
