package dev.booky.cloudcore.i18n;
// Created by booky10 in CloudCore (17:27 11.05.2024.)

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslationArgument;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.util.Index;
import net.kyori.adventure.util.TriState;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import static net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText;

final class AdvancedTagFormatter implements TagResolver {

    private final List<TranslationArgument> args;

    public AdvancedTagFormatter(List<TranslationArgument> args) {
        this.args = args;
    }

    private @Nullable TranslationArgument resolveArg(String name) {
        if (name.isEmpty() || !Character.isDigit(name.charAt(0))) {
            return null;
        }
        try {
            int id = Integer.parseInt(name);
            if (id < 0 || id >= this.args.size()) {
                return null;
            }
            return this.args.get(id);
        } catch (NumberFormatException ignored) {
            return null; // invalid integer
        }
    }

    @Override
    public @Nullable Tag resolve(String name, ArgumentQueue args, Context ctx) throws ParsingException {
        TranslationArgument arg = this.resolveArg(name);
        if (arg == null) {
            return null;
        }
        if (!args.hasNext()) {
            return DynamicFormatting.NONE.format(args, ctx, arg);
        }
        String dynamicFormattingName = args.pop().value();
        DynamicFormatting dynamicFormatting = DynamicFormatting.INDEX.valueOr(
            dynamicFormattingName, DynamicFormatting.NONE);
        return dynamicFormatting.format(args, ctx, arg);
    }

    @Override
    public boolean has(String name) {
        if (name.isEmpty() || !Character.isDigit(name.charAt(0))) {
            return false;
        }
        try {
            int id = Integer.parseInt(name);
            if (id >= 0 && id < this.args.size()) {
                return true;
            }
        } catch (NumberFormatException ignored) {
        }
        return false;
    }

    private enum DynamicFormatting {

        BOOLEAN("bool") {
            private TriState parse(String string) {
                return switch (string) {
                    case "0", "false", "off" -> TriState.FALSE;
                    case "1", "true", "on" -> TriState.TRUE;
                    default -> TriState.NOT_SET;
                };
            }

            @Override
            public Tag format(ArgumentQueue args, Context ctx, TranslationArgument arg) {
                TriState state = switch (arg.value()) {
                    case Boolean bool -> TriState.byBoolean(bool);
                    case String str -> this.parse(str);
                    default -> this.parse(plainText().serialize(arg.asComponent()));
                };
                int argOffset = switch (state) {
                    case TRUE -> 0;
                    case FALSE -> 1;
                    case NOT_SET -> 2;
                };

                String value;
                do {
                    value = args.popOr(
                        () -> "No argument found for " + state).value();
                } while (argOffset-- > 0);
                return Tag.inserting(ctx.deserialize(value));
            }
        },
        NUMBER("num") {
            private static NumberFormat getDecimalFormat(ArgumentQueue args) {
                // copied from Formatter#number
                if (!args.hasNext()) {
                    return DecimalFormat.getInstance();
                }

                String localeStr = args.pop().value();
                if (args.hasNext()) {
                    String format = args.pop().value();
                    Locale locale = Locale.forLanguageTag(localeStr);
                    DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
                    return new DecimalFormat(format, symbols);
                }

                return localeStr.indexOf('.') < 0
                    ? DecimalFormat.getInstance(Locale.forLanguageTag(localeStr))
                    : new DecimalFormat(localeStr, DecimalFormatSymbols.getInstance());
            }

            @Override
            public Tag format(ArgumentQueue args, Context ctx, TranslationArgument arg) {
                String formatted = getDecimalFormat(args).format(asNumber(arg));
                Component component = ctx.deserialize(formatted);
                return Tag.inserting(component);
            }
        },
        NONE("none") {
            @Override
            public Tag format(ArgumentQueue args, Context ctx, TranslationArgument arg) {
                return Tag.selfClosingInserting(arg);
            }
        };

        private static final Index<String, DynamicFormatting> INDEX =
            Index.create(DynamicFormatting::getId, values());

        private final String id;

        DynamicFormatting(String id) {
            this.id = id;
        }

        protected static Number asNumber(TranslationArgument arg) {
            if (arg.value() instanceof Number) {
                return (Number) arg.value();
            }
            String string = arg.value() instanceof Component
                ? plainText().serialize((Component) arg.value())
                : String.valueOf(arg.value());
            return Double.parseDouble(string);
        }

        public abstract Tag format(ArgumentQueue args, Context ctx, TranslationArgument arg);

        public String getId() {
            return this.id;
        }
    }
}
