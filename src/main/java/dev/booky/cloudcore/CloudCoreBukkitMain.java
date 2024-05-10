package dev.booky.cloudcore;
// Created by booky10 in CloudCore (10:35 14.03.23)

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public class CloudCoreBukkitMain extends JavaPlugin {

    public CloudCoreBukkitMain() {
        try {
            Class.forName("io.papermc.paper.configuration.Configuration");
        } catch (ClassNotFoundException exception) {
            throw new RuntimeException("Please use paper for this plugin to function! Download it at https://papermc.io/.");
        }
    }

    @Override
    public void onLoad() {
        new Metrics(this, 17949);
    }
}
