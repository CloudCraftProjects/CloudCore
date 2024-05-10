package dev.booky.cloudcore.i18n;
// Created by booky10 in CloudCore (03:17 10.05.2024.)

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.Translator;
import net.kyori.adventure.util.TriState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CloudTranslator implements Translator {

    private final Key translatorName;
    private final String baseName;

    private final List<Locale> locales;
    final Locale defaultLocale;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private Map<String, Translation> translations;

    public CloudTranslator(Keyed translatorName, Locale... locales) {
        this(translatorName, List.of(locales));
    }

    public CloudTranslator(Keyed translatorName, Collection<Locale> locales) {
        if (locales.isEmpty()) {
            throw new IllegalArgumentException("At least one locale has to be provided");
        }

        this.translatorName = translatorName.key();
        this.baseName = this.translatorName.namespace();

        this.locales = List.copyOf(locales);
        this.defaultLocale = this.locales.getFirst();
    }

    public void unload() {
        this.lock.writeLock().lock();
        try {
            this.unload0();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    private void unload0() {
        if (this.translations != null) {
            GlobalTranslator.translator().removeSource(this);
            this.translations = null;
        }
    }

    public void load() {
        this.lock.writeLock().lock();
        try {
            this.load0();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    private void load0() {
        // remove before adding, just to be safe
        this.unload0();

        // load translations from resource bundles
        Map<Locale, Map<String, String>> rawTranslations = new HashMap<>();
        for (Locale locale : this.locales) {
            Map<String, String> translations = this.processBundle(locale);
            rawTranslations.put(locale, translations);
        }

        this.translations = rawTranslations.entrySet().stream()
                // flatten raw translations
                .flatMap(rawEntry -> rawEntry.getValue().entrySet().stream()
                        .map(entry -> Map.entry(entry.getKey(),
                                Map.entry(rawEntry.getKey(), entry.getValue()))))
                // group by translation key
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> Stream.of(entry.getValue()),
                        Stream::concat
                ))
                .entrySet().stream()
                // create translation objects and collect them
                .map(entry -> Map.entry(entry.getKey(),
                        new Translation(this, entry.getValue())))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));

        // add as global translator source after loading
        GlobalTranslator.translator().addSource(this);
    }

    private Map<String, String> processBundle(Locale locale) {
        return this.processBundle(locale, this.getClass().getClassLoader());
    }

    private Map<String, String> processBundle(Locale locale, ClassLoader classLoader) {
        Map<String, String> translations = new HashMap<>();
        ResourceBundle bundle = ResourceBundle.getBundle(this.baseName, locale, classLoader);
        for (String key : bundle.keySet()) {
            translations.put(key, bundle.getString(key));
        }
        return translations;
    }

    @Override
    public @NotNull Key name() {
        return this.translatorName;
    }

    @Override
    public @NotNull TriState hasAnyTranslations() {
        this.lock.readLock().lock();
        try {
            return TriState.byBoolean(!this.translations.isEmpty());
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public @Nullable MessageFormat translate(@NotNull String key, @NotNull Locale locale) {
        return null; // this translator only supports component translations
    }

    @Override
    public @Nullable Component translate(@NotNull TranslatableComponent component, @NotNull Locale locale) {
        Translation translation;
        this.lock.readLock().lock();
        try {
            translation = this.translations.get(component.key());
        } finally {
            this.lock.readLock().unlock();
        }

        if (translation == null) {
            return null; // nothing found at all
        }
        Translation.Entry entry = translation.lookupEntry(locale);
        if (entry != null) {
            // entry found, replace component arguments & more
            return TranslationRenderer.render(entry, locale, component);
        }
        return null; // no entry found
    }
}
