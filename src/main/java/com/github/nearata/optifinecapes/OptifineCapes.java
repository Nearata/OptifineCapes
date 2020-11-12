package com.github.nearata.optifinecapes;

import java.nio.file.Files;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

@Mod("optifinecapes")
public class OptifineCapes
{
    public static final Logger logger = LogManager.getLogger();
    private final Minecraft mc = Minecraft.getInstance();

    public OptifineCapes()
    {
        MinecraftForge.EVENT_BUS.register(this);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> CapeManager.clearCapes()));
    }

    @SubscribeEvent
    public final void renderPlayer(RenderPlayerEvent.Pre event)
    {
        AbstractClientPlayerEntity acp = (AbstractClientPlayerEntity) event.getPlayer();

        if (acp.hasPlayerInfo() && acp.getLocationCape() == null)
        {
            String username = acp.getDisplayName().getString();
            String uuid = acp.getUniqueID().toString();

            NetworkPlayerInfo playerInfo = ObfuscationReflectionHelper.getPrivateValue(AbstractClientPlayerEntity.class, acp, "field_175157_a");
            Map<Type, ResourceLocation> textures = ObfuscationReflectionHelper.getPrivateValue(NetworkPlayerInfo.class, playerInfo, "field_187107_a");

            ResourceLocation resourceLocation = CapeManager.loadCape(uuid, username);

            textures.put(Type.CAPE, resourceLocation);
            textures.put(Type.ELYTRA, resourceLocation);
        }
    }

    @SubscribeEvent
    public final void clientTick(TickEvent.ClientTickEvent event)
    {
        if (mc.world == null && Files.exists(CapeManager.capeDir()))
        {
            CapeManager.clearCapes();
        }
    }
}
