package dev.booky.cloudcore.commands;
// Created by booky10 in CloudCore (11:18 14.03.23)

import dev.booky.cloudcore.CloudManager;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.LocationType;
import dev.jorel.commandapi.arguments.WorldArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.wrappers.NativeProxyCommandSender;
import io.papermc.paper.entity.TeleportFlag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.time.Duration;

public final class LaunchPlateCommand {

    private LaunchPlateCommand() {
    }

    public static void register(CloudManager manager) {
        CommandAPI.unregister("launchplate", true);

        new CommandTree("launchplate")
                .withPermission("cloudcore.command.launchplate")
                .then(new LiteralArgument("list")
                        .withPermission("cloudcore.command.launchplate.list")
                        .executesNative((NativeProxyCommandSender sender, CommandArguments args) ->
                                list(manager, sender)))
                .then(new LiteralArgument("create")
                        .withPermission("cloudcore.command.launchplate.create")
                        .then(new LocationArgument("block", LocationType.BLOCK_POSITION)
                                .executesNative((NativeProxyCommandSender sender, CommandArguments args) -> {
                                    Location pos = ((Location) args.getOrDefault("block", sender.getLocation()));
                                    create(manager, sender, sender.getWorld(), pos.toVector());
                                }))
                        .then(new WorldArgument("dimension")
                                .then(new LocationArgument("block", LocationType.BLOCK_POSITION)
                                        .executesNative((NativeProxyCommandSender sender, CommandArguments args) -> {
                                            World world = (World) args.getOrDefault("dimension", sender.getWorld());
                                            Location pos = ((Location) args.getOrDefault("block", sender.getLocation()));
                                            create(manager, sender, world, pos.toVector());
                                        })))
                        .executesNative((NativeProxyCommandSender sender, CommandArguments args) ->
                                create(manager, sender, sender.getWorld(), sender.getLocation().toVector())))
                .then(new LiteralArgument("delete")
                        .withPermission("cloudcore.command.launchplate.delete")
                        .then(new LocationArgument("block", LocationType.BLOCK_POSITION)
                                .executesNative((NativeProxyCommandSender sender, CommandArguments args) -> {
                                    Location pos = ((Location) args.getOrDefault("block", sender.getLocation()));
                                    delete(manager, sender, sender.getWorld(), pos.toVector());
                                }))
                        .then(new WorldArgument("dimension")
                                .then(new LocationArgument("block", LocationType.BLOCK_POSITION)
                                        .executesNative((NativeProxyCommandSender sender, CommandArguments args) -> {
                                            World world = (World) args.getOrDefault("dimension", sender.getWorld());
                                            Location pos = ((Location) args.getOrDefault("block", sender.getLocation()));
                                            delete(manager, sender, world, pos.toVector());
                                        })))
                        .executesNative((NativeProxyCommandSender sender, CommandArguments args) ->
                                delete(manager, sender, sender.getWorld(), sender.getLocation().toVector())))
                .register();
    }

    private static void list(CloudManager manager, NativeProxyCommandSender sender) {
        if (manager.getConfig().getLaunchPlates().isEmpty()) {
            sender.sendMessage(manager.getPrefix().append(Component.translatable("cc.command.launch.list.none", NamedTextColor.RED)));
            return;
        }

        TextComponent.Builder builder = Component.text()
                .append(manager.getPrefix())
                .append(Component.translatable("cc.command.launch.list.header", NamedTextColor.YELLOW))
                .append(Component.newline());

        boolean deletePerms = sender.hasPermission("cloudcore.command.launchplate.delete");
        boolean teleportPerms = sender.hasPermission("minecraft.command.teleport");

        boolean firstExec = true;
        for (Block block : manager.getConfig().getLaunchPlates()) {
            if (!firstExec) {
                builder.append(Component.newline());
            } else {
                firstExec = false;
            }

            Component deleteComp;
            if (!deletePerms) {
                deleteComp = Component.empty();
            } else {
                deleteComp = Component.translatable("cc.command.launch.list.delete.button", NamedTextColor.RED)
                        .hoverEvent(Component.translatable("cc.command.launch.list.delete.warning", NamedTextColor.RED))
                        .clickEvent(ClickEvent.callback(clicker -> {
                            if (clicker == sender.getCallee()) {
                                manager.updateConfig(config -> config.getLaunchPlates().remove(block));
                                clicker.sendMessage(manager.getPrefix().append(Component.translatable("cc.command.launch.delete.success", NamedTextColor.YELLOW)));
                            }
                        }, opts -> opts.uses(1).lifetime(Duration.ofMinutes(10))));
            }

            Component entryComp = Component.translatable("cc.command.launch.list.entry", NamedTextColor.YELLOW,
                    Component.text(block.getX()), Component.text(block.getY()), Component.text(block.getZ()),
                    Component.text(block.getWorld().getKey().asString()), deleteComp);

            if (teleportPerms) {
                Location blockLocation = block.getLocation().toCenterLocation();
                entryComp = entryComp
                        .hoverEvent(Component.translatable("cc.command.launch.list.tp-hint", NamedTextColor.AQUA))
                        .clickEvent(ClickEvent.callback(clicker -> {
                            if (clicker == sender.getCallee() && clicker instanceof Player player) {
                                blockLocation.setYaw(player.getLocation().getYaw());
                                blockLocation.setPitch(player.getLocation().getPitch());
                                player.teleport(blockLocation, TeleportFlag.Relative.YAW, TeleportFlag.Relative.PITCH);
                            }
                        }, opts -> opts.uses(ClickCallback.UNLIMITED_USES).lifetime(Duration.ofMinutes(10))));
            }

            builder.append(Component.space()).append(entryComp);
        }

        sender.sendMessage(builder.build());
    }

    private static void create(CloudManager manager, CommandSender sender, World world, Vector pos) {
        Block block = world.getBlockAt(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
        if (manager.getConfig().getLaunchPlates().contains(block)) {
            sender.sendMessage(manager.getPrefix().append(Component.translatable("cc.command.launch.create.already", NamedTextColor.RED)));
            return;
        }
        if (!Tag.PRESSURE_PLATES.isTagged(block.getType())) {
            sender.sendMessage(manager.getPrefix().append(Component.translatable("cc.command.launch.create.not-plate", NamedTextColor.RED)));
            return;
        }

        manager.updateConfig(config -> config.getLaunchPlates().add(block));
        sender.sendMessage(manager.getPrefix().append(Component.translatable("cc.command.launch.create.success", NamedTextColor.YELLOW)));
    }

    private static void delete(CloudManager manager, CommandSender sender, World world, Vector pos) {
        Block block = world.getBlockAt(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
        if (!manager.getConfig().getLaunchPlates().contains(block)) {
            sender.sendMessage(manager.getPrefix().append(Component.translatable("cc.command.launch.delete.not-found", NamedTextColor.RED)));
            return;
        }

        manager.updateConfig(config -> config.getLaunchPlates().remove(block));
        sender.sendMessage(manager.getPrefix().append(Component.translatable("cc.command.launch.delete.success", NamedTextColor.YELLOW)));
    }
}
