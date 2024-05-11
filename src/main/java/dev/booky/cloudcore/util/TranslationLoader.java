package dev.booky.cloudcore.util;
// Created by booky10 in StoneAttack (14:48 28.07.22)

import dev.booky.cloudcore.i18n.CloudTranslator;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.translation.Translator;
import net.kyori.adventure.util.TriState;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.Locale;

public final class TranslationLoader implements Translator {

    private final CloudTranslator delegate;

    public TranslationLoader(Plugin plugin) {
        this(plugin, Locale.ENGLISH, Locale.GERMAN);
    }

    public TranslationLoader(Plugin plugin, Locale... locales) {
        NamespacedKey name = new NamespacedKey(plugin, "i18n");
        this.delegate = new CloudTranslator(
                plugin.getClass().getClassLoader(),
                name, locales
        );
    }

    public void unload() {
        this.delegate.unload();
    }

    @Deprecated(forRemoval = true)
    public void unload0() {
        throw new UnsupportedOperationException();
    }

    @Deprecated(forRemoval = true)
    public void load() {
        this.delegate.load();
    }

    public void load0() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Key name() {
        return this.delegate.name();
    }

    @Override
    public @NotNull TriState hasAnyTranslations() {
        return this.delegate.hasAnyTranslations();
    }

    @Override
    public @Nullable MessageFormat translate(@NotNull String key, @NotNull Locale locale) {
        return this.delegate.translate(key, locale);
    }

    @Override
    public @Nullable Component translate(@NotNull TranslatableComponent component, @NotNull Locale locale) {
        return this.delegate.translate(component, locale);
    }
}
