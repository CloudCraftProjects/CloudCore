package dev.booky.cloudcore.commands;
// Created by booky10 in CloudCore (18:12 18.05.2024.)

import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.kyori.adventure.text.Component;

import static io.papermc.paper.command.brigadier.MessageComponentSerializer.message;

public final class CommandUtil {

    private CommandUtil() {
    }

    public static Message asMessage(Component component) {
        return message().serialize(component);
    }

    public static Component asComponent(Message message) {
        return message().deserialize(message);
    }

    public static SimpleCommandExceptionType buildExceptionType(Component message) {
        return new SimpleCommandExceptionType(asMessage(message));
    }

    public static CommandSyntaxException buildException(Component message) {
        return buildExceptionType(message).create();
    }
}
