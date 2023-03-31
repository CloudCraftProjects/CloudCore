package dev.booky.cloudcore.listener;
// Created by booky10 in CloudCore (15:50 31.03.23)

import dev.booky.cloudcore.CloudManager;
import dev.booky.cloudcore.events.LaunchPlateUseEvent;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Map;
import java.util.WeakHashMap;

public class LaunchPlateListener implements Listener {

    private final CloudManager manager;
    private final Map<Player, Long> lastUse = new WeakHashMap<>();

    public LaunchPlateListener(CloudManager manager) {
        this.manager = manager;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL || event.getClickedBlock() == null) {
            return;
        }
        if (!Tag.PRESSURE_PLATES.isTagged(event.getClickedBlock().getType())) {
            return;
        }
        if (!this.manager.getConfig().getLaunchPlates().contains(event.getClickedBlock())) {
            return;
        }

        Player player = event.getPlayer();
        LaunchPlateUseEvent launchEvent = new LaunchPlateUseEvent(player,
                event.getClickedBlock(), this.manager.getConfig().getLaunchVelocity());

        if (System.currentTimeMillis() - this.lastUse.getOrDefault(player, 0L) < 1000L) {
            launchEvent.setCancelled(true);
        }

        event.setUseInteractedBlock(Event.Result.DENY);
        if (!launchEvent.callEvent()) {
            return;
        }

        this.lastUse.put(player, System.currentTimeMillis());
        player.setVelocity(player.getVelocity().add(launchEvent.getLaunchVelocity()));
    }
}
