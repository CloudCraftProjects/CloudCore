package dev.booky.cloudcore.config;
// Created by booky10 in CloudCore (14:08 10.05.2024.)

import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.Locale;

import static net.kyori.adventure.util.Services.serviceWithFallback;

interface IConfigurateLoaderPlatform {

    IConfigurateLoaderPlatform DUMMY = new DummyConfigurateLoaderPlatform();
    IConfigurateLoaderPlatform PLATFORM = serviceWithFallback(IConfigurateLoaderPlatform.class).orElse(DUMMY);

    default TypeSerializerCollection.Builder buildDefaultSerializers(
            TypeSerializerCollection.Builder builder
    ) {
        return builder
                .register(Component.class, ComponentSerializer.INSTANCE)
                .register(new TypeToken<Enum<?>>() {}, EnumSerializer.INSTANCE)
                .register(Locale.class, LocaleSerializer.INSTANCE)
                .register(Key.class, KeySerializer.INSTANCE);
    }

    class DummyConfigurateLoaderPlatform implements IConfigurateLoaderPlatform {}
}
