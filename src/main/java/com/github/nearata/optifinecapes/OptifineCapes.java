package com.github.nearata.optifinecapes;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

public final class OptifineCapes implements ClientModInitializer
{
    public static final Logger LOGGER = LoggerFactory.getLogger("OptifineCapes");
    public static final Map<UUID, Identifier> ofc = new Object2ObjectOpenHashMap<>();

    @Override
    public void onInitializeClient()
    {
    }
}
