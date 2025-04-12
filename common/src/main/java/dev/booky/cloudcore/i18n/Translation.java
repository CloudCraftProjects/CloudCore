package dev.booky.cloudcore.i18n;
// Created by booky10 in CloudCore (03:26 10.05.2024.)

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.TranslationArgument;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

@NullMarked
final class Translation {

    private final CloudTranslator translator;
    private final Map<Locale, Entry> entries;

    public Translation(CloudTranslator translator, Stream<Map.Entry<Locale, String>> strings) {
        this.translator = translator;
        this.entries = strings
                .map(entry -> Map.entry(entry.getKey(), Entry.parse(entry.getValue())))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public @Nullable Entry lookupEntry(Locale locale) {
        Map<Locale, Entry> entries = this.entries;

        // copied from TranslationRegistryImpl.Translation#translate
        Entry directTranslated = entries.get(locale);
        if (directTranslated != null) {
            return directTranslated;
        }

        // try without country
        Locale langLocale = Locale.of(locale.getLanguage());
        Entry translated = entries.get(langLocale);
        if (translated != null) {
            return translated;
        }

        // fallback to default locale
        return entries.get(this.translator.defaultLocale);
    }

    record Entry(
            String string,
            StringType type,
            Component component
    ) {

        private static final char LEGACY_COLOR_CHAR = '§';
        private static final char MINIMESSAGE_TAG_START = '<';
        private static final char MINIMESSAGE_TAG_END = '>';

        public Entry(String rawString, StringType stringType) {
            this(rawString, stringType, stringType.parse(rawString));
        }

        public static Entry parse(String string) {
            // try to assume which format the string is written in
            if (string.indexOf(LEGACY_COLOR_CHAR) != -1) {
                return new Entry(string, StringType.LEGACY);
            } else if (string.indexOf(MINIMESSAGE_TAG_START) != -1
                    && string.indexOf(MINIMESSAGE_TAG_END) != -1) {
                return new Entry(string, StringType.MINI_MESSAGE);
            } else {
                return new Entry(string, StringType.LITERAL);
            }
        }
    }

    enum StringType {

        MINI_MESSAGE {
            @Override
            public Component parse(String input) {
                return miniMessage().deserialize(input);
            }

            @Override
            public Component replaceArgs(Entry entry, List<TranslationArgument> args) {
                // use a custom tag resolver for minimessage
                // this means {0}, {1}, etc. will not work here for arguments,
                // instead use <0>, <1>, etc.
                return miniMessage().deserialize(entry.string(),
                        new AdvancedTagFormatter(args));
            }
        },
        LEGACY {
            @Override
            public Component parse(String input) {
                return LegacyComponentSerializer.legacySection().deserialize(input);
            }
        },
        LITERAL {
            @Override
            public Component parse(String input) {
                return Component.text(input);
            }
        };

        // both {0}, {1}, etc. and <0>, <1>, etc. will work by default
        private static final Pattern REPLACE_PATTERN = Pattern.compile("[<{](\\d+)[>}]");

        public abstract Component parse(String input);

        public Component replaceArgs(Entry entry, List<TranslationArgument> args) {
            // use regex to replace arguments as fallback translation
            return entry.component().replaceText(TextReplacementConfig.builder()
                    .match(REPLACE_PATTERN)
                    .replacement((result, builder) -> {
                        int index = Integer.parseInt(result.group(1));
                        if (index >= 0 && index < args.size()) {
                            return builder.content("").append(args.get(index));
                        }
                        return null;
                    })
                    .build());
        }
    }
}
