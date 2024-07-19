package dev.booky.cloudcore.config;
// Created by booky10 in CloudCore (14:11 10.05.2024.)

import dev.booky.cloudcore.util.BlockBBox;
import dev.booky.cloudcore.util.EntityPosition;
import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.FinePosition;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

@ApiStatus.Internal
public class PaperConfigurateLoaderPlatform implements IConfigurateLoaderPlatform {

    @Override
    public TypeSerializerCollection.Builder buildDefaultSerializers(
            TypeSerializerCollection.Builder builder
    ) {
        return IConfigurateLoaderPlatform.super.buildDefaultSerializers(builder
                .register(NamespacedKey.class, NamespacedKeySerializer.INSTANCE)
                .register(BlockBBox.class, BlockBBoxSerializer.INSTANCE)
                .register(BlockPosition.class, BlockPositionSerializer.INSTANCE)
                .register(Location.class, LocationSerializer.INSTANCE)
                .register(EntityPosition.class, EntityPositionSerializer.INSTANCE)
                .register(FinePosition.class, FinePositionSerializer.INSTANCE)
                .register(BlockVector.class, BlockVectorSerializer.INSTANCE)
                .register(Vector.class, VectorSerializer.INSTANCE)
                .register(World.class, WorldSerializer.INSTANCE)
                .register(Block.class, BlockSerializer.INSTANCE)
        );
    }
}
