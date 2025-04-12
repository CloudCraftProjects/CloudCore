package dev.booky.cloudcore.util;
// Created by booky10 in CloudCore (03:49 15.07.23)

import org.jspecify.annotations.NullMarked;

@NullMarked
record EntityPositionImpl(
        double x, double y, double z,
        float yaw, float pitch
) implements EntityPosition {}
