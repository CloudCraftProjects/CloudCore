package dev.booky.cloudcore.util;
// Created by booky10 in CloudCore (03:48 15.07.23)

import io.papermc.paper.math.FinePosition;
import io.papermc.paper.math.Position;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

@NullMarked
@ApiStatus.Experimental
public sealed interface EntityPosition extends FinePosition permits EntityPositionImpl {

    EntityPosition ENTITY_ZERO = new EntityPositionImpl(0d, 0d, 0d, 0f, 0f);

    float yaw();

    float pitch();

    default EntityPosition rotate(float yaw, float pitch) {
        return yaw == 0f && pitch == 0f ? this
                : new EntityPositionImpl(this.x(), this.y(), this.z(), this.yaw() + yaw, this.pitch() + pitch);
    }

    @Override
    default EntityPosition offset(int x, int y, int z) {
        return this.offset((double) x, (double) y, (double) z);
    }

    @Override
    default EntityPosition offset(double x, double y, double z) {
        return x == 0d && y == 0d && z == 0d ? this
                : new EntityPositionImpl(this.x() + x, this.y() + y, this.z() + z, this.yaw(), this.pitch());
    }

    @Override
    default EntityPosition toCenter() {
        return new EntityPositionImpl(this.blockX() + 0.5d, this.blockY() + 0.5d, this.blockZ() + 0.5d, this.yaw(), this.pitch());
    }

    @Override
    default Location toLocation(World world) {
        return new Location(world, this.x(), this.y(), this.z(), this.yaw(), this.pitch());
    }

    static EntityPosition entity(Location location) {
        return entity(location, location.getYaw(), location.getPitch());
    }

    static EntityPosition entity(Position position) {
        return entity(position, 0f, 0f);
    }

    static EntityPosition entity(Position position, float yaw, float pitch) {
        return entity(position.x(), position.y(), position.z(), yaw, pitch);
    }

    static EntityPosition entity(double x, double y, double z) {
        return entity(x, y, z, 0f, 0f);
    }

    static EntityPosition entity(double x, double y, double z, float yaw, float pitch) {
        return new EntityPositionImpl(x, y, z, yaw, pitch);
    }
}
