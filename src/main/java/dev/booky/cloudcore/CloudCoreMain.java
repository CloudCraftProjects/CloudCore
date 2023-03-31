package dev.booky.cloudcore;
// Created by booky10 in CloudCore (10:35 14.03.23)

import dev.booky.cloudcore.commands.LaunchPlateCommand;
import dev.booky.cloudcore.listener.LaunchPlateListener;
import dev.booky.cloudcore.util.TranslationLoader;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class CloudCoreMain extends JavaPlugin {

    private CloudManager manager;
    private TranslationLoader i18n;

    public CloudCoreMain() {
        try {
            Class.forName("io.papermc.paper.configuration.Configuration");
        } catch (ClassNotFoundException exception) {
            throw new RuntimeException("Please use paper for this plugin to function! Download it at https://papermc.io/.");
        }
    }

    @Override
    public void onLoad() {
        new Metrics(this, 17949);
        this.manager = new CloudManager(this);

        this.i18n = new TranslationLoader(this);
        this.i18n.load();
    }

    @Override
    public void onEnable() {
        this.manager.reloadConfig();

        Bukkit.getPluginManager().registerEvents(new LaunchPlateListener(this.manager), this);

        LaunchPlateCommand.register(this.manager);

        Bukkit.getServicesManager().register(CloudManager.class, this.manager, this, ServicePriority.Normal);
    }

    @Override
    public void onDisable() {
        if (this.i18n != null) {
            this.i18n.unload();
        }
    }
}
