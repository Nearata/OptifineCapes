package com.github.nearata.optifinecapes;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("optifinecapes")
public final class OptifineCapes
{
    private final Minecraft mc = Minecraft.getInstance();
    private List<String> players = new ArrayList<>();

    public OptifineCapes()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public final void renderPlayerPre(final RenderPlayerEvent.Pre event)
    {
        final PlayerEntity player = event.getPlayer();
        final AbstractClientPlayerEntity acp = (AbstractClientPlayerEntity) player;
        final String name = player.getDisplayName().getString();

        if (acp.hasPlayerInfo() && acp.getLocationCape() == null && !players.contains(name))
        {
            this.players.add(name);
            this.loadCape(acp);
        }
    }

    @SubscribeEvent
    public final void clientTick(final ClientTickEvent event)
    {
        if (mc.world == null && !this.players.isEmpty())
        {
            this.players.clear();
        }
    }

    private final void loadCape(final AbstractClientPlayerEntity acp)
    {
        final String name = acp.getName().getString();
        final NetworkPlayerInfo playerInfo = acp.getPlayerInfo();

        Util.getServerExecutor().execute(() -> {
            try
            {
                final URL url = new URL(String.format("http://s.optifine.net/capes/%s.png", name));
                final NativeImage nativeImage = NativeImage.read(url.openStream());
                final DynamicTexture dynamicTexture = new DynamicTexture(this.parseCape(nativeImage));
                final ResourceLocation resourceLocation = mc.getTextureManager().getDynamicTextureLocation("optifinecapes/", dynamicTexture);
                
                mc.getTextureManager().loadTexture(resourceLocation, dynamicTexture);

                playerInfo.playerTextures.put(Type.CAPE, resourceLocation);
                playerInfo.playerTextures.put(Type.ELYTRA, resourceLocation);
            }
            catch (final IOException e)
            {
                // no cape
            }
        });
    }

    private final NativeImage parseCape(final NativeImage nativeImageIn)
    {
        int imageWidth = 64;
        int imageHeight = 32;
        int imageSrcWidth = nativeImageIn.getWidth();

        for (int srcHeight = nativeImageIn.getHeight(); imageWidth < imageSrcWidth || imageHeight < srcHeight; imageHeight *= 2)
        {
            imageWidth *= 2;
        }

        NativeImage nativeImage = new NativeImage(imageWidth, imageHeight, true);
        nativeImage.copyImageData(nativeImageIn);
        nativeImageIn.close();

        return nativeImage;
    }
}
