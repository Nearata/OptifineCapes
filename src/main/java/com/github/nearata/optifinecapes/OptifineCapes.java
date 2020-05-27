package com.github.nearata.optifinecapes;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

@Mod(
	modid = OptifineCapes.MODID,
	name = OptifineCapes.NAME,
	version = OptifineCapes.VERSION,
	acceptedMinecraftVersions = OptifineCapes.ACCEPTED_MINECRAFT_VERSIONS
)
public class OptifineCapes
{
    public static final String MODID = "optifinecapes";
    public static final String NAME = "Optifine Capes";
    public static final String VERSION = "1.0";
    public static final String ACCEPTED_MINECRAFT_VERSIONS = "[1.12.2]";

    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void renderPlayer(RenderPlayerEvent.Post event)
    {
    	EntityPlayer player = event.getEntityPlayer();
    	String uuid = player.getUniqueID().toString();

    	if (player instanceof AbstractClientPlayer)
    	{
    		AbstractClientPlayer acp = (AbstractClientPlayer) player;

    		if (acp.hasPlayerInfo() && acp.getLocationCape() == null)
    		{
                NetworkPlayerInfo playerInfo = ObfuscationReflectionHelper.getPrivateValue(AbstractClientPlayer.class, acp, "field_175157_a");
                Map<Type, ResourceLocation> textures = ObfuscationReflectionHelper.getPrivateValue(NetworkPlayerInfo.class, playerInfo, "field_187107_a");
    	    	ResourceLocation resourceLocation = new ResourceLocation(MODID, String.format("capes/%s.png", uuid));
    	    	Utils.getDownloadImageCape(resourceLocation, player.getName());
				textures.put(Type.CAPE, resourceLocation);
				textures.put(Type.ELYTRA, resourceLocation);
    		}
    	}
    }
}
