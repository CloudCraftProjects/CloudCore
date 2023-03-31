package dev.booky.cloudcore.listener;
// Created by booky10 in CloudCore (15:50 31.03.23)

import dev.booky.cloudcore.CloudManager;
import dev.booky.cloudcore.events.LaunchPlateUseEvent;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Set;

public class LaunchPlateListener implements Listener {

    private final CloudManager manager;

    public LaunchPlateListener(CloudManager manager) {
        this.manager = manager;
    }

    private boolean isProtected(Block block) {
        Set<Block> plates = this.manager.getConfig().getLaunchPlates();
        if (plates.contains(block)) {
            return true;
        }

        // prevent block breaking below the actual plate
        return plates.contains(block.getRelative(BlockFace.UP));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        if (event.getAction() != Action.PHYSICAL) {
            if (this.isProtected(event.getClickedBlock())) {
                event.setUseInteractedBlock(Event.Result.DENY);
            }
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

        if (System.currentTimeMillis() - this.manager.getLastLaunchUse(player) < 1000L) {
            launchEvent.setCancelled(true);
        }

        event.setUseInteractedBlock(Event.Result.DENY);
        if (!launchEvent.callEvent()) {
            return;
        }

        this.manager.setLastLaunchUse(player);
        player.setVelocity(player.getVelocity().add(launchEvent.getLaunchVelocity()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (this.isProtected(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(this::isProtected);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(this::isProtected);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (this.isProtected(block)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            if (this.isProtected(block)) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
