package dev.booky.cloudcore;
// Created by booky10 in CloudCore (11:18 14.03.23)

import dev.booky.cloudcore.config.ConfigLoader;
import dev.booky.cloudcore.util.CloudConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

public final class CloudManager {

    private static final Component PREFIX = Component.text() // <c:#755466>[<gradient:#d4e3b8:#b1c9c2>CloudCore</gradient>] </c>
            .append(Component.text("[", TextColor.color(0x755466)))
            .append(Component.text("C", TextColor.color(0xD4E3B8)))
            .append(Component.text("l", TextColor.color(0xD0E0B9)))
            .append(Component.text("o", TextColor.color(0xCCDDBA)))
            .append(Component.text("u", TextColor.color(0xC8DABB)))
            .append(Component.text("d", TextColor.color(0xC4D7BC)))
            .append(Component.text("C", TextColor.color(0xC1D5BE)))
            .append(Component.text("o", TextColor.color(0xBDD2BF)))
            .append(Component.text("r", TextColor.color(0xB9CFC0)))
            .append(Component.text("e", TextColor.color(0xB5CCC1)))
            .append(Component.text("] ", TextColor.color(0x755466)))
            .build().compact();

    private final Plugin plugin;

    private final Map<Player, Long> lastLaunchUse = new WeakHashMap<>();

    private final Path configPath;
    private CloudConfig config;

    public CloudManager(Plugin plugin) {
        this.plugin = plugin;
        this.configPath = plugin.getDataFolder().toPath().resolve("config.yml");
    }

    public void reloadConfig() {
        this.config = ConfigLoader.loadObject(this.configPath, CloudConfig.class);
    }

    public void saveConfig() {
        ConfigLoader.saveObject(this.configPath, this.config);
    }

    public synchronized void updateConfig(Consumer<CloudConfig> consumer) {
        consumer.accept(this.config);
        this.saveConfig();
    }

    public long getLastLaunchUse(Player player) {
        return this.lastLaunchUse.getOrDefault(player, 0L);
    }

    @ApiStatus.Internal
    public void setLastLaunchUse(Player player) {
        this.lastLaunchUse.put(player, System.currentTimeMillis());
    }

    public Component getPrefix() {
        return PREFIX;
    }

    public CloudConfig getConfig() {
        return this.config;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }
}
