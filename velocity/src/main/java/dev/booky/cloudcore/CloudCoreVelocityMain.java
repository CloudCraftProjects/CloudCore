package dev.booky.cloudcore;
// Created by booky10 in CloudCore (14:56 10.05.2024.)

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import dev.booky.cloudcore.util.BuildConstants;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bstats.velocity.Metrics;

@Plugin(
        id = "cloudcore",
        name = "CloudCore",
        version = BuildConstants.PLUGIN_VERSION,
        authors = "booky10"
)
@Singleton
public class CloudCoreVelocityMain {

    private final Metrics.Factory metrics;

    @Inject
    public CloudCoreVelocityMain(
            Metrics.Factory metrics
    ) {
        this.metrics = metrics;
    }

    @Subscribe
    public void onInit(ProxyInitializeEvent event) {
        this.metrics.make(this, 21856);
    }
}
