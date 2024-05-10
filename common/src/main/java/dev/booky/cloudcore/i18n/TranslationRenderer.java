package dev.booky.cloudcore.i18n;
// Created by booky10 in CloudCore (03:49 10.05.2024.)

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.translation.GlobalTranslator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final class TranslationRenderer {

    private TranslationRenderer() {
    }

    public static Component render(Translation.Entry translated, Locale locale, TranslatableComponent source) {
        Component rendered = render0(translated, locale, source);
        if (translated.type() == Translation.StringType.MINI_MESSAGE) {
            // pass this component through the renderer again,
            // it is possible to use recursive translations with minimessage
            return GlobalTranslator.render(rendered, locale);
        }
        return rendered; // recursive rendering not possible here
    }

    private static Component render0(Translation.Entry translated, Locale locale, TranslatableComponent source) {
        Component replaced;
        if (source.arguments().isEmpty()) {
            replaced = translated.component(); // no args, return cache
        } else {
            replaced = translated.type().replaceArgs(translated, source.arguments());
        }

        // as we are using component translations, we'll have to
        // apply fallback styles and children rendering manually

        // apply parent style as fallback
        Component styled;
        if (!source.hasStyling()) {
            styled = replaced;
        } else {
            Component preStyled = replaced.applyFallbackStyle(source.style());
            HoverEvent<?> hoverEvent = preStyled.hoverEvent();
            styled = hoverEvent == null || !(hoverEvent.value() instanceof Component text) ? preStyled
                    : preStyled.hoverEvent(GlobalTranslator.render(text, locale));
        }

        // render children of parent component
        List<Component> rawChildren = source.children();
        if (!rawChildren.isEmpty()) {
            // don't discard children of already translated component
            List<Component> children = new ArrayList<>(styled.children().size() + rawChildren.size());
            children.addAll(styled.children());

            for (Component child : rawChildren) {
                children.add(GlobalTranslator.render(child, locale));
            }
            return styled.children(children);
        } else {
            return styled;
        }
    }
}
