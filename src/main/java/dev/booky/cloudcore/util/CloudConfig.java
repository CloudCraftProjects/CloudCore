package dev.booky.cloudcore.util;
// Created by booky10 in CloudCore (14:51 14.03.23)

import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.LinkedHashSet;
import java.util.Set;

// Can't be final because of object mapping
@SuppressWarnings("FieldMayBeFinal")
@ConfigSerializable
public final class CloudConfig {

    private LaunchPlates launchPlates = new LaunchPlates();

    @ConfigSerializable
    public static final class LaunchPlates {

        private Set<Block> blocks = new LinkedHashSet<>();
        private Vector launchVelocity = new Vector(0d, 2d, 0d);

        private LaunchPlates() {
        }

        public Set<Block> getBlocks() {
            return this.blocks;
        }

        public Vector getLaunchVelocity() {
            return this.launchVelocity;
        }
    }

    private CloudConfig() {
    }

    public Set<Block> getLaunchPlates() {
        return this.launchPlates.getBlocks();
    }

    public Vector getLaunchVelocity() {
        return this.launchPlates.getLaunchVelocity();
    }
}
