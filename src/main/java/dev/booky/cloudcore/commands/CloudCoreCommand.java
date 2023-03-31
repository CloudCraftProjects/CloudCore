package dev.booky.cloudcore.commands;
// Created by booky10 in CloudCore (23:55 31.03.23)

import dev.booky.cloudcore.CloudManager;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.wrappers.NativeProxyCommandSender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

public final class CloudCoreCommand {

    private CloudCoreCommand() {
    }

    public static void register(CloudManager manager) {
        CommandAPI.unregister("cloudcore", true);
        CommandAPI.unregister("cc", true);

        new CommandTree("cloudcore")
                .withPermission("cloudcore.command.cloudcore")
                .withAliases("cc")
                .then(new LiteralArgument("reload")
                        .withPermission("cloudcore.command.cloudcore.reload")
                        .executesNative((NativeProxyCommandSender sender, CommandArguments args) ->
                                reloadConfig(manager, sender)))
                .register();
    }

    private static void reloadConfig(CloudManager manager, CommandSender sender) {
        manager.reloadConfig();
        sender.sendMessage(manager.getPrefix().append(Component.translatable("cc.command.cloudcore.reload-config", NamedTextColor.YELLOW)));
    }
}
