package dev.booky.cloudcore.util;
// Created by booky10 in CraftAttack (13:27 05.10.22)

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import java.lang.ref.WeakReference;
import java.util.Objects;

public final class BlockBBox implements Cloneable {

    private final WeakReference<World> world;
    private final int minX, minY, minZ;
    private final int maxX, maxY, maxZ;

    public BlockBBox(Location corner1, Location corner2) {
        this(corner1.getWorld(), corner1.getX(), corner1.getY(), corner1.getZ(),
                corner2.getX(), corner2.getY(), corner2.getZ());
        if (corner1.getWorld() != corner2.getWorld()) {
            throw new IllegalStateException("Worlds mismatch between corners: corner1=" + corner1 + ", corner2=" + corner2);
        }
    }

    public BlockBBox(World world, Vector corner1, Vector corner2) {
        this(world, corner1.getX(), corner1.getY(), corner1.getZ(), corner2.getX(), corner2.getY(), corner2.getZ());
    }

    public BlockBBox(World world, double x1, double y1, double z1, double x2, double y2, double z2) {
        this(world, NumberConversions.floor(x1), NumberConversions.floor(y1), NumberConversions.floor(z1),
                NumberConversions.ceil(x2), NumberConversions.ceil(y2), NumberConversions.ceil(z2));
    }

    public BlockBBox(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
        this.world = new WeakReference<>(world);
        this.minX = Math.min(x1, x2);
        this.minY = Math.min(y1, y2);
        this.minZ = Math.min(z1, z2);
        this.maxX = Math.max(x1, x2);
        this.maxY = Math.max(y1, y2);
        this.maxZ = Math.max(z1, z2);
    }

    public final boolean contains(Block block) {
        return this.contains(block.getWorld(), block.getX(), block.getY(), block.getZ());
    }

    public final boolean contains(World world, int x, int y, int z) {
        if (this.getWorld() != world) {
            return false;
        }
        return this.contains(x, y, z);
    }

    public final boolean contains(int x, int y, int z) {
        return x >= this.minX && x <= this.maxX
                && y >= this.minY && y <= this.maxY
                && z >= this.minZ && z <= this.maxZ;
    }

    public final int getMinX() {
        return this.minX;
    }

    public final int getMinY() {
        return this.minY;
    }

    public final int getMinZ() {
        return this.minZ;
    }

    public final int getMaxX() {
        return this.maxX;
    }

    public final int getMaxY() {
        return this.maxY;
    }

    public final int getMaxZ() {
        return this.maxZ;
    }

    public final Block getMinBlock() {
        return this.getWorld().getBlockAt(this.getMinX(), this.getMinY(), this.getMinZ());
    }

    public final Block getMaxBlock() {
        return this.getWorld().getBlockAt(this.getMaxX(), this.getMaxY(), this.getMaxZ());
    }

    public final BlockVector getMinVec() {
        return new BlockVector(this.minX, this.minY, this.minZ);
    }

    public final BlockVector getMaxVec() {
        return new BlockVector(this.maxX, this.maxY, this.maxZ);
    }

    public final World getWorld() {
        return Objects.requireNonNull(this.world.get(), "World has been unloaded");
    }

    @Override
    public BlockBBox clone() {
        try {
            return (BlockBBox) super.clone();
        } catch (CloneNotSupportedException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BlockBBox that)) return false;
        if (!super.equals(obj)) return false;
        return this.getWorld().equals(that.getWorld());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.getWorld().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "BlockBBox{world=" + this.getWorld() + ",parent={" + super.toString() + "}}";
    }
}
