package dev.booky.cloudcore;
// Created by booky10 in CloudCore (10:35 14.03.23)

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public class CloudCoreMain extends JavaPlugin {

    @Override
    public void onLoad() {
        new Metrics(this, 17949);
    }
}
