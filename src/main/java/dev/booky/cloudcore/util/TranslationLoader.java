package dev.booky.cloudcore.util;
// Created by booky10 in StoneAttack (14:48 28.07.22)

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.Translator;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

public final class TranslationLoader implements Translator {

    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
    private static final Pattern REPLACE_PATTERN = Pattern.compile("\\{(\\d+)}");

    private final NamespacedKey name;
    private final Plugin plugin;

    private final Lock lock = new ReentrantLock();
    private Map<String, Map<Locale, Component>> translations;

    public TranslationLoader(Plugin plugin) {
        this.name = new NamespacedKey(plugin, "i18n");
        this.plugin = plugin;
    }

    public void unload() {
        this.lock.lock();
        try {
            this.unload0();
        } finally {
            this.lock.unlock();
        }
    }

    public void unload0() {
        if (this.translations != null) {
            GlobalTranslator.translator().removeSource(this);
            this.translations = null;
        }
    }

    public void load() {
        this.lock.lock();
        try {
            this.load0();
        } finally {
            this.lock.unlock();
        }
    }

    public void load0() {
        this.unload0();

        this.translations = new HashMap<>();
        this.registerBundle(Locale.ENGLISH);
        this.registerBundle(Locale.GERMAN);

        GlobalTranslator.translator().addSource(this);
    }

    private void registerBundle(Locale locale) {
        String baseName = this.plugin.getPluginMeta().getName().toLowerCase(Locale.ROOT);
        ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale,
                this.plugin.getClass().getClassLoader());

        for (String key : bundle.keySet()) {
            Component component = MiniMessage.miniMessage().deserialize(bundle.getString(key));
            this.translations.computeIfAbsent(key, $ -> new HashMap<>()).put(locale, component);
        }
    }

    @Override
    public @NotNull Key name() {
        return this.name;
    }

    @Override
    public @Nullable MessageFormat translate(@NotNull String key, @NotNull Locale locale) {
        return null;
    }

    @Override
    public @Nullable Component translate(@NotNull TranslatableComponent component, @NotNull Locale locale) {
        // copied from TranslationRegistryImpl#translate
        Map<Locale, Component> translation = this.translations.get(component.key());
        if (translation == null) {
            return null;
        }

        // copied from TranslationRegistryImpl.Translation#translate
        Component translated = translation.get(locale);
        if (translated == null) {
            translated = translation.get(new Locale(locale.getLanguage())); // try without country
            if (translated == null) {
                translated = translation.get(DEFAULT_LOCALE); // try local default locale
                if (translated == null) {
                    return null;
                }
            }
        }

        Component rendered = this.replaceArgs(locale, translated, component.args());
        if (component.hasStyling()) {
            rendered = rendered.applyFallbackStyle(component.style());
        }

        List<Component> rawChildren = component.children();
        if (!rawChildren.isEmpty()) {
            List<Component> children = new ArrayList<>(rendered.children().size() + rawChildren.size());
            children.addAll(rendered.children());

            for (Component child : rawChildren) {
                children.add(GlobalTranslator.renderer().render(child, locale));
            }
            rendered = rendered.children(children);
        }

        return rendered;
    }

    private Component replaceArgs(Locale locale, Component component, List<Component> args) {
        if (args.isEmpty()) {
            return component;
        }

        return component.replaceText(TextReplacementConfig.builder()
                .match(REPLACE_PATTERN)
                .replacement((result, builder) -> {
                    try {
                        int index = Integer.parseInt(result.group(1));
                        if (index < args.size()) {
                            Component arg = GlobalTranslator.render(args.get(index), locale);
                            return builder.content("").append(arg);
                        }
                    } catch (NumberFormatException ignored) {
                    }
                    return null;
                })
                .build());
    }
}
