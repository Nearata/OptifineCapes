package com.github.nearata.optifinecapes;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("optifinecapes")
public class OptifineCapes
{
    public static final Logger logger = LogManager.getLogger();
    private final Minecraft mc = Minecraft.getInstance();
    private static List<String> players = new ArrayList<String>();

    public OptifineCapes()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public final void renderPlayer(RenderPlayerEvent.Pre event)
    {
        final AbstractClientPlayerEntity acp = (AbstractClientPlayerEntity) event.getPlayer();
        final String username = acp.getDisplayName().getString();

        if (acp.hasPlayerInfo() && acp.getLocationCape() == null && !players.contains(username))
        {
            players.add(username);
            CapeManager.loadCape(acp);
        }
    }
    
    @SubscribeEvent
    public final void clientTick(TickEvent.ClientTickEvent event)
    {
        if (mc.world == null && !players.isEmpty())
        {
            players.clear();
        }
    }
}
