package com.github.nearata.optifinecapes;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
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
    public void renderPlayer(RenderPlayerEvent.Pre event)
    {
        final AbstractClientPlayerEntity acp = (AbstractClientPlayerEntity) event.getPlayer();
        final String username = acp.getName().getString();

        if (acp.isCapeLoaded() && acp.getCloakTextureLocation() == null && !players.contains(username))
        {
            this.players.add(username);
            Map<Type, ResourceLocation> textureLocations = acp.getPlayerInfo().textureLocations;

            Util.backgroundExecutor().execute(() -> {
                try
                {
                    final URL url = new URL(String.format("http://s.optifine.net/capes/%s.png", username));
                    final NativeImage nativeImage = NativeImage.read(url.openStream());
                    final DynamicTexture dynamictexture = new DynamicTexture(this.parseCape(nativeImage));
                    final ResourceLocation resourcelocation = mc.getTextureManager().register("optifinecapes/", dynamictexture);

                    textureLocations.put(Type.CAPE, resourcelocation);
                    textureLocations.put(Type.ELYTRA, resourcelocation);
                }
                catch (IOException e)
                {
                    // no cape
                }
            });
        }
    }

    @SubscribeEvent
    public void clientTick(ClientTickEvent event)
    {
        if (mc.player == null && !this.players.isEmpty())
        {
            this.players.clear();
        }
    }

    private NativeImage parseCape(NativeImage nativeImageIn)
    {
        int imageWidth = 64;
        int imageHeight = 32;
        int imageSrcWidth = nativeImageIn.getWidth();
        int imageSrcHeight = nativeImageIn.getHeight();

        while (imageWidth < imageSrcWidth || imageHeight < imageSrcHeight)
        {
            imageWidth *= 2;
            imageHeight *= 2;
        }

        NativeImage nativeImage = new NativeImage(imageWidth, imageHeight, true);

        for (int x = 0; x < imageSrcWidth; x++)
        {
            for (int y = 0; y < imageSrcHeight; y++)
            {
                nativeImage.setPixelRGBA(x, y, nativeImageIn.getPixelRGBA(x, y));
            }
        }

        nativeImageIn.close();

        return nativeImage;
    }
}
