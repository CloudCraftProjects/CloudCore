package dev.booky.cloudcore.commands;
// Created by booky10 in CloudUtilities (17:36 18.05.2024.)

import com.google.common.net.InetAddresses;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static dev.booky.cloudcore.commands.CommandUtil.buildExceptionType;
import static net.kyori.adventure.text.Component.translatable;

@NullMarked
public final class AddressArgumentType implements CustomArgumentType.Converted<InetAddress, String> {

    private static final SimpleCommandExceptionType ERROR_INVALID_IP =
            buildExceptionType(translatable("commands.banip.invalid"));

    private static final AddressArgumentType INSTANCE = new AddressArgumentType();

    private AddressArgumentType() {
    }

    public static ArgumentType<InetAddress> address() {
        return INSTANCE;
    }

    private static InetAddress getIpAddress(Player player) throws CommandSyntaxException {
        InetSocketAddress address = player.getAddress();
        if (address != null) {
            return address.getAddress();
        }
        // probably connected by unix socket?
        throw ERROR_INVALID_IP.create();
    }

    @Override
    public InetAddress convert(String nativeType) throws CommandSyntaxException {
        if (InetAddresses.isInetAddress(nativeType)) {
            return InetAddresses.forString(nativeType);
        }
        Player player = Bukkit.getPlayerExact(nativeType);
        if (player != null) {
            return getIpAddress(player);
        }
        throw ERROR_INVALID_IP.create();
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return word();
    }
}
